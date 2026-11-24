package com.school.project;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * THE ASYNC SERVLET: POST /reports/generate (id=1) builds the report in the background.
 *
 *   request thread:  check the id -> startAsync() -> hand the job to a worker -> return (thread is FREE)
 *   worker thread:   build the report (3 s) -> save it -> redirect the browser to the report -> complete()
 *   timeout (10 s):  if the worker is too slow, AsyncListener.onTimeout() answers with a 503 page instead
 */
@WebServlet(urlPatterns = "/reports/generate", asyncSupported = true)
public class GenerateReportServlet extends HttpServlet {

    private static final long TIMEOUT_MILLIS = 10_000;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Report report = EditReportServlet.find(request, response);    // 400 / 404 are answered normally
        if (report == null) return;

        String requestThread = Thread.currentThread().getName();
        String base = request.getContextPath() + "/reports";
        ReportStore store = ReportStore.from(getServletContext());

        AsyncContext async = request.startAsync();
        async.setTimeout(TIMEOUT_MILLIS);
        AtomicBoolean answered = new AtomicBoolean(false);            // the worker or the timeout: only one answers

        async.addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                if (answered.compareAndSet(false, true)) {
                    HttpServletResponse res = (HttpServletResponse) event.getAsyncContext().getResponse();
                    res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    res.setContentType("text/html;charset=UTF-8");
                    res.getWriter().println(Page.page(request, "Taking too long",
                            "<p class='msg error'>The report took more than 10 seconds. Please try again later.</p>"));
                    event.getAsyncContext().complete();
                }
            }

            @Override
            public void onComplete(AsyncEvent event) {
            }

            @Override
            public void onError(AsyncEvent event) {
                System.out.println("[ASYNC] error: " + event.getThrowable());
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
            }
        });

        AppListener.workers(getServletContext()).submit(() -> {
            long start = System.currentTimeMillis();
            String workerThread = Thread.currentThread().getName();
            try {
                List<Integer> scores = ReportBuilder.build(report);   // the slow part
                long took = System.currentTimeMillis() - start;
                boolean saved = store.markReady(report.getId(), scores, took);
                System.out.printf("[ASYNC] report #%d built by %s in %d ms%n", report.getId(), workerThread, took);

                if (answered.compareAndSet(false, true)) {
                    HttpServletResponse res = (HttpServletResponse) async.getResponse();
                    // Post/Redirect/Get still works from the worker thread
                    res.sendRedirect(saved ? base + "/view?id=" + report.getId() + "&msg=generated" : base);
                    async.complete();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();                   // the app is stopping
            } catch (IOException e) {
                System.out.println("[ASYNC] the browser has gone: " + e.getMessage());
                async.complete();
            }
        });

        System.out.println("[ASYNC] " + requestThread + " handed report #" + report.getId()
                + " to a worker and is free again");
    }
}
