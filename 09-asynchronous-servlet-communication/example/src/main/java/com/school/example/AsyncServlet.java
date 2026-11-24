package com.school.example;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * THE SOLUTION: the same 3-second job, done ASYNCHRONOUSLY.
 *   1. asyncSupported = true          switch the feature on
 *   2. request.startAsync()           "keep the response open when doGet returns"
 *   3. give the job to a worker thread
 *   4. doGet returns at once          Tomcat's thread is free to serve other users
 *   5. the worker writes the answer and calls complete()   the response is sent
 */
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String requestThread = Thread.currentThread().getName();
        long start = System.currentTimeMillis();

        AsyncContext async = request.startAsync();                   // 2.

        WorkerPool.of(getServletContext()).submit(() -> {             // 3. (runs later, on a worker thread)
            String workerThread = Thread.currentThread().getName();
            WorkerPool.slowJob(3000);
            try {
                HttpServletResponse res = (HttpServletResponse) async.getResponse();
                res.setContentType("text/html;charset=UTF-8");
                res.getWriter().println(WorkerPool.page("Async servlet", """
                        <p class="msg ok">Done after %d ms.</p>
                        <div class="table-wrap"><table>
                          <tr><th>Tomcat's request thread (freed straight away)</th><td><code>%s</code></td></tr>
                          <tr><th>Worker thread (did the slow job)</th><td><code>%s</code></td></tr>
                        </table></div>
                        """.formatted(System.currentTimeMillis() - start, requestThread, workerThread)));
            } catch (IOException e) {
                System.out.println("[ASYNC] could not write: " + e.getMessage());
            } finally {
                async.complete();                                     // 5. send the response now
            }
        });

        System.out.println("[ASYNC] " + requestThread + " is free after "
                + (System.currentTimeMillis() - start) + " ms");     // 4. printed long before the job ends
    }
}
