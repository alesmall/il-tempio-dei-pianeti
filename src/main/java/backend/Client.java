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
     * invia una richiesta POST con i dati del giocatore (nickname, tempo, finale) al server locale.
     * 
     * @param nickname    nickname del giocatore
     * @param time        tempo di gioco
     * @param finale scelta finale effettuata
     * @throws Exception l'eccezione in caso di errore nella richiesta HTTP
     */
    public void sendPostRequest(final String nickname, final String time, final String finale) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1111/api/data"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("username=" + nickname + "&tempo=" + time + "&finale=" + finale))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (!(response.statusCode() == 200)) {
            System.err.println("errore durante l'invio dei dati: " + response.statusCode());
        }
    }
}
