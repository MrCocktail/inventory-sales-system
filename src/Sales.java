import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class Sales extends JInternalFrame {
    private JComboBox<String> cbClients, cbPieces;
    private JTextField tfQuantite;
    private JLabel lblTotal;
    private JTable tableLignes;
    private DefaultTableModel model;

    private double totalVente = 0.0;
    private Vector<Vector<Object>> lignes = new Vector<>();

    public Sales() {
        setTitle("Nouvelle Vente");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setSize(700, 500);
        setLocation(60, 30);

        Color rouge = new Color(183, 28, 28);
        Color grisClair = new Color(240, 240, 240);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(grisClair);
        setContentPane(panel);

        JLabel lblTitre = new JLabel("Vente de Pièces", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setForeground(rouge);
        lblTitre.setBounds(200, 10, 300, 30);
        panel.add(lblTitre);

        JLabel lblClient = new JLabel("Client :");
        lblClient.setBounds(30, 60, 80, 20);
        panel.add(lblClient);

        cbClients = new JComboBox<>();
        cbClients.setBounds(100, 60, 200, 22);
        panel.add(cbClients);
        chargerClients();

        JLabel lblPiece = new JLabel("Pièce :");
        lblPiece.setBounds(30, 100, 80, 20);
        panel.add(lblPiece);

        cbPieces = new JComboBox<>();
        cbPieces.setBounds(100, 100, 200, 22);
        panel.add(cbPieces);
        chargerPieces();

        JLabel lblQte = new JLabel("Quantité :");
        lblQte.setBounds(320, 100, 80, 20);
        panel.add(lblQte);

        tfQuantite = new JTextField();
        tfQuantite.setBounds(390, 100, 50, 22);
        panel.add(tfQuantite);

        JButton btnAjouter = new JButton("Ajouter Ligne");
        btnAjouter.setBounds(460, 100, 130, 22);
        panel.add(btnAjouter);

        model = new DefaultTableModel(new String[]{"Code", "Nom", "Quantité", "Prix U", "Montant"}, 0);
        tableLignes = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableLignes);
        scrollPane.setBounds(30, 150, 620, 200);
        panel.add(scrollPane);

        lblTotal = new JLabel("Total : 0.0 Gourdes");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotal.setBounds(30, 360, 250, 25);
        panel.add(lblTotal);

        JButton btnValider = new JButton("Valider Vente");
        btnValider.setBounds(480, 360, 150, 25);
        panel.add(btnValider);

        // Action ajout ligne
        btnAjouter.addActionListener(e -> ajouterLigne());

        // Action valider vente
        btnValider.addActionListener(e -> validerVente());
    }

    private void chargerClients() {
        try (Connection cn = Connexion.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT idClient, Nom FROM Client");
            while (rs.next()) {
                cbClients.addItem(rs.getInt(1) + ": " + rs.getString(2));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement clients : " + e.getMessage());
        }
    }

    private void chargerPieces() {
        try (Connection cn = Connexion.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT idPiece, Nom FROM Pieces");
            while (rs.next()) {
                cbPieces.addItem(rs.getInt(1) + ": " + rs.getString(2));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement pièces : " + e.getMessage());
        }
    }

    private void ajouterLigne() {
        try {
            String item = (String) cbPieces.getSelectedItem();
            int idPiece = Integer.parseInt(item.split(":")[0]);
            String nom = item.split(":")[1].trim();
            int qte = Integer.parseInt(tfQuantite.getText());

            Connection cn = Connexion.getConnection();
            PreparedStatement pst = cn.prepareStatement("SELECT Prix_vente FROM Pieces WHERE idPiece = ?");
            pst.setInt(1, idPiece);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double prix = rs.getDouble(1);
                double montant = prix * qte;

                model.addRow(new Object[]{idPiece, nom, qte, prix, montant});
                totalVente += montant;
                lblTotal.setText("Total : " + totalVente + " Gourdes");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur ajout ligne : " + e.getMessage());
        }
    }

    private void validerVente() {
    try {
        String clientItem = (String) cbClients.getSelectedItem();
        int idClient = Integer.parseInt(clientItem.split(":")[0]);
        int idEmploye = 1; // TODO: remplacer par l'employé connecté

        Connection cn = Connexion.getConnection();

        // 1. Insertion dans Vente
        String insertVente = "INSERT INTO Vente (idEmploye, idClient, montant, DateVente) VALUES (?, ?, ?, NOW())";
        PreparedStatement pst = cn.prepareStatement(insertVente, Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1, idEmploye);
        pst.setInt(2, idClient);
        pst.setDouble(3, totalVente);
        pst.executeUpdate(); 
        // 2. Récupérer le numéro de vente généré
        ResultSet rs = pst.getGeneratedKeys();
        int numero = 0;
        if (rs.next()) {
            numero = rs.getInt(1);
        } else {
            throw new SQLException("Échec de récupération du numéro de vente généré.");
        }

        // 3. Insertion des lignes
        for (int i = 0; i < model.getRowCount(); i++) {
            int idPiece = (int) model.getValueAt(i, 0);
            int qte = (int) model.getValueAt(i, 2);
            double montant = (double) model.getValueAt(i, 4);

            String insertLigne = "INSERT INTO ligne_de_vente (numero, idEmploye, idPiece, Quantite, montant) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst2 = cn.prepareStatement(insertLigne);
            pst2.setInt(1, numero); // On utilise le bon numéro généré
            pst2.setInt(2, idEmploye);
            pst2.setInt(3, idPiece);
            pst2.setInt(4, qte);
            pst2.setDouble(5, montant);
            pst2.executeUpdate();

            // 4. Mise à jour du stock
            PreparedStatement pst3 = cn.prepareStatement("UPDATE Pieces SET Quantite = Quantite - ? WHERE idPiece = ?");
            pst3.setInt(1, qte);
            pst3.setInt(2, idPiece);
            pst3.executeUpdate();
        }

        // 5. Nettoyage final
        JOptionPane.showMessageDialog(this, "Vente enregistrée avec succès.");
        model.setRowCount(0);
        lblTotal.setText("Total : 0.0 Gourdes");
        totalVente = 0.0;
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erreur validation vente : " + e.getMessage());
        e.printStackTrace();
    }
}

}
