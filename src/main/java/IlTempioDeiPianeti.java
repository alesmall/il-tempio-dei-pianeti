import GUI.ManagerGUI;
import backend.DatabaseConnection;
import backend.RestServer;

/**
 * 
 * la classe principale (main class). è l'entry point dell'applicazione.
 * 
 */
public class IlTempioDeiPianeti {

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
