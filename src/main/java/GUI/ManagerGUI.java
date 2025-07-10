package GUI;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.CardLayout;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import util.Mixer;

/**
 * classe che gestisce le GUI.
 */
public class ManagerGUI extends JFrame {
    static GameGUI game;

    /**
     * istanzia un nuovo gestore della GUI.
     */
    public ManagerGUI() {
        // imposta le proprietà del frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Il Tempio dei Pianeti");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        try {
            Image icon = ImageIO.read(new File("src/main/resources/img/icon.png"));
            setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // inizializza la musica
        Mixer music = Mixer.getInstance();

        // crea i pannelli delle schermate
        JPanel cards = new JPanel(new CardLayout());
        MenuGUI menu = new MenuGUI();
        CreditsGUI credits = new CreditsGUI();
        ProgressBarGUI progressBar = new ProgressBarGUI();
        game = new GameGUI();

        // aggiunge i pannelli al contenitore cards
        cards.add(menu, "MenuGUI");
        cards.add(progressBar, "ProgressBarGUI");
        cards.add(game, "GameGUI");
        cards.add(credits, "CreditsGUI");

        // avvia il frame
        add(cards);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // avvia la musica
        music.start();
    }

    /**
     * chiude la GUI della schermata di gioco, tornando al menu.
     */
    public static void closeGame() {
        game.goBack();
    }
}
