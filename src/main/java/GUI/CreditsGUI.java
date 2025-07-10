package GUI;

import util.Mixer;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.CardLayout;

/**
 * classe che rappresenta il pannello della GUI per la schermata dei riconoscimenti (credits).
 * mostra i membri del team di sviluppo, una breve descrizione del progetto
 * e un pulsante per tornare al menu principale.
 */
public class CreditsGUI extends JPanel {

    // costanti per uno stile grafico coerente in tutto il pannello
    private static final Color COLORE_SFONDO = new Color(10, 15, 45); 
    private static final Color COLORE_TESTO = new Color(240, 248, 255);
    private static final Color COLORE_ACCENTO = new Color(0, 246, 255);
    private static final Font FONT_TITOLO = new Font("Dialog", Font.BOLD, 34);
    
    private static JButton soundButton;

    /**
     * costruttore della classe CreditsGUI.
     * invoca il metodo per inizializzare e assemblare tutti i componenti grafici.
     */
    public CreditsGUI() {
        initComponents();
    }
    
    /**
     * inizializza e assembla tutti i componenti della GUI.
     * imposta il layout manager, crea il pulsante "indietro", il titolo,
     * il pannello degli sviluppatori e il box di testo, e li posiziona
     * usando GridBagLayout per un controllo preciso e un centraggio efficace.
     */
    private void initComponents() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());
        setBackground(COLORE_SFONDO);

        // pulsante "indietro"
        JButton goBackButton = createStyledBackButton();
        GridBagConstraints backGBC = new GridBagConstraints();
        backGBC.anchor = GridBagConstraints.NORTHWEST;
        backGBC.insets = new Insets(15, 15, 15, 15);
        backGBC.weightx = 1.0; 
        backGBC.weighty = 1.0; 
        add(goBackButton, backGBC);

        // pulsante suono in alto a destra
        soundButton = createSoundButton();
        GridBagConstraints soundGBC = new GridBagConstraints();
        soundGBC.anchor = GridBagConstraints.NORTHEAST;
        soundGBC.insets = new Insets(15, 15, 15, 15);
        add(soundButton, soundGBC);
        
        // contenitore per gli elementi centrali
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        // titolo
        JLabel titleLabel = new JLabel("Il Team di Sviluppo", JLabel.CENTER);
        titleLabel.setFont(FONT_TITOLO);
        titleLabel.setForeground(COLORE_ACCENTO);
        gbc.insets = new Insets(0, 0, 20, 0);
        centerPanel.add(titleLabel, gbc);
        
        // pannello icone
        JPanel developersPanel = createDevelopersPanel();
        gbc.insets = new Insets(10, 0, 25, 0); 
        centerPanel.add(developersPanel, gbc);

        // etichetta contenuti
        JLabel contentLabel = createContentLabel();
        gbc.insets = new Insets(20, 50, 20, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(contentLabel, gbc);

        // aggiunge il pannello centrale
        GridBagConstraints centerGBC = new GridBagConstraints();
        centerGBC.gridx = 0;
        centerGBC.gridy = 0;
        centerGBC.gridwidth = 2;
        add(centerPanel, centerGBC);
    }
    
    /**
     * crea e restituisce un pulsante stilizzato per tornare indietro.
     *
     * @return il JButton "indietro" 
     */
    private JButton createStyledBackButton() {
        JButton button = new JButton("← Torna al Menu");
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setForeground(COLORE_TESTO);
        button.setBackground(new Color(25, 35, 80));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_ACCENTO, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.addActionListener(this::goBackActionPerformed);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COLORE_ACCENTO.darker());
                button.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(25, 35, 80));
                button.setForeground(COLORE_TESTO);
            }
        });
        return button;
    }
    
    /**
     * crea e restituisce un pulsante per gestire il suono.
     * 
     * @return il JButton per il suono
     */
    private JButton createSoundButton() {
        String iconText = "🔊";
        JButton button = new JButton(iconText);
        button.setFont(new Font("Dialog", Font.BOLD, 23));
        button.setPreferredSize(new Dimension(35, 35));
        button.setForeground(COLORE_TESTO);
        button.setBackground(new Color(25, 35, 80));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(COLORE_ACCENTO, 1));

        button.addActionListener(_ -> {
            if (Mixer.isRunning()) {
                Mixer.stopClip();
            } else {
                Mixer.startClip();
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COLORE_ACCENTO.darker());
                button.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(25, 35, 80));
                button.setForeground(COLORE_TESTO);
            }
        });
        return button;
    }

    /**
     * crea e restituisce un pannello che contiene i profili dei singoli sviluppatori.
     * dispone i profili orizzontalmente usando un GridBagLayout.
     *
     * @return un JPanel contenente i profili degli sviluppatori
     */
    private JPanel createDevelopersPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 30, 5, 30);

        gbc.gridx = 0;
        panel.add(createDeveloperProfile("src/main/resources/img/alessandra.jpg"), gbc);
        gbc.gridx = 1;
        panel.add(createDeveloperProfile("src/main/resources/img/giorgia.jpeg"), gbc);
        gbc.gridx = 2;
        panel.add(createDeveloperProfile("src/main/resources/img/michele.jpeg"), gbc);

        return panel;
    }

    /**
     * crea un singolo profilo di sviluppatore, composto da un JLabel con un'immagine scalata.
     *
     * @param imagePath il percorso del file dell'immagine dello sviluppatore
     * @return un JLabel che mostra l'icona del profilo, scalata a 120x120
     */
    private JLabel createDeveloperProfile(String imagePath) { 
        ImageIcon originalIcon = new ImageIcon(imagePath);
        JLabel imageLabel;

        if (originalIcon.getIconWidth() > 0) {
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            imageLabel = new JLabel(scaledIcon);
        } else {
            System.err.println("immagine non trovata o non caricabile: " + imagePath);
            // crea un JLabel placeholder
            imageLabel = new JLabel();
            imageLabel.setPreferredSize(new Dimension(120, 120));
            imageLabel.setOpaque(true);
            imageLabel.setBackground(Color.DARK_GRAY);
            imageLabel.setBorder(BorderFactory.createLineBorder(COLORE_ACCENTO));
        }
        
        return imageLabel;
    }
    
    /**
     * crea e restituisce un'etichetta contenente il testo descrittivo del progetto.
     *
     * @return una JLabel con la descrizione del progetto
     */
    private JLabel createContentLabel() {
        String contentHtml = """
            <html>
                <body style='width: 450px; text-align: center; font-family: Dialog, sans-serif; font-size: 16pt;'>
                    <p>
                        <b>"Il Tempio dei Pianeti"</b> è stato realizzato da <br><b>Alessandra Piccolo</b>, <b>Giorgia Sguera</b> e <b>Michele Ricco</b>.
                    </p>
                    <p style='margin-top: 10px;'>
                        Il progetto è nato come esame finale per il corso di <b>Metodi Avanzati di Programmazione</b>, tenuto dal prof. Pierpaolo Basile presso l'Università degli Studi di Bari "Aldo Moro". L'obiettivo era esplorare il paradigma della programmazione a oggetti attraverso lo sviluppo di un'avventura testuale interamente in <b>Java</b>, arricchita da un'interfaccia grafica per un'esperienza più immersiva.
                    </p>
                </body>
            </html>
            """;
        JLabel label = new JLabel(contentHtml);
        label.setForeground(COLORE_TESTO);
        label.setBackground(new Color(17, 24, 68, 220));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_ACCENTO, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return label;
    }

    /**
     * gestisce l'evento di click del pulsante "indietro" per tornare al menu principale.
     *
     * @param event l'evento generato dal click del pulsante
     */
    private void goBackActionPerformed(ActionEvent event) {
        if (getParent() != null && getParent().getLayout() instanceof CardLayout) {
            CardLayout cl = (CardLayout) getParent().getLayout();
            cl.show(getParent(), "MenuGUI"); 
        }
    }
    
    /**
     * metodo statico per impostare il testo del pulsante del suono di questa schermata.
     * 
     * @param text il testo/simbolo da mostrare
     */
    public static void musicButtonSetTextCredits(String text) {
        if (soundButton != null) {
            soundButton.setText(text);
        }
    }
}