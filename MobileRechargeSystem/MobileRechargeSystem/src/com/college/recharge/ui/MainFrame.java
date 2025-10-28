package com.college.recharge.ui;

import com.college.recharge.core.RechargeSystem;
import com.college.recharge.models.RechargePlan;
import com.college.recharge.models.RechargeRecord;
import com.college.recharge.models.User;
import com.college.recharge.utils.FileHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {
    private RechargeSystem rechargeSystem;
    private JLabel balanceLabel;

    public MainFrame() {
        super("Mobile Recharge System");

        setLayout(new BorderLayout(10, 10));
        Color backgroundColor = new Color(250, 245, 250);    
        Color buttonColor = new Color(157, 97, 158);          
        Color buttonHoverColor = new Color(178, 120, 179);    
        Color textColor = Color.WHITE;                        
        Color headingColor = new Color(90, 45, 91);          
        Color tableHeaderColor = new Color(157, 97, 158);    
        Color borderColor = new Color(120, 70, 121);          

        getContentPane().setBackground(backgroundColor);

        rechargeSystem = FileHandler.loadSystemData();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FileHandler.saveSystemData(rechargeSystem);
                JOptionPane.showMessageDialog(MainFrame.this, "Data saved successfully!");
            }
        });

        // UI Components
        balanceLabel = new JLabel("Current Balance: ₹0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(headingColor);

        JButton loginButton = new JButton("Login / New User");
        JButton rechargeButton = new JButton("Recharge Now");
        JButton historyButton = new JButton("View History");

        // Style Buttons
        JButton[] buttons = {loginButton, rechargeButton, historyButton};
        for (JButton btn : buttons) {
            btn.setBackground(buttonColor);
            btn.setForeground(textColor);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(borderColor, 2, true));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);

            // Hover animation
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(buttonHoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(buttonColor);
                }
            });
        }

        // Login Button Action 
        loginButton.addActionListener(e -> {
            String mobile;
            while (true) {
                mobile = JOptionPane.showInputDialog(MainFrame.this, "Enter Mobile Number to login or register:");

                if (mobile == null) return; // user cancelled

            
                if (!mobile.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Invalid Mobile Number!\nPlease enter exactly 10 digits (numbers only).",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                break;
            }

            User existingUser = rechargeSystem.findUser(mobile);
            if (existingUser != null) {
                rechargeSystem.setCurrentUser(mobile);
                updateBalanceDisplay();
                JOptionPane.showMessageDialog(MainFrame.this, "Welcome back, " + existingUser.getName() + "!");
            } else {
                String name;
                while (true) {
                    name = JOptionPane.showInputDialog(MainFrame.this, "New user! Enter your name:");

                    if (name == null) return;

                   
                    if (!name.matches("[a-zA-Z ]+")) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Invalid Name!\nPlease enter alphabets only (no numbers or symbols).",
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    break;
                }

                String balanceStr = JOptionPane.showInputDialog(MainFrame.this, "Enter an initial balance:");
                if (balanceStr == null || balanceStr.isEmpty()) return;

                try {
                    double initialBalance = Double.parseDouble(balanceStr);
                    User newUser = new User(mobile, name, initialBalance);
                    rechargeSystem.addUser(newUser);
                    rechargeSystem.setCurrentUser(mobile);
                    updateBalanceDisplay();
                    JOptionPane.showMessageDialog(MainFrame.this, "New user registered and logged in!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Invalid balance input.");
                }
            }
        });

        // Recharge Button
        rechargeButton.addActionListener(e -> {
            if (rechargeSystem.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please log in first.");
                return;
            }

            List<RechargePlan> plans = rechargeSystem.getAvailablePlans();
            Object[] planDescriptions = new Object[plans.size()];
            for (int i = 0; i < plans.size(); i++) {
                planDescriptions[i] = plans.get(i).getDescription() + " (₹" + plans.get(i).getPrice() + ")";
            }

            String selectedPlanDesc = (String) JOptionPane.showInputDialog(MainFrame.this,
                    "Choose a recharge plan:", "Recharge", JOptionPane.QUESTION_MESSAGE,
                    null, planDescriptions, planDescriptions[0]);

            if (selectedPlanDesc != null) {
                int selectedPlanId = -1;
                for (RechargePlan plan : plans) {
                    if ((plan.getDescription() + " (₹" + plan.getPrice() + ")").equals(selectedPlanDesc)) {
                        selectedPlanId = plan.getPlanId();
                        break;
                    }
                }

                if (selectedPlanId != -1) {
                    boolean success = rechargeSystem.recharge(selectedPlanId);
                    if (success) {
                        updateBalanceDisplay();
                        JOptionPane.showMessageDialog(MainFrame.this, "Recharge successful!");
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "Recharge failed. Insufficient balance.");
                    }
                }
            }
        });

        // History Button
        historyButton.addActionListener(e -> {
            User currentUser = rechargeSystem.getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please log in first.");
                return;
            }

            List<RechargeRecord> history = currentUser.getHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.this, "No recharge history found.");
                return;
            }

            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Date", "Plan", "Amount"}, 0);
            for (RechargeRecord record : history) {
                tableModel.addRow(new Object[]{record.getDate(), record.getPlanDescription(), "₹" + record.getAmount()});
            }

            JTable historyTable = new JTable(tableModel);
            historyTable.setRowHeight(24);
            historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            historyTable.getTableHeader().setBackground(tableHeaderColor);
            historyTable.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setPreferredSize(new Dimension(350, 200));

            JOptionPane.showMessageDialog(MainFrame.this, scrollPane,
                    "Recharge History for " + currentUser.getName(), JOptionPane.PLAIN_MESSAGE);
        });

        // Panels
        JPanel topPanel = new JPanel();
        topPanel.setBackground(backgroundColor);
        topPanel.add(balanceLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(loginButton);
        centerPanel.add(rechargeButton);
        centerPanel.add(historyButton);
        add(centerPanel, BorderLayout.CENTER);

        // Frame Settings
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        User currentUser = rechargeSystem.getCurrentUser();
        if (currentUser != null) {
            balanceLabel.setText("Current Balance: ₹" + String.format("%.2f", currentUser.getWalletBalance()));
        } else {
            balanceLabel.setText("Current Balance: ₹0.00");
        }
    }

    public static void main(String[] args) {
        try {
            // Use Nimbus Look & Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

