import java.awt.EventQueue;

public class App {
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
					frame.setVisible(true);
					// Employees frame = new Employees();
					// Parts partsFrame = new Parts();
					// Clients clientFrame = new Clients();
					// frame.setVisible(true);
					// partsFrame.setVisible(true);
					// clientFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}
