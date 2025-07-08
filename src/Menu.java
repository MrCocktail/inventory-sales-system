import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends JFrame {
    private BackgroundDesktopPane desktopPane;

    public Menu() {
        setTitle("Application de Gestion - UniQ Autoparts");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(Toolkit.getDefaultToolkit().getImage("src\\assets\\icon.png"));
        setLocationRelativeTo(null); // Centrer la fenêtre
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Palette de couleurs
        Color rouge = new Color(183, 28, 28);
        Color grisClair = new Color(240, 240, 240);

        // Zone de travail
        desktopPane = new BackgroundDesktopPane("src\\assets\\background.png");
        setContentPane(desktopPane);
        setLayout(null); 
        setResizable(true);
        setVisible(true);

        // desktopPane = new JDesktopPane();
        // desktopPane.setBackground(grisClair);
        // setContentPane(desktopPane);

        // Barre de menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(rouge);
        menuBar.setForeground(Color.WHITE);
        setJMenuBar(menuBar);

        // Menu Gestion
        JMenu menuGestion = new JMenu("Modules");
        menuGestion.setBackground(grisClair);
        menuGestion.setForeground(Color.WHITE);
        menuBar.add(menuGestion);
        
        JMenuItem itemEmployes = new JMenuItem("Employés");
        itemEmployes.addActionListener(e -> ouvrirModule(new Employees()));
        menuGestion.add(itemEmployes);

        JMenuItem itemClients = new JMenuItem("Clients");
        itemClients.addActionListener(e -> ouvrirModule(new Clients()));
        menuGestion.add(itemClients);

        JMenuItem itemPieces = new JMenuItem("Pièces");
        itemPieces.addActionListener(e -> ouvrirModule(new Parts()));
        menuGestion.add(itemPieces);

        JMenuItem itemSales = new JMenuItem("Ventes");
        itemSales.addActionListener(e -> ouvrirModule(new Sales()));
        menuGestion.add(itemSales);

        JMenuItem itemReports = new JMenuItem("Inventaire");
        itemReports.addActionListener(e -> ouvrirModule(new SalesReport()));
        menuGestion.add(itemReports);
    }

    private void ouvrirModule(JInternalFrame module) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame.getClass() == module.getClass()) {
                try {
                    frame.setSelected(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        desktopPane.add(module);
        module.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Menu().setVisible(true);
        });
    }
}
