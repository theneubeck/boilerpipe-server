import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;

public class ParseServer{
  public static void main(String[] args) throws IOException {
    InetSocketAddress addr = new InetSocketAddress(6666);
    HttpServer server = HttpServer.create(addr, 0);

    server.createContext("/", new MyHandler());

    // server.setExecutor(Executors.newCachedThreadPool());
    // Faster, less CPU
    server.setExecutor(Executors.newFixedThreadPool(20));

    server.start();
    System.out.println("Server is listening on port 6666");
  }
}

class MyHandler implements HttpHandler {
  public void handle(HttpExchange exchange) throws IOException {
      InputStream is = exchange.getRequestBody();
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      String requestMethod = exchange.getRequestMethod();

      Headers responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0);


      OutputStream responseBody = exchange.getResponseBody();
      Headers requestHeaders = exchange.getRequestHeaders();
      String s = "";
      try {
        s = ArticleExtractor.INSTANCE.getText(in);
      } catch (BoilerpipeProcessingException e) {
        System.out.println("Processing failed");
      }

      responseBody.write(s.getBytes());
      responseBody.close();
  }
}

