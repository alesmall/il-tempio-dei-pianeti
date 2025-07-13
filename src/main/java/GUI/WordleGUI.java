package GUI;

import gameplay.WordleGame;
import gameplay.WordleGame.GuessResult;
import gameplay.WordleGame.LetterState;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;

/**
 * classe che gestisce la GUI per il mini-gioco Wordle.
 * contiene la griglia 5x5 e gestisce l'interazione con la logica di gioco contenuta in WordleGame.
 */
public class WordleGUI extends JPanel {
    
    private static final int RIGHE = 5;
    private static final int COLONNE = 5;
    private static final int GAP = 5; // spaziatura tra le caselle
    private static final int LATO_CASELLA = 54; // dimensione di una singola casella
    
    private final LetterBox[][] boxes;
    private WordleGame wordleGame;

    /**
     * costruttore della classe.
     */
    public WordleGUI() {
        this.boxes = new LetterBox[RIGHE][COLONNE];
        initComponents();
    }

    /**
     * inizializza e assembla i componenti grafici.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 15, 45)); 

        JPanel gridPanel = new JPanel(new GridLayout(RIGHE, COLONNE, 5, 5));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // calcolo la dimensione totale della griglia
        int larghezzaGriglia = (COLONNE * LATO_CASELLA) + ((COLONNE - 1) * GAP);
        int altezzaGriglia = (RIGHE * LATO_CASELLA) + ((RIGHE - 1) * GAP);
        Dimension dimensioneGriglia = new Dimension(larghezzaGriglia, altezzaGriglia);
        
        // diciamo al pannello che questa è la sua dimensione minima e preferita
        gridPanel.setPreferredSize(dimensioneGriglia);
        gridPanel.setMinimumSize(dimensioneGriglia);

        for (int r = 0; r < RIGHE; r++) {
            for (int c = 0; c < COLONNE; c++) {
                boxes[r][c] = new LetterBox();
                gridPanel.add(boxes[r][c]);
            }
        }
        
        add(gridPanel);
    }

    /**
     * avvia una nuova sessione di gioco.
     * questo metodo deve essere chiamato quando il pannello Wordle diventa attivo.
     */
    public void startNewGameSession() {
        this.wordleGame = new WordleGame();
        this.wordleGame.initializeGame();
        resetVisuals();
    }

    /**
     * processa il tentativo del giocatore, delegando alla logica di gioco
     * e aggiornando la GUI con il risultato.
     * 
     * @param text il testo inserito dal giocatore
     */
    public void processPlayerGuess(String text) {
        if (wordleGame == null) {
            System.err.println("WordleGame non inizializzato!! Chiamare prima startNewGameSession().");
            return;
        }

        GuessResult result = wordleGame.processGuess(text);

        if (result != null) {
            int currentRow = wordleGame.getCurrentAttempt() - 1;
            updateRow(currentRow, text, result.letterStates);
        }
    }

    private void updateRow(int row, String guess, LetterState[] states) {
        if (row < 0 || row >= RIGHE) return;
        
        String[] letters = guess.toUpperCase().split("");
        for (int c = 0; c < COLONNE; c++) {
            if (c < letters.length) {
                boxes[row][c].updateAppearance(states[c], letters[c]);
            }
        }
    }

    /**
     * resetta la grafica dell'intera griglia allo stato iniziale.
     */
    public void resetVisuals() {
        for (int r = 0; r < RIGHE; r++) {
            for (int c = 0; c < COLONNE; c++) {
                boxes[r][c].reset();
            }
        }
    }
}