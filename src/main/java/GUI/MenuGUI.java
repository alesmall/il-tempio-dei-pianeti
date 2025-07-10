package GUI;

import entity.GameManager;
import gameplay.UserInputFlow;
import entity.Game;
import util.Mixer;
import util.TimerManager;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * classe per gestire la GUI del menu principale del gioco.
 */
public class MenuGUI extends JPanel {

    private static final Color COLORE_SFONDO_PULSANTI = new Color(17, 24, 68, 200); 
    private static final Color COLORE_BORDO_PULSANTI = new Color(0, 246, 255);   
    private static final Color COLORE_BORDO_ICONA = new Color(63,84,158); // 0, 180, 200 || 35, 42, 101
    private static final Color COLORE_TESTO_PULSANTI = Color.WHITE;
    private static final Color COLORE_HOVER_PULSANTI = new Color(30, 50, 110, 220); 
    private static final Font FONT_PULSANTI_PRINCIPALI = new Font("Dialog", Font.BOLD, 20);
    private static final Font FONT_PULSANTI_ICONA = new Font("Dialog", Font.BOLD, 25);
    
    private static JButton soundButton;
    private final Image immagineSfondo;
    private final GameManager gameManager = new GameManager();

    /**
     * costruttore della classe.
     */
    public MenuGUI() {
        this.immagineSfondo = new ImageIcon("src/main/resources/img/menuBackground.png").getImage();
        initComponents();
    }

    /**
     * inizializza e assembla i componenti grafici della schermata.
     */
    private void initComponents() {
        setLayout(new BorderLayout(0, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topLeftPanel.setOpaque(false);
        soundButton = createIconButton("🔊", FONT_PULSANTI_ICONA, new Dimension(45, 45));
        soundButton.addActionListener(this::soundActionPerformed);
        JButton helpButton = createIconButton("?", FONT_PULSANTI_ICONA, new Dimension(45, 45));
        helpButton.addActionListener(this::helpActionPerformed);
        topLeftPanel.add(soundButton);
        topLeftPanel.add(helpButton);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        topRightPanel.setOpaque(false);
        JButton siteButton = createIconButton("🌐", FONT_PULSANTI_ICONA, new Dimension(45, 45));
        siteButton.addActionListener(this::siteActionPerformed);
        topRightPanel.add(siteButton);

        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0); 
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton newGameButton = createMainButton("nuova partita", FONT_PULSANTI_PRINCIPALI, new Dimension(250, 50));
        newGameButton.addActionListener(this::newGameActionPerformed);
        
        JButton loadGameButton = createMainButton("carica partita", FONT_PULSANTI_PRINCIPALI, new Dimension(250, 50));
        loadGameButton.addActionListener(this::loadGameActionPerformed);
        
        JButton creditsButton = createMainButton("riconoscimenti", FONT_PULSANTI_PRINCIPALI, new Dimension(250, 50));
        creditsButton.addActionListener(this::creditsActionPerformed);
        
        centerPanel.add(newGameButton, gbc);
        centerPanel.add(loadGameButton, gbc);
        centerPanel.add(creditsButton, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * metodo per creare un pulsante di menu principale (con sfondo semi-trasparente).
     * 
     * @param text il testo del pulsante
     * @param font il font da applicare
     * @param size la dimensione del pulsante
     * @return un JButton con stile personalizzato
     */
    private JButton createMainButton(String text, Font font, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(COLORE_TESTO_PULSANTI);
        button.setBackground(COLORE_SFONDO_PULSANTI);
        button.setBorder(BorderFactory.createLineBorder(COLORE_BORDO_PULSANTI, 2));
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setOpaque(true); 
        button.setContentAreaFilled(true);

        // effetto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLORE_HOVER_PULSANTI);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLORE_SFONDO_PULSANTI);
            }
        });
        return button;
    }

