import GUI.ManagerGUI;
import backend.DatabaseConnection;
import backend.RestServer;

/**
 * 
 * la classe principale (main class). è l'entry point dell'applicazione.
 * 
 */
public class IlTempioDeiPianeti {

    /**
     * metodo principale che avvia l'applicazione.
     * inizializza l'interfaccia grafica, avvia il server REST e stabilisce la connessione al database.
     *
     * @param args argomenti della riga di comando (non utilizzati)
     */
    public static void main(final String[] args) {
            new ManagerGUI(); 
            try {
                new RestServer().startServer();
                DatabaseConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
        }
    }
}
