package gameplay;

import GUI.GameGUI;
import java.awt.FontMetrics;

/**
 * classe che gestisce il testo da visualizzare nel pannello di testo.
 */
public class OutputDisplayManager {

    private static final FontMetrics fontMetrics = GameGUI.getTextPaneFontMetrics();
    private static final int maxWidth = GameGUI.getTextPaneWidth();

    /**
     * mostra il testo nel pannello.
     *
     * @param text il testo da visualizzare
     */
    public static void displayText(String text) {
        String formattedText = formatText(text);
        GameGUI.displayTextPaneSetText(formattedText);
    }

    /**
     * divide la stringa in parole e aggiunge una nuova riga quando una parola supera la larghezza massima.
     * la larghezza è calcolata usando i FontMetrics (per esempio, Q è più larga di i).
     * se la larghezza di una parola supera la larghezza massima, la parola viene spezzata.
     * non si occupa del wrapping automatico delle righe, ma solo della divisione delle parole troppo lunghe!!
     * il wrapping automatico delle righe è gestito da JTextPane.
     *
     * @param text il testo da formattare
     * @return il testo formattato
     */
    private static String formatText(String text) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");

        for (String word : words) {
            StringBuilder line = new StringBuilder();
            for (char c : word.toCharArray()) {
                if (fontMetrics.stringWidth(line.toString() + c + fontMetrics.charWidth('-')) > maxWidth) {
                    result.append(line).append("-\n");
                    line.setLength(0);
                }
                line.append(c);
            }
            result.append(line).append(" ");
        }

        return result.toString();
    }
}
