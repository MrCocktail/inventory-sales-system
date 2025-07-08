import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class Employees extends JInternalFrame {
    private JPanel contentPane;
    private JTable table;
    private JTextField[] fields = new JTextField[7];
    private JLabel lblTitre;

    public Employees() {
        setTitle("Gestion des Employés");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setSize(750, 500);
        setLocation(30, 30);

        Color rouge = new Color(183, 28, 28);
        Color gris = new Color(238, 238, 238);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(gris);
        setContentPane(contentPane);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 750, 30);
        menuBar.setBackground(rouge);
        menuBar.setForeground(Color.WHITE);

        JMenu menu = new JMenu("Actions");
        menu.setForeground(Color.WHITE);
        menuBar.add(menu);

        JMenuItem miVider = new JMenuItem("Vider les champs");
        JMenuItem miInserer = new JMenuItem("Insertion");
        JMenuItem miRechercher = new JMenuItem("Recherche");
        JMenuItem miModifier = new JMenuItem("Modification");
        JMenuItem miSupprimer = new JMenuItem("Suppression");
        JMenuItem miLister = new JMenuItem("Listing");

        menu.add(miVider);
        menu.add(miInserer);
        menu.add(miRechercher);
        menu.add(miModifier);
        menu.add(miSupprimer);
        menu.add(miLister);
        contentPane.add(menuBar);

        lblTitre = new JLabel("Gestion des Employés", JLabel.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setForeground(rouge);
        lblTitre.setBounds(200, 40, 350, 30);
        contentPane.add(lblTitre);

        // Champs
        String[] noms = {"Identifiant", "Prénom", "Nom", "Poste", "Adresse", "Téléphone", "Salaire"};
        int y = 90;
        for (int i = 0; i < noms.length; i++) {
            JLabel lbl = new JLabel(noms[i]);
            lbl.setBounds(40, y, 100, 20);
            contentPane.add(lbl);
            
            fields[i] = new JTextField();
            fields[i].setBounds(150, y, 200, 22);
            if (i == 0) {
                fields[i].setEditable(false); // Preventing user to edit ID
                fields[i].setBackground(gris);;
            }
            contentPane.add(fields[i]);
            y += 28;
        }

        // Table
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(40, 300, 660, 140);
        contentPane.add(scrollPane);

        // Actions
        miVider.addActionListener(e -> viderChamps());
        miInserer.addActionListener(e -> insererEmploye());
        miRechercher.addActionListener(e -> rechercherEmploye());
        miModifier.addActionListener(e -> modifierEmploye());
        miSupprimer.addActionListener(e -> supprimerEmploye());
        miLister.addActionListener(e -> listerEmployes());
    }

    private void viderChamps() {
        for (JTextField t : fields) t.setText("");
    }

    private boolean verifierChamps() {
    for (int i = 1; i < fields.length; i++) {
        if (fields[i].getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return false;
        }
    }
    return true;
}


    private void insererEmploye() {
        String sql = "INSERT INTO Employe (Prenom, Nom, Poste, Adresse, Phone, Salaire) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            if (!verifierChamps()) return;
            for (int i = 0; i < 6; i++) {
                pst.setString(i + 1, fields[i + 1].getText());
            }
            pst.setDouble(6, Double.parseDouble(fields[6].getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Insertion réussie.");
            viderChamps();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur insertion : " + ex.getMessage());
        }
    }

    private void rechercherEmploye() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID de l'employé");
        String sql = "SELECT * FROM Employe WHERE idEmploye = ?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
            rs = pst.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText(rs.getString(i + 1));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Employé non trouvé.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur recherche : " + ex.getMessage());
        }
    }

    private void modifierEmploye() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID de l'employé pour confirmer");
        String sql = "UPDATE Employe SET Prenom=?, Nom=?, Poste=?, Adresse=?, Phone=?, Salaire=? WHERE idEmploye=?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            if (!verifierChamps()) return;
            for (int i = 1; i < 6; i++) {
                pst.setString(i, fields[i].getText());
            }
            pst.setDouble(6, Double.parseDouble(fields[6].getText()));
            pst.setString(7, fields[0].getText());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Modification réussie.");
            } else {
                JOptionPane.showMessageDialog(this, "Aucune modification.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur modification : " + ex.getMessage());
        }
    }

    private void supprimerEmploye() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID à supprimer");
        String sql = "DELETE FROM Employe WHERE idEmploye=?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Suppression réussie.");
            } else {
                JOptionPane.showMessageDialog(this, "Aucun enregistrement supprimé.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur suppression : " + ex.getMessage());
        }
    }

    private void listerEmployes() {
        String sql = "SELECT * FROM Employe";
        try (Connection cn = Connexion.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur listing : " + ex.getMessage());
        }
    }
}
