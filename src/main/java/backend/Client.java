package backend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * classe che invia al server RESTful i dati di fine partita (nickname, tempo e scelta finale). 
 */
public class Client {

    /**
     * invia una richiesta POST.
     *
     * @throws Exception l'eccezione
     */
    public void sendPostRequest(final String nickname, final String time, final String finalchoice) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1111/api/data"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("username=" + nickname + "&tempo=" + time + "&finale=" + finalchoice))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (!(response.statusCode() == 200)) {
            System.err.println("errore durante l'invio dei dati: " + response.statusCode());
        }
    }
}
