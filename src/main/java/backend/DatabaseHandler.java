package backend;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * classe che estisce le richieste per il gioco, servendo la pagina web
 * e gestendo il salvataggio dei punteggi.
 */
public class DatabaseHandler extends HttpHandler {

    private static final String DB_URL = "jdbc:h2:./src/main/resources/database/il_tempio_dei_pianeti";
    private static final String DB_USER = "aaaa";
    private static final String DB_PASS = "1111";

    /**
     * smista le richieste ai metodi corretti in base al verbo HTTP (GET o POST).
     *
     * @param request  la richiesta
     * @param response la risposta
     * @throws Exception l'eccezione
     */
    @Override
    public void service(final Request request, final Response response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod().toString())) {
            handleGet(request, response);
        } else if ("POST".equalsIgnoreCase(request.getMethod().toString())) {
            handlePost(request, response);
        } else {
            response.setStatus(405, "metodo non permesso");
        }
    }

    /**
     * gestisce la richiesta GET per ottenere i dati dal database e visualizzarli in una pagina HTML.
     *
     * @param request  la richiesta
     * @param response la risposta
     * @throws IOException eccezione di I/O
     */
    private void handleGet(final Request request, final Response response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        // costruisce dinamicamente le righe della tabella della classifica
        StringBuilder classificaRows = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(DatabaseConnection.queryClassifica())) {
            
            while (rs.next()) {
                classificaRows.append("<tr>\n");
                classificaRows.append("<td>").append(rs.getString("USERNAME")).append("</td>\n");
                classificaRows.append("<td>").append(rs.getString("TEMPO")).append("</td>\n");
                classificaRows.append("<td>").append(rs.getString("FINALE")).append("</td>\n");
                classificaRows.append("</tr>\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            classificaRows.append("<tr><td colspan='3'>errore nel caricamento della classifica.</td></tr>");
        }

        // uso un Text Block per l'HTML
        String htmlTemplate = """
            <!DOCTYPE html>
            <html lang="it">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Il Tempio dei Pianeti - Avventura Cosmica</title>
                
                <!-- i font personalizzati vengono caricati da Google, con fallback locali in caso di fallimento -->
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Exo+2:wght@700&family=Lato:wght@400;700&display=swap" rel="stylesheet">
                
                <style>
                    /* CSS */
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    html {
                        scroll-behavior: smooth;
                    }

                    body {
                        font-family: 'Lato', 'Helvetica', 'Arial', sans-serif;
                        background-color: #0a0f2d;
                        background-image: radial-gradient(circle at 10%% 20%%, rgba(30, 39, 93, 0.6) 0%%, transparent 50%%),
                                          radial-gradient(circle at 80%% 90%%, rgba(28, 70, 115, 0.4) 0%%, transparent 50%%);
                        color: #f0f8ff;
                        line-height: 1.8;
                    }

                    .main-container {
                        max-width: 900px;
                        margin: 0 auto;
                        padding: 2rem 1.5rem;
                    }

                    header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding: 1.5rem 2rem;
                        border-bottom: 1px solid rgba(0, 246, 255, 0.2);
                        position: sticky;
                        top: 0;
                        z-index: 1000;
                        background-color: rgba(10, 15, 45, 0.9);
                        backdrop-filter: blur(8px);
                    }

                    header h1 {
                        font-family: 'Exo 2', 'Segoe UI', 'Trebuchet MS', sans-serif;
                        font-size: 2rem;
                        color: #ffffff;
                        text-shadow: 0 0 8px rgba(0, 246, 255, 0.7);
                    }

                    nav ul {
                        list-style: none;
                        display: flex;
                        gap: 2rem;
                    }

                    nav a {
                        text-decoration: none;
                        color: #f0f8ff;
                        font-weight: 700;
                        transition: color 0.3s, text-shadow 0.3s;
                    }

                    nav a:hover {
                        color: #00f6ff;
                        text-shadow: 0 0 10px rgba(0, 246, 255, 0.8);
                    }

                    .card {
                        background: rgba(17, 24, 68, 0.7);
                        border: 1px solid rgba(0, 246, 255, 0.3);
                        border-radius: 15px;
                        padding: 2.5rem;
                        margin-bottom: 3rem;
                        backdrop-filter: blur(5px);
                        box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2), 0 0 15px rgba(0, 246, 255, 0.1) inset;
                    }

                    section {
                        padding-top: 3rem; 
                    }
                    
                    h2 {
                        font-family: 'Exo 2', 'Segoe UI', 'Trebuchet MS', sans-serif;
                        font-size: 2.5rem;
                        color: #00f6ff;
                        text-align: center;
                        margin-bottom: 1.5rem;
                        text-shadow: 0 0 12px rgba(0, 246, 255, 0.6);
                    }
                    
                    h3 {
                        font-family: 'Exo 2', 'Segoe UI', 'Trebuchet MS', sans-serif;
                        color: #f0f8ff;
                        text-align: center;
                        margin: 2rem 0 1rem 0;
                        font-size: 1.5rem;
                    }

                    p {
                        margin-bottom: 1rem;
                        text-align: justify;
                    }
                    
                    b, i {
                        color: #00f6ff;
                        font-weight: 700;
                    }

                    a {
                        color: #00f6ff;
                        font-weight: bold;
                        text-decoration: none;
                        transition: text-shadow 0.3s;
                    }
                    a:hover {
                        text-shadow: 0 0 8px rgba(0, 246, 255, 0.7);
                    }

                    table {
                        width: 100%%;
                        border-collapse: collapse;
                        margin-top: 1.5rem;
                        background: transparent;
                    }

                    th, td {
                        padding: 1rem;
                        text-align: center;
                        border-bottom: 1px solid rgba(0, 246, 255, 0.2);
                    }

                    th {
                        font-family: 'Exo 2', sans-serif;
                        color: #00f6ff;
                        font-size: 1.2rem;
                        text-transform: uppercase;
                    }
                    
                    tr:last-child td {
                        border-bottom: none;
                    }

                    .developers-list {
                        text-align: center;
                    }
                    .developers-list p {
                        text-align: center;
                        font-size: 1.1rem;
                        margin-bottom: 0.5rem;
                    }

                    footer {
                        text-align: center;
                        padding: 2rem;
                        margin-top: 2rem;
                        border-top: 1px solid rgba(0, 246, 255, 0.2);
                        font-size: 0.9rem;
                        color: rgba(240, 248, 255, 0.6);
                    }
                </style>
            </head>
            <body id="top">
                <header>
                    <h1>Il Tempio dei Pianeti 🌌</h1>
                    <nav>
                        <ul>
                            <li><a href="#progetto">Missione</a></li>
                            <li><a href="#mappa">Mappa</a></li>
                            <li><a href="#manuale">Guida</a></li>
                            <li><a href="#classifica">Classifica</a></li>
                            <li><a href="#sviluppatori">Team</a></li>
                        </ul>
                    </nav>
                </header>
            
                <main class="main-container">
            
                    <section id="progetto">
                        <div class="card">
                            <h2>La Missione Cosmica ✨</h2>
                            <p>Il progetto <b>"Il Tempio dei Pianeti"</b> è nato come esame finale per il corso di Metodi Avanzati di Programmazione, tenuto dal prof. Pierpaolo Basile presso l'Università degli Studi di Bari "Aldo Moro". L'obiettivo era esplorare il paradigma della programmazione a oggetti attraverso lo sviluppo di un'avventura testuale interamente in <b>Java</b>, arricchita da un'interfaccia grafica per un'esperienza più immersiva.</p>
                            <p>L'avventura è ambientata in un universo silenzioso, dove le stelle hanno perso la loro luce. Nei panni di un viaggiatore cosmico, dovrai esplorare una misteriosa struttura per recuperare i <b>cristalli planetari</b>, frammenti di memoria di mondi ormai spenti. Solo ricomponendo il bracciale di cristalli potrai riattivare il <b>Nucleo Solare</b> e decidere le sorti dell'intero universo.</p>
                            <p>Le tue scelte contano: il numero di cristalli raccolti e la scoperta di luoghi segreti, come la <b>Stanza della Luna</b>, sbloccheranno finali diversi, da un oblio totale a un risveglio cosmico. </p>
                            <p> <b><i>Il destino delle stelle è nelle tue mani. Parti per l'avventura e risveglia la luce perduta!</i></b></p>
                        </div>
                    </section>
            
                    <section id="mappa">
                        <div class="card">
                            <h2>Atlante dei Mondi Silenti 🗺️</h2>
                            <p>L'universo de "Il Tempio dei Pianeti" è vasto e interconnesso. Per aiutare gli esploratori a non perdersi tra le stelle, ecco una rappresentazione della mappa di gioco completa. <br>Usala per pianificare il tuo viaggio e scoprire tutte le stanze segrete! :)</p>
                            <img src="/mappa.png" alt="Mappa cosmica del Tempio dei Pianeti con tutte le stanze e i loro collegamenti" style="max-width: 80%%; height: auto; display: block; margin: 1.5rem auto 0 auto; border-radius: 10px;">
                        </div>
                    </section>
            
                    <section id="manuale">
                        <div class="card">
                            <h2>Manuale dell'Esploratore 🎮</h2>
                            <p>Per muoverti e interagire nel Tempio dei Pianeti, usa i seguenti comandi testuali:</p>
                            <h3>Comandi di Movimento</h3>
                            <p>
                                - <b>nord</b> / <b>N</b> / <b>avanti</b> <br>
                                - <b>sud</b> / <b>S</b> / <b>indietro</b> <br>
                                - <b>est</b> / <b>E</b> / <b>destra</b> <br>
                                - <b>ovest</b> / <b>O</b> / <b>sinistra</b> </p>
                            <h3>Comandi di Gioco</h3>
                            <p>
                                - <b>inventario</b>: mostra gli oggetti nell'inventario.<br>
                                - <b>aiuto</b>: mostra la lista dei comandi disponibili.<br>
                                - <b>osserva</b>: descrive la stanza attuale.<br>
                                - <b>osserva [oggetto]</b>: fornisce dettagli su un oggetto.<br>
                                - <b>prendi [oggetto]</b>: raccoglie un oggetto.<br>
                                - <b>lascia [oggetto]</b>: abbandona un oggetto dall'inventario.<br>
                                - <b>usa [oggetto]</b>: utilizza un oggetto.<br>
                                - <b>usa [oggetto1] [oggetto2]</b>: usa il primo oggetto sul secondo oggetto.<br>
                                - <b>unisci [oggetto1] [oggetto2]</b>: unisce due oggetti per crearne uno nuovo.
                            </p>
                        </div>
                    </section>
            
                    <section id="classifica">
                        <div class="card">
                            <h2>Albo degli Eroi Cosmici 🏆</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Username</th>
                                        <th>Tempo di Completamento</th>
                                        <th>Finale Ottenuto</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    %s
                                </tbody>
                            </table>
                        </div>
                    </section>
            
                    <section id="sviluppatori">
                        <div class="card">
                            <h2>Il Team di Sviluppo</h2>
                            <h3>Repository del Progetto</h3>
                            <div class="developers-list">
                                <p><a href="https://github.com/alesmall/il-tempio-dei-pianeti.git" target="_blank"> 🚀 GitHub - Il Tempio dei Pianeti</a></p>
                            </div>
                            <h3>Creatori dell'Avventura <3 </h3>
                            <div class="developers-list">
                                <p>👩🏻‍💻 <a href="https://github.com/alesmall" target="_blank">Alessandra Piccolo (alesmall)</a></p>
                                <p>👩🏻‍💻 <a href="https://github.com/giorgiasguera" target="_blank">Giorgia Sguera (giorgiasguera)</a></p>
                                <p>👨🏻‍💻 <a href="https://github.com/mricco19" target="_blank">Michele Ricco (mricco19)</a></p>
                            </div>
                        </div>
                    </section>
                    
                </main>
                
                <footer>
                    <p>© 2025 - Il Tempio dei Pianeti. Un progetto per il corso di Metodi Avanzati di Programmazione.</p>
                </footer>
            
            </body>
            </html>
            """;

        // inserisce le righe dinamiche nel template (al posto di %s) e invia la risposta
        Writer out = response.getWriter();
        out.write(String.format(htmlTemplate, classificaRows.toString()));
    }

    /**
     * gestisce la richiesta POST per inserire i dati della classifica nel database.
     *
     * @param request  la richiesta
     * @param response la risposta
     * @throws IOException eccezione di I/O
     */
    private void handlePost(final Request request, final Response response) throws IOException {
        String username = request.getParameter("username");
        String tempo = request.getParameter("tempo");
        String finale = request.getParameter("finale");

        // prepared statement 
        String sql = "INSERT INTO CLASSIFICA (USERNAME, TEMPO, FINALE) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, tempo);
            pstmt.setString(3, finale);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(500, "Database Error");
            response.getWriter().write("errore durante il salvataggio nel database: " + e.getMessage());
        }
    }
}