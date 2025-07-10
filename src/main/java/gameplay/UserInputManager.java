package gameplay;

/**
 * classe che gestisce l'input dell'utente e richiama il flusso di gioco.
 */
public class UserInputManager {

    private static String currentInput = "";

    /**
     * restituisce l'input corrente e lo resetta.
     *
     * @return l'input corrente
     */
    public static synchronized String getCurrentInput() {
        String temp = currentInput;
        currentInput = "";
        return temp;
    }

    /**
     * imposta l'input corrente.
     *
     * @param currentInput l'input corrente
     */
    public static synchronized void setCurrentInput(String currentInput) {
        UserInputManager.currentInput = currentInput;
    }

    /**
     * controlla se l'input corrente è vuoto.
     *
     * @return true se l'input corrente è vuoto, false altrimenti
     */
    public static synchronized boolean isCurrentInputEmpty() {
        return currentInput.isEmpty();
    }

    /**
     * avvia il listener di input come un nuovo thread.
     * il listener controlla se l'utente ha inserito un nuovo input e richiama il flusso di gioco.
     */
    public static void startInputListener() {
        new Thread(() -> {
            while (true) {
                if (!isCurrentInputEmpty()) {
                    UserInputFlow.gameFlow(getCurrentInput());
                }
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
