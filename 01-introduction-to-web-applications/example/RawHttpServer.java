import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * RAW HTTP SERVER: see the HTTP language with your own eyes.
 *
 * This is a server with NO helpers at all. It:
 *   1. Opens "door" (port) 8080 and waits.
 *   2. When a browser connects, it PRINTS the raw request text to the terminal.
 *   3. Writes a raw HTTP response back by hand (status line, headers, blank line, body).
 *
 * HOW TO RUN (inside this folder):   java RawHttpServer.java
 * THEN OPEN:                         http://localhost:8080/
 * WATCH:                             the terminal, where every request is printed
 * STOP:                              Ctrl + C
 */
public class RawHttpServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        // ServerSocket = "open the restaurant door at counter number 8080 and wait for customers"
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is waiting on http://localhost:" + port + "/  (Ctrl + C to stop)");

            while (true) { // a server runs forever, serving one customer after another
                // accept() WAITS here until a browser connects
                try (Socket browser = serverSocket.accept()) {
                    handle(browser);
                } catch (IOException e) {
                    System.out.println("Problem with one request: " + e.getMessage());
                }
            }
        }
    }

    private static void handle(Socket browser) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(browser.getInputStream(), StandardCharsets.UTF_8));

        // ---------- 1. READ THE REQUEST ----------
        String requestLine = in.readLine();              // e.g. "GET /hello?name=Ada HTTP/1.1"
        if (requestLine == null || requestLine.isBlank()) {
            return; // the browser connected but sent nothing
        }

        System.out.println();
        System.out.println("================ A NEW HTTP REQUEST ARRIVED ================");
        System.out.println(requestLine + "        <-- REQUEST LINE (method, path, version)");

        // Headers: one per line, until an EMPTY line
        int contentLength = 0;
        String header;
        while ((header = in.readLine()) != null && !header.isEmpty()) {
            System.out.println(header);
            if (header.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(header.substring("content-length:".length()).trim());
            }
        }
        System.out.println("                         <-- BLANK LINE (headers finished)");

        // Body: only present when Content-Length > 0 (for example, a POST form)
        String body = "";
        if (contentLength > 0) {
            char[] chars = new char[contentLength];
            int read = in.read(chars, 0, contentLength);
            body = new String(chars, 0, Math.max(read, 0));
            System.out.println(body + "        <-- BODY (the POST form data)");
        }
        System.out.println("============================================================");

        // Break the request line into its 3 parts
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String path = parts.length > 1 ? parts[1] : "/";

        // ---------- 2. DECIDE WHAT TO ANSWER ----------
        String status;
        String html;
        if (path.equals("/favicon.ico")) {
            // Browsers ask for the little tab icon all by themselves. We don't have one.
            status = "404 Not Found";
            html = "<h1>404 - no icon here</h1>";
        } else {
            status = "200 OK";
            html = buildPage(method, path, body);
        }

        // ---------- 3. WRITE THE RESPONSE BY HAND ----------
        byte[] bodyBytes = html.getBytes(StandardCharsets.UTF_8);
        String responseHead =
                "HTTP/1.1 " + status + "\r\n" +                        // status line
                "Content-Type: text/html; charset=UTF-8\r\n" +         // header: what the body is
                "Content-Length: " + bodyBytes.length + "\r\n" +       // header: how long the body is
                "Connection: close\r\n" +                              // header: we hang up after this
                "\r\n";                                                // BLANK LINE: headers finished

        OutputStream out = browser.getOutputStream();
        out.write(responseHead.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);                                          // the body
        out.flush();

        System.out.println("Answered with: HTTP/1.1 " + status);
    }

    private static String buildPage(String method, String path, String body) {
        return """
                <!DOCTYPE html>
                <html>
                <head><title>Raw HTTP Server</title>
                <style>body{font-family:sans-serif;max-width:700px;margin:40px auto;line-height:1.5}
                code{background:#eee;padding:2px 6px}form{border:1px solid #ccc;padding:12px;margin:12px 0}</style>
                </head>
                <body>
                  <h1>Hello from a hand-made server!</h1>
                  <p>You sent a <code>%s</code> request for the path <code>%s</code></p>
                  <p>Body you sent: <code>%s</code></p>
                  <p><b>Now look at your terminal</b> to see the full raw request.</p>

                  <form action="/search" method="get">
                    <b>GET form</b>: data goes into the URL<br>
                    <input name="q" placeholder="type something"> <button>Send with GET</button>
                  </form>

                  <form action="/login" method="post">
                    <b>POST form</b>: data goes into the BODY<br>
                    <input name="username" placeholder="username">
                    <input name="password" type="password" placeholder="password">
                    <button>Send with POST</button>
                  </form>
                </body>
                </html>
                """.formatted(method, escape(path), body.isEmpty() ? "(empty)" : escape(body));
    }

    /** Turn < and > into safe text so typed input can't become HTML (more about this in topic 13). */
    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
