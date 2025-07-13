package backend;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import java.io.IOException;

/**
 * classe che avvia il server.
 */
public class RestServer {

    /**
     * avvia il server.
     *
     * @throws IOException eccezione di input/output
     */
    public void startServer() throws IOException {
        HttpServer server = HttpServer.createSimpleServer("/", 1111);
        ServerConfiguration config = server.getServerConfiguration();
        config.addHttpHandler(new DatabaseHandler(), "/api/data");

        StaticHttpHandler staticHandler = new StaticHttpHandler("src/main/resources/static/");
        config.addHttpHandler(staticHandler, "/");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
