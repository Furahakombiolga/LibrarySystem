package vu.libgui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Libgui extends javax.swing.JFrame {
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTable bookTable;
    private javax.swing.JTextField bookIDField;
    private javax.swing.JTextField titleField;
    private javax.swing.JTextField authorField;
    private javax.swing.JTextField yearField;
    private DefaultTableModel tableModel;

    public Libgui() {
        initComponents();
        refreshBookList();
    }

    private void initComponents() {
        JLabel bookIDLabel = new JLabel("Book ID:");
        JLabel titleLabel = new JLabel("Title:");
        JLabel authorLabel = new JLabel("Author:");
        JLabel yearLabel = new JLabel("Year:");

        bookIDField = new javax.swing.JTextField();
        titleField = new javax.swing.JTextField();
        authorField = new javax.swing.JTextField();
        yearField = new javax.swing.JTextField();

        addButton = new javax.swing.JButton("Add Book");
        deleteButton = new javax.swing.JButton("Delete Book");
        refreshButton = new javax.swing.JButton("Refresh List");

        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Title", "Author", "Year"}, 0);
        bookTable = new javax.swing.JTable(tableModel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBookList();
            }
        });

        setLayout(new java.awt.GridLayout(6, 2));
        add(bookIDLabel);
        add(bookIDField);
        add(titleLabel);
        add(titleField);
        add(authorLabel);
        add(authorField);
        add(yearLabel);
        add(yearField);
        add(addButton);
        add(deleteButton);
        add(refreshButton);
        add(new JScrollPane(bookTable));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        int year = Integer.parseInt(yearField.getText());

        try (Connection connection = DriverManager.getConnection("jdbc:ucanaccess://path/to/Library.accdb")) {
            String sql = "INSERT INTO Books (Title, Author, Year) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully.");
            refreshBookList();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book.");
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookID = (int) bookTable.getValueAt(selectedRow, 0);

            try (Connection connection = DriverManager.getConnection("jdbc:ucanaccess://path/to/Library.accdb")) {
                String sql = "DELETE FROM Books WHERE BookID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, bookID);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                refreshBookList();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting book.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No book selected.");
        }
    }

    private void refreshBookList() {
        tableModel.setRowCount(0); // Clear existing rows

        try (Connection connection = DriverManager.getConnection("jdbc:ucanaccess://path/to/Library.accdb")) {
            String sql = "SELECT * FROM Books";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int bookID = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                int year = resultSet.getInt("Year");

                tableModel.addRow(new Object[]{bookID, title, author, year});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving book list.");
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable () {
            public void run() {
                new LibraryGUI().setVisible(true);
            }
        });
    }
}
