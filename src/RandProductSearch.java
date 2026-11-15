import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class RandProductSearch extends JFrame {
    private JTextField searchField;
    private JTextArea resultsArea;
    private JButton searchBtn, quitBtn;

    public RandProductSearch() {
        super("Random Access Product Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 400);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(new JLabel("Partial Product Name:"));
        searchField = new JTextField(20);
        searchBtn = new JButton("Search");
        quitBtn = new JButton("Quit");

        searchBtn.addActionListener(e -> searchProducts());
        searchField.addActionListener(e -> searchProducts());
        quitBtn.addActionListener(e -> System.exit(0));

        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(quitBtn);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void searchProducts() {
        String query = searchField.getText().trim().toLowerCase();
        resultsArea.setText("");

        File workingDirectory = new File(System.getProperty("user.dir"));
        File file = Paths.get(workingDirectory.getPath(), "src", "ProductRandomData.dat").toFile();

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "ProductRandomData.dat not found in src folder. Run RandProductMaker first.");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long numRecords = raf.length() / Product.RECORD_SIZE;
            int matchCount = 0;

            for (int i = 0; i < numRecords; i++) {
                raf.seek((long) i * Product.RECORD_SIZE);

                String id = readFixedString(raf, Product.ID_LEN).trim();
                String name = readFixedString(raf, Product.NAME_LEN).trim();
                String description = readFixedString(raf, Product.DESC_LEN).trim();
                double cost = raf.readDouble();

                if (name.toLowerCase().contains(query)) {
                    Product p = new Product(id, name, description, cost);
                    resultsArea.append(p.toString() + "\n");
                    matchCount++;
                }
            }

            if (matchCount == 0) {
                resultsArea.setText("No products found matching: \"" + searchField.getText().trim() + "\"");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readFixedString(RandomAccessFile raf, int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(raf.readChar());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RandProductSearch::new);
    }
}
