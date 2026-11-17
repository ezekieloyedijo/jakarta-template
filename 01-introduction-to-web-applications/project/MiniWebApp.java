import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * MINI WEB APP: a tiny web application written with ONLY plain Java.
 *
 *   /              -> STATIC home page (the same every time)
 *   /time          -> DYNAMIC page: shows the current time (refresh it!)
 *   /greet?name=X  -> DYNAMIC page: uses YOUR input
 *   anything else  -> 404 page
 *
 * HOW TO RUN (inside this folder):   java MiniWebApp.java
 * THEN OPEN:                         http://localhost:8080/
 *
 * Notice how much "plumbing" we must write ourselves: matching paths, decoding the
 * query string, setting headers, status codes... Jakarta EE (topic 02) does all
 * of this for us.
 */
public class MiniWebApp {

    public static void main(String[] args) throws IOException {
        // Java's built-in mini server. Better than raw sockets, but still very basic.
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // "When a request comes for this path, run this code"
        server.createContext("/", MiniWebApp::home);
        server.createContext("/time", MiniWebApp::time);
        server.createContext("/greet", MiniWebApp::greet);

        server.start();
        System.out.println("Mini Web App running at http://localhost:8080/  (Ctrl + C to stop)");
    }

    // ---------------------------------------------------------------- handlers

    /** STATIC: same HTML for everybody, every time. */
    private static void home(HttpExchange exchange) throws IOException {
        // "/" also catches every unknown path, so we must check it ourselves (more plumbing!)
        if (!exchange.getRequestURI().getPath().equals("/")) {
            send(exchange, 404, page("404 - Not Found",
                    "<p>There is nothing at <code>" + escape(exchange.getRequestURI().getPath()) + "</code></p>"));
            return;
        }
        send(exchange, 200, page("Welcome to my Mini Web App", """
                <p>This home page is <b>static</b>: it never changes.</p>
                <ul>
                  <li><a href="/time">What time is it?</a> (dynamic)</li>
                </ul>
                <form action="/greet" method="get">
                  <label>Your name: <input name="name" required></label>
                  <button type="submit">Greet me</button>
                </form>
                """));
    }

    /** DYNAMIC: the HTML is built fresh at the moment of the request. */
    private static void time(HttpExchange exchange) throws IOException {
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        send(exchange, 200, page("The time on the server is " + now,
                "<p>Refresh the page: this is <b>dynamic</b> content.</p>"));
    }

    /** DYNAMIC: built from the user's input in the query string (?name=...). */
    private static void greet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getRawQuery();   // e.g. "name=Amaka%20Obi"
        String name = getParameter(query, "name");               // we must decode it ourselves!

        if (name == null || name.isBlank()) {
            send(exchange, 400, page("400 - Bad Request", "<p>Please tell me your name.</p>"));
            return;
        }
        send(exchange, 200, page("Hello, " + escape(name) + "!",
                "<p>This page was built just for you.</p>"));
    }

    // ---------------------------------------------------------------- plumbing (the boring part)

    /** Find one value in a query string like "a=1&name=Amaka%20Obi". */
    private static String getParameter(String query, String key) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv[0].equals(key)) {
                return kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            }
        }
        return null;
    }

    /** Write the status code, the Content-Type header, and the body. */
    private static void send(HttpExchange exchange, int status, String html) throws IOException {
        byte[] body = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
        System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + "  ->  " + status);
    }

    /** Wrap some content in a full HTML page. */
    private static String page(String title, String content) {
        return """
                <!DOCTYPE html>
                <html>
                <head><title>Mini Web App</title>
                <style>body{font-family:sans-serif;max-width:600px;margin:40px auto;line-height:1.6}</style>
                </head>
                <body>
                  <h1>%s</h1>
                  %s
                  <p><a href="/">Home</a></p>
                </body>
                </html>
                """.formatted(title, content);
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
