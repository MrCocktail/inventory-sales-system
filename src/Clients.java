import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class Clients extends JInternalFrame {
    private JPanel contentPane;
    private JTextField t1, t2, t3, t4, t5;
    private JTable table;
    private ResultSet rs;

    public Clients() {
        setTitle("Gestion des Clients");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setSize(700, 450);
        setLocation(50, 30);

        Color rouge = new Color(183, 28, 28);
        Color grisClair = new Color(238, 238, 238);

        contentPane = new JPanel();
        contentPane.setBackground(grisClair);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("Gestion des Clients", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(rouge);
        lblTitle.setBounds(200, 10, 300, 30);
        contentPane.add(lblTitle);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(30, 50, 620, 25);
        menuBar.setBackground(rouge);
        contentPane.add(menuBar);

        JMenu menuActions = new JMenu("Actions");
        menuActions.setForeground(Color.WHITE);
        menuBar.add(menuActions);

        JMenuItem itemInsert = new JMenuItem("Insertion");
        itemInsert.addActionListener(e -> insererClient());
        menuActions.add(itemInsert);

        JMenuItem itemSearch = new JMenuItem("Recherche");
        itemSearch.addActionListener(e -> rechercherClient());
        menuActions.add(itemSearch);

        JMenuItem itemUpdate = new JMenuItem("Modification");
        itemUpdate.addActionListener(e -> modifierClient());
        menuActions.add(itemUpdate);

        JMenuItem itemDelete = new JMenuItem("Suppression");
        itemDelete.addActionListener(e -> supprimerClient());
        menuActions.add(itemDelete);

        JMenuItem itemList = new JMenuItem("Listing complet");
        itemList.addActionListener(e -> listerClients());
        menuActions.add(itemList);

        String[] labels = {"ID Client", "Prénom", "Nom", "Adresse", "Téléphone"};
        JTextField[] fields = new JTextField[5];
        int y = 90;
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Tahoma", Font.PLAIN, 13));
            label.setBounds(30, y, 180, 20);
            contentPane.add(label);

            fields[i] = new JTextField();
            fields[i].setBounds(220, y, 160, 20);
            if (i == 0) {
                fields[i].setEditable(false); // ID is auto-generated
                fields[i].setBackground(grisClair);
            } 
            contentPane.add(fields[i]);
            y += 28;
        }

        t1 = fields[0]; t2 = fields[1]; t3 = fields[2]; t4 = fields[3]; t5 = fields[4];

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 260, 620, 120);
        contentPane.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);
    }

    private boolean verifierChamps() {
        return !(t2.getText().isEmpty() || t3.getText().isEmpty() ||
                 t4.getText().isEmpty() || t5.getText().isEmpty());
    }

    private void insererClient() {
        if (!verifierChamps()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }
        String sql = "INSERT INTO Client (Prenom, Nom, Adresse, Phone) VALUES (?, ?, ?, ?)";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            // pst.setInt(1, Integer.parseInt(t1.getText()));
            pst.setString(1, t2.getText());
            pst.setString(2, t3.getText());
            pst.setString(3, t4.getText());
            pst.setString(4, t5.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Insertion réussie.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void rechercherClient() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID du client :");
        String sql = "SELECT * FROM Client WHERE idClient = ?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(id));
            rs = pst.executeQuery();
            if (rs.next()) {
                t1.setText(rs.getString(1));
                t2.setText(rs.getString(2));
                t3.setText(rs.getString(3));
                t4.setText(rs.getString(4));
                t5.setText(rs.getString(5));
            } else {
                JOptionPane.showMessageDialog(this, "Client non trouvé.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void modifierClient() {
        if (!verifierChamps()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }
        String sql = "UPDATE Client SET Prenom=?, Nom=?, Adresse=?, Phone=? WHERE idClient=?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, t2.getText());
            pst.setString(2, t3.getText());
            pst.setString(3, t4.getText());
            pst.setString(4, t5.getText());
            pst.setInt(5, Integer.parseInt(t1.getText()));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Modification réussie.");
            } else {
                JOptionPane.showMessageDialog(this, "Client introuvable.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void supprimerClient() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID du client à supprimer :");
        String sql = "DELETE FROM Client WHERE idClient = ?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(id));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Suppression réussie.");
            } else {
                JOptionPane.showMessageDialog(this, "Client non trouvé.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void listerClients() {
        String sql = "SELECT * FROM Client";
        try (Connection cn = Connexion.getConnection(); Statement st = cn.createStatement()) {
            rs = st.executeQuery(sql);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}
