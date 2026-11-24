package com.school.example;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TIMEOUTS and AsyncListener: we only wait 2 seconds, but the job takes 5.
 * The AsyncListener's onTimeout() answers "too slow" and finishes the response.
 * When the job ends later, it sees that someone already answered and writes nothing.
 */
@WebServlet(urlPatterns = "/timeout", asyncSupported = true)
public class TimeoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext async = request.startAsync();
        async.setTimeout(2000);                                       // give up after 2 seconds
        AtomicBoolean answered = new AtomicBoolean(false);            // who answers first: the job or the timeout?

        async.addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                if (answered.compareAndSet(false, true)) {
                    HttpServletResponse res = (HttpServletResponse) event.getAsyncContext().getResponse();
                    res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);         // 503
                    res.setContentType("text/html;charset=UTF-8");
                    res.getWriter().println(WorkerPool.page("Timed out", """
                            <p class="msg error">⏰ The job took longer than 2 seconds, so <code>onTimeout()</code>
                               answered instead (status 503).</p>
                            """));
                    System.out.println("[TIMEOUT] onTimeout answered the user");
                    event.getAsyncContext().complete();
                }
            }

            @Override
            public void onComplete(AsyncEvent event) {
                System.out.println("[TIMEOUT] onComplete: the response has been sent");
            }

            @Override
            public void onError(AsyncEvent event) {
                System.out.println("[TIMEOUT] onError: " + event.getThrowable());
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
                // not used
            }
        });

        WorkerPool.of(getServletContext()).submit(() -> {
            WorkerPool.slowJob(5000);                                 // too slow!
            if (answered.compareAndSet(false, true)) {                // only if the timeout hasn't answered yet
                try {
                    async.getResponse().getWriter().println("Finished in time");
                } catch (IOException ignored) {
                    // the user has gone
                }
                async.complete();
            } else {
                System.out.println("[TIMEOUT] the job finished, but the user already got the timeout page");
            }
        });
    }
}
