import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import com.toedter.calendar.JDateChooser;


public class SalesReport extends JInternalFrame {
    private JPanel contentPane;
    private JTable table;
    private JLabel lblTotalAchat, lblTotalVente, lblBenefice;
    private JTextField tfDate;
    private boolean found = false;

    public SalesReport() {
        setTitle("Rapport de Ventes par Date");
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

        JLabel lblTitre = new JLabel("Rapport de Ventes", JLabel.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setForeground(rouge);
        lblTitre.setBounds(200, 10, 350, 30);
        contentPane.add(lblTitre);

        
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD) :");
        lblDate.setBounds(30, 60, 150, 25);
        contentPane.add(lblDate);
        
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setBounds(180, 60, 160, 25);
        contentPane.add(dateChooser);

        // tfDate = new JTextField();
        // tfDate.setBounds(180, 60, 150, 25);
        // contentPane.add(tfDate);

        JButton btnAfficher = new JButton("Afficher Rapport");
        btnAfficher.setBounds(350, 60, 150, 25);
        contentPane.add(btnAfficher);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 100, 680, 250);
        contentPane.add(scrollPane);

        lblTotalAchat = new JLabel("Total Achat : 0.0 Gourdes");
        lblTotalAchat.setBounds(30, 370, 200, 25);
        contentPane.add(lblTotalAchat);

        lblTotalVente = new JLabel("Total Vente : 0.0 Gourdes");
        lblTotalVente.setBounds(250, 370, 200, 25);
        contentPane.add(lblTotalVente);

        lblBenefice = new JLabel("Bénéfice : 0.0 Gourdes");
        lblBenefice.setBounds(470, 370, 200, 25);
        contentPane.add(lblBenefice);

        btnAfficher.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        java.util.Date date = dateChooser.getDate();
if (date == null) {
    JOptionPane.showMessageDialog(null, "Veuillez sélectionner une date.");
    return;
}
java.sql.Date sqlDate = new java.sql.Date(date.getTime());


        // java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "SELECT lv.idPiece, p.Nom, lv.Quantite, p.Prix_achat, p.Prix_vente, " +
                     "lv.Quantite * p.Prix_achat AS TotalAchat, lv.Quantite * p.Prix_vente AS TotalVente, " +
                     "(lv.Quantite * p.Prix_vente) - (lv.Quantite * p.Prix_achat) AS Benefice " +
                     "FROM ligne_de_vente lv " +
                     "JOIN Vente v ON lv.numero = v.numero " +
                     "JOIN Pieces p ON lv.idPiece = p.idPiece " +
                     "WHERE v.DateVente = ?";

        try (Connection cn = Connexion.getConnection();
             PreparedStatement pst = cn.prepareStatement(
                 sql,
                 ResultSet.TYPE_SCROLL_INSENSITIVE,
                 ResultSet.CONCUR_READ_ONLY
             )) {

            pst.setDate(1, sqlDate);
            ResultSet rs = pst.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));

            // Calcul des totaux
            double totalAchat = 0, totalVente = 0;
            boolean found = false;
            rs.beforeFirst();
            while (rs.next()) {
                found = true;
                totalAchat += rs.getDouble("TotalAchat");
                totalVente += rs.getDouble("TotalVente");
            }
            double benefice = totalVente - totalAchat;
            lblTotalAchat.setText("Total Achat : " + totalAchat + " Gourdes");
            lblTotalVente.setText("Total Vente : " + totalVente + " Gourdes");
            lblBenefice.setText("Bénéfice Totale : " + benefice + " Gourdes");

            if (!found) {
                JOptionPane.showMessageDialog(null, "Aucune vente trouvée pour cette date.");
                lblTotalAchat.setText("Total Achat : 0.0 Gourdes");
                lblTotalVente.setText("Total Vente : 0.0 Gourdes");
                lblBenefice.setText("Bénéfice : 0.0 Gourdes");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage());
        }
    }
});

    }
}