    /**
     * metodo per creare un pulsante icona (con sfondo trasparente di default).
     * 
     * @param text il simbolo del pulsante
     * @param font il font da applicare
     * @param size la dimensione del pulsante
     * @return un JButton con stile personalizzato per icone
     */
    private JButton createIconButton(String text, Font font, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(COLORE_TESTO_PULSANTI);
        button.setBorder(BorderFactory.createLineBorder(COLORE_BORDO_ICONA, 2));
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        // effetto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLORE_HOVER_PULSANTI);
                button.setContentAreaFilled(true);
                button.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
                button.setOpaque(false);
            }
        });
        return button;
    }

    /**
     * sovrascrive il metodo paintComponent per disegnare l'immagine di sfondo.
     * 
     * @param g l'oggetto Graphics usato per disegnare
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (immagineSfondo != null) {
            g.drawImage(immagineSfondo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // metodi per la gestione degli eventi (ActionListeners) 

    /** 
    * gestisce l'azione del pulsante "NUOVA PARTITA".
    * 
    * @param evt l'evento ActionEvent generato dal pulsante
    */
    private void newGameActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) getParent().getLayout();
        Mixer.playEffect("progressbar");
        cl.show(getParent(), "ProgressBarGUI");
        
        ProgressBarGUI progressBarPanel = findProgressBarPanel();
        if (progressBarPanel == null) return;

        progressBarPanel.addPropertyChangeListener(e -> {
            if ("isFinished".equals(e.getPropertyName()) && (Boolean) e.getNewValue()) {
                cl.show(getParent(), "GameGUI");
                TimerManager.getInstance().startTimer("00:00:00");
            }
        });
        
        progressBarPanel.startProgressBar();
        gameManager.createGame();
        new Thread(() -> UserInputFlow.setUpGameFlow(Game.getInstance())).start();
    }
    
    /**
     * gestisce l'azione del pulsante "CARICA PARTITA".
     * 
     * @param evt l'evento ActionEvent generato dal pulsante
     */
    private void loadGameActionPerformed(ActionEvent evt) {
        try {
            gameManager.resetAllItems();
            boolean loaded = gameManager.loadGame();
            if (loaded) {
                CardLayout cl = (CardLayout) getParent().getLayout();
                Mixer.playEffect("progressbar");
                cl.show(getParent(), "ProgressBarGUI");

                ProgressBarGUI progressBarPanel = findProgressBarPanel();
                if (progressBarPanel == null) return;

                progressBarPanel.addPropertyChangeListener(e -> {
                    if ("isFinished".equals(e.getPropertyName()) && (Boolean) e.getNewValue()) {
                        cl.show(getParent(), "GameGUI");
                        TimerManager.getInstance().startTimer(Game.getInstance().getCurrentTime());
                    }
                });
                
                progressBarPanel.startProgressBar();
                new Thread(() -> UserInputFlow.setUpLoadedGameFlow(Game.getInstance())).start();
            } else {
                showMessageDialog(this, "nessun salvataggio trovato :(", "errore", ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessageDialog(this, "errore durante il caricamento del file di salvataggio.", "errore critico", ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * gestisce l'azione del pulsante "RICONOSCIMENTI".
     * 
     * @param evt l'evento ActionEvent generato dal pulsante
     */
    private void creditsActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) getParent().getLayout();
        cl.show(getParent(), "CreditsGUI");
    }

    /** 
     * gestisce l'azione del pulsante del suono.
     * 
     * @param evt l'evento ActionEvent generato dal pulsante
     */
    private void soundActionPerformed(ActionEvent evt) {
        if (Mixer.isRunning()) {
            Mixer.stopClip();
        } else {
            Mixer.startClip();
        }
    }

    /** 
     * gestisce l'azione del pulsante di aiuto.
     * 
     * @param evt l'evento ActionEvent generato dal pulsante
     */
    private void helpActionPerformed(ActionEvent evt) {
        HelpGUI.getInstance().setVisible(true);
    }

    /**
     * gestisce l'azione del pulsante per aprire il sito web.
     * 
     * @param evt l'evento ActionEvent generato dal pulsante
     */
    private void siteActionPerformed(ActionEvent evt) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:1111/api/data"));
            } else {
                showMessageDialog(this, "impossibile aprire il browser :(", "funzione non supportata", ERROR_MESSAGE);
            }
        } catch (URISyntaxException | IOException e) {
            showMessageDialog(this, "errore nell'apertura del sito :(", "errore", ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /** 
     * metodo per trovare il pannello ProgressBarGUI nel contenitore padre.
     * 
     * @return il pannello ProgressBarGUI, o null se non trovato
     */
    private ProgressBarGUI findProgressBarPanel() {
        if (getParent() == null) return null;
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof ProgressBarGUI) {
                return (ProgressBarGUI) comp;
            }
        }
        return null;
    }

    /**
     * metodo statico per impostare il testo del pulsante del suono (es. per mostrare 🔊 o 🔇).
     * 
     * @param text il testo/simbolo da mostrare.
     */
    public static void musicButtonSetTextMenu(String text) {
        if (soundButton != null) {
            soundButton.setText(text);
        }
    }
}