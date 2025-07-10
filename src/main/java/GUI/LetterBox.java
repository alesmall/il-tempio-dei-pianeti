package GUI;

import gameplay.WordleGame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * classe che rappresenta una singola casella della griglia di Wordle.
 * è un JLabel personalizzato che cambia colore in base allo stato della lettera.
 */
public class LetterBox extends JLabel {

    // costanti per lo stile delle caselle
    private static final Font FONT_CASELLA = new Font("DialogInput", Font.BOLD, 36);
    private static final Color COLORE_TESTO = Color.WHITE;
    private static final Color COLORE_BORDO = new Color(0, 100, 130);
    private static final Color COLORE_DEFAULT = new Color(10, 25, 60, 200);

    /**
     * costruttore della classe.
     */
    public LetterBox() {
        super("", SwingConstants.CENTER); // letterbox estende jlabel
        setPreferredSize(new Dimension(65, 65));
        setFont(FONT_CASELLA);
        setForeground(COLORE_TESTO);
        setBorder(BorderFactory.createLineBorder(COLORE_BORDO, 2));
        setOpaque(true);
        setBackground(COLORE_DEFAULT);
    }

    /**
     * aggiorna l'aspetto della casella in base al risultato del tentativo.
     * 
     * @param state Lo stato della lettera (CORRECT_SPOT, WRONG_SPOT, NOT_IN_WORD).
     * @param letter La lettera da visualizzare.
     */
    public void updateAppearance(WordleGame.LetterState state, String letter) {
        setText(letter.toUpperCase());
        setBackground(state.getColor());
    }
    
    /**
     * resetta la casella al suo stato iniziale (vuota e con colore di default).
     */
    public void reset() {
        setText("");
        setBackground(COLORE_DEFAULT);
    }
}