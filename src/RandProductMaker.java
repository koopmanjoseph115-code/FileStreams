import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class RandProductMaker extends JFrame {
    private JTextField idField, nameField, descField, costField, countField;
    private JButton addBtn, quitBtn;
    private RandomAccessFile raf;
    private int recordCount = 0;

    public RandProductMaker() {
        super("Random Access Product Maker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 320);
        setLocationRelativeTo(null);

        try {
            File workingDirectory = new File(System.getProperty("user.dir"));
            File file = Paths.get(workingDirectory.getPath(), "src", "ProductRandomData.dat").toFile();
            raf = new RandomAccessFile(file, "rw");
            recordCount = (int) (raf.length() / Product.RECORD_SIZE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage());
            System.exit(1);
        }

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Product ID [6 chars]:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Product Name [max 35]:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description [max 75]:"));
        descField = new JTextField();
        formPanel.add(descField);

        formPanel.add(new JLabel("Cost:"));
        costField = new JTextField();
        formPanel.add(costField);

        formPanel.add(new JLabel("Record Count:"));
        countField = new JTextField(String.valueOf(recordCount));
        countField.setEditable(false);
        formPanel.add(countField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        addBtn = new JButton("Add Record");
        quitBtn = new JButton("Quit");

        addBtn.addActionListener(e -> addRecord());
        quitBtn.addActionListener(e -> closeAndExit());

        buttonPanel.add(addBtn);
        buttonPanel.add(quitBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRecord() {
        String ID = idField.getText().trim();
        String name = nameField.getText().trim();
        String description = descField.getText().trim();
        String costText = costField.getText().trim();

        // Validate non-empty fields
        if (ID.isEmpty() || name.isEmpty() || description.isEmpty() || costText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields before adding.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate field lengths
        if (ID.length() > Product.ID_LEN) {
            JOptionPane.showMessageDialog(this, "ID must be 6 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (name.length() > Product.NAME_LEN) {
            JOptionPane.showMessageDialog(this, "Name must be 35 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (description.length() > Product.DESC_LEN) {
            JOptionPane.showMessageDialog(this, "Description must be 75 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate numeric cost matching Lab 01 bounds
        double cost;
        try {
            cost = Double.parseDouble(costText);
            if (cost < 0.01 || cost > 999999.99) {
                JOptionPane.showMessageDialog(this, "Cost must be between 0.01 and 999999.99.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for Cost.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Matches your Lab 01 constructor: Product(ID, name, description, cost)
        Product product = new Product(ID, name, description, cost);

        try {
            raf.seek(raf.length());
            raf.writeChars(product.getFixedID());
            raf.writeChars(product.getFixedName());
            raf.writeChars(product.getFixedDescription());
            raf.writeDouble(product.getCost());

            recordCount++;
            countField.setText(String.valueOf(recordCount));

            // Clear fields for next record
            idField.setText("");
            nameField.setText("");
            descField.setText("");
            costField.setText("");
            idField.requestFocus();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeAndExit() {
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RandProductMaker::new);
    }
}
