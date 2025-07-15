package GUI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * classe della GUI della finestra di aiuto.
 */
public class HelpGUI extends JFrame {
    
    private static final Color COLORE_SFONDO = new Color(23, 27, 64); // leggermente più chiaro del colore di sfondo della schermata principale, 10 15 45

    private static HelpGUI instance;

    /**
     * costruttore della classe.
     */
    private HelpGUI() {
        initComponents();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * restituisce l'unica istanza della finestra di aiuto.
     *
     * @return l'istanza di HelpGUI
     */
    public static HelpGUI getInstance() {
        if (instance == null) {
            instance = new HelpGUI();
        }
        return instance;
    }

    /**
     * inizializza i componenti grafici della finestra.
     */
    private void initComponents() {
        // impostazioni della finestra (JFrame) 
        setTitle("Manuale dell'Esploratore");
        setPreferredSize(new Dimension(565, 485));
        setResizable(false);
        getContentPane().setBackground(COLORE_SFONDO);
        setIconImage(new ImageIcon("src/main/resources/img/icon.png").getImage());
        

        // testo del manuale
        String manualeHtml = """
            <html>
                <head>
                    <style type="text/css">
                        body { font-family: 'Dialog', 'Helvetica', sans-serif; font-size: 13pt; color: rgb(240, 248, 255); }
                        p { margin: 5px 0; }
                        h3 { color: rgb(0, 246, 255); margin-top: 10px; margin-bottom: 0px; font-size: 15pt;}
                        b { color: rgb(142, 223, 223); }
                    </style>
                </head>
                <body>
                    <p>Per muoverti e interagire nel Tempio dei Pianeti, usa i seguenti comandi:</p>
                    
                    <h3>Comandi di Movimento:</h3>
                    <p>
                        ↑ <b>nord</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        ↓ <b>sud</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        → <b>est</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        ← <b>ovest</b> <br>
                    </p>

                    <h3>Comandi di Gioco:</h3>
                    <p>
                        - <b>inventario</b>: mostra gli oggetti nell'inventario.<br>
                        - <b>aiuto</b>: mostra questa lista di comandi.<br>
                        - <b>osserva</b>: descrive la stanza attuale.<br>
                        - <b>osserva [oggetto]</b>: fornisce dettagli su un oggetto.<br>
                        - <b>prendi [oggetto]</b>: raccoglie un oggetto.<br>
                        - <b>lascia [oggetto]</b>: abbandona un oggetto dall'inventario.<br>
                        - <b>usa [oggetto]</b>: utilizza un oggetto.<br>
                        - <b>usa [oggetto1] [oggetto2]</b>: usa il primo oggetto sul secondo.<br>
                        - <b>unisci [oggetto1] [oggetto2]</b>: unisce due oggetti per crearne uno nuovo.
                    </p>
                    <p>
                        <b><i>Note sugli Oggetti Speciali:</i></b> nel Tempio dei Pianeti, alcune reliquie hanno nomi particolari.
                        Ogni <b>lettera</b> ritrovata è identificata dal corpo celeste a cui appartiene, e si presenta con il nome <code><b>LetteraNomeCorpoCeleste</b></code>, come ad esempio <code>LetteraSole</code> o <code>LetteraTerra</code>.
                        Allo stesso modo, ogni <b>cristallo</b> è detto <code><b>CristalloNomeCorpoCeleste</b></code>, come <code>CristalloSaturno</code> o <code>CristalloGiove</code>.<br>
                        Ricorda di usare questi nomi esatti quando interagisci con gli oggetti.
                    </p>
                </body>
            </html>
            """;
        
        // impostazioni dell'area di testo (JLabel) 
        JLabel listaComandiLabel = new JLabel(manualeHtml);
        listaComandiLabel.setOpaque(true);
        listaComandiLabel.setBackground(COLORE_SFONDO);
        listaComandiLabel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); 

        // layout della finestra 
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(listaComandiLabel, GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(listaComandiLabel, GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null); // centra la finestra sullo schermo 
    }
}