import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class Parts extends JInternalFrame {
    private JPanel contentPane;
    private JTextField[] fields = new JTextField[7];
    private JTable table;
    private ResultSet rs;

    public Parts() {
        setTitle("Gestion des Pièces");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setSize(700, 500);
        setLocation(50, 30);

        Color rouge = new Color(183, 28, 28);
        Color grisClair = new Color(238, 238, 238);

        contentPane = new JPanel();
        contentPane.setBackground(grisClair);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("Gestion des Pièces", JLabel.CENTER);
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
        itemInsert.addActionListener(e -> insererPiece());
        menuActions.add(itemInsert);

        JMenuItem itemSearch = new JMenuItem("Recherche");
        itemSearch.addActionListener(e -> rechercherPiece());
        menuActions.add(itemSearch);

        JMenuItem itemUpdate = new JMenuItem("Modification");
        itemUpdate.addActionListener(e -> modifierPiece());
        menuActions.add(itemUpdate);

        JMenuItem itemDelete = new JMenuItem("Suppression");
        itemDelete.addActionListener(e -> supprimerPiece());
        menuActions.add(itemDelete);

        JMenuItem itemList = new JMenuItem("Listing complet");
        itemList.addActionListener(e -> listerPieces());
        menuActions.add(itemList);

        JMenuItem itemSortAZ = new JMenuItem("Lister par ordre alphabétique A → Z");
        itemSortAZ.addActionListener(e -> listerParNomAsc());
        menuActions.add(itemSortAZ);

        JMenuItem clearFields = new JMenuItem("Vider les champs");
        clearFields.addActionListener(e -> viderChamps());
        menuActions.add(clearFields);

        String[] labels = {"ID Pièce", "Nom", "Provenance", "Prix Achat", "Prix Vente", "Date Arrivage (YYYY-MM-DD)", "Quantité"};
        int y = 90;
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Tahoma", Font.PLAIN, 13));
            label.setBounds(30, y, 180, 20);
            contentPane.add(label);

            fields[i] = new JTextField();
            fields[i].setBounds(220, y, 160, 20);
            if (i == 0) {
                fields[i].setEditable(false);
                fields[i].setBackground(grisClair);
            } else if (i == 5) {
                fields[i].setToolTipText("Format: YYYY-MM-DD");
            }
            contentPane.add(fields[i]);
            y += 28;
        }

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 320, 620, 120);
        contentPane.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);
    }

    private void viderChamps() {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setText("");
        }
    }

    private boolean verifierChamps() {
        for (int i = 1; i < fields.length; i++) {
            if (fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
                return false;
            }
        }
        return true;
    }

    private void insererPiece() {
        if (!verifierChamps()) return;
        String sql = "INSERT INTO Pieces (Nom, Provenance, Prix_achat, Prix_vente, date_arrivage, Quantite) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, fields[1].getText());
            pst.setString(2, fields[2].getText());
            pst.setDouble(3, Double.parseDouble(fields[3].getText()));
            pst.setDouble(4, Double.parseDouble(fields[4].getText()));
            pst.setDate(5, Date.valueOf(fields[5].getText()));
            pst.setInt(6, Integer.parseInt(fields[6].getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Insertion réussie.");
            viderChamps();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void rechercherPiece() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID de la pièce :");
        String sql = "SELECT * FROM Pieces WHERE idPiece = ?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(id));
            rs = pst.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText(rs.getString(i + 1));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pièce non trouvée.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void modifierPiece() {
        if (!verifierChamps()) return;
        String sql = "UPDATE Pieces SET Nom=?, Provenance=?, Prix_achat=?, Prix_vente=?, date_arrivage=?, Quantite=? WHERE idPiece=?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, fields[1].getText());
            pst.setString(2, fields[2].getText());
            pst.setDouble(3, Double.parseDouble(fields[3].getText()));
            pst.setDouble(4, Double.parseDouble(fields[4].getText()));
            pst.setDate(5, Date.valueOf(fields[5].getText()));
            pst.setInt(6, Integer.parseInt(fields[6].getText()));
            pst.setInt(7, Integer.parseInt(fields[0].getText()));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Modification réussie.");
            } else {
                JOptionPane.showMessageDialog(this, "Pièce introuvable.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void supprimerPiece() {
        String id = JOptionPane.showInputDialog(this, "Entrez l'ID de la pièce à supprimer :");
        String sql = "DELETE FROM Pieces WHERE idPiece = ?";
        try (Connection cn = Connexion.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(id));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Suppression réussie.");
            } else {
                JOptionPane.showMessageDialog(this, "Pièce non trouvée.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void listerPieces() {
        String sql = "SELECT * FROM Pieces";
        try (Connection cn = Connexion.getConnection(); Statement st = cn.createStatement()) {
            rs = st.executeQuery(sql);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void listerParNomAsc() {
        String sql = "SELECT * FROM Pieces ORDER BY Nom ASC";
        try (Connection cn = Connexion.getConnection(); Statement st = cn.createStatement()) {
            rs = st.executeQuery(sql);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}
