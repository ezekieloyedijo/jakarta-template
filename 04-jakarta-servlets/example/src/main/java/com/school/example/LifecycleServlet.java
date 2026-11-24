package com.school.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WATCH THE TERMINAL while using this servlet.
 *
 *  - init()    is printed ONCE, when the app starts (because of <load-on-startup>1</load-on-startup> in web.xml)
 *  - doGet()   is printed EVERY time you open /lifecycle
 *  - destroy() is printed ONCE, when you stop the server with Ctrl + C
 */
// Registered in WEB-INF/web.xml with the URL "/lifecycle" and <load-on-startup>1</load-on-startup>
public class LifecycleServlet extends HttpServlet {

    // A field is SHARED by all requests (there is only ONE servlet object).
    // That's fine for a counter, as long as it's thread-safe: AtomicInteger.
    private final AtomicInteger requestCount = new AtomicInteger();
    private LocalTime bornAt;

    @Override
    public void init() {
        bornAt = LocalTime.now().withNano(0);
        System.out.println(">>> [LIFECYCLE] init(): the servlet object was created and set up at " + bornAt);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int number = requestCount.incrementAndGet();
        String thread = Thread.currentThread().getName();
        System.out.println(">>> [LIFECYCLE] doGet(): serving request #" + number + " on thread " + thread);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Servlet life cycle", """
                <table>
                  <tr><th>This servlet object was created (init) at</th><td>%s</td></tr>
                  <tr><th>Object identity (same number = same object)</th><td>%s</td></tr>
                  <tr><th>Requests served by this ONE object so far</th><td>%d</td></tr>
                  <tr><th>Thread serving THIS request</th><td>%s</td></tr>
                </table>
                <p class="note">Refresh several times. The <b>object identity and init time never change</b>
                (only ONE object exists), but the counter goes up and the thread may change.
                Then press <b>Ctrl + C</b> in the terminal and look for <code>destroy()</code>.</p>
                """.formatted(bornAt, Integer.toHexString(System.identityHashCode(this)), number, thread)));
    }

    @Override
    public void destroy() {
        System.out.println(">>> [LIFECYCLE] destroy(): the app is stopping. Served "
                + requestCount.get() + " requests. Goodbye!");
    }
}
