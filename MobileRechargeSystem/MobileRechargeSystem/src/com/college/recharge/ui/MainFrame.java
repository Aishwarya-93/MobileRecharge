package com.college.recharge.ui;

import com.college.recharge.core.RechargeSystem;
import com.college.recharge.models.User;
import com.college.recharge.models.RechargePlan;
import com.college.recharge.models.RechargeRecord;
import com.college.recharge.utils.FileHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainFrame extends JFrame {
    private RechargeSystem rechargeSystem; // This will now be loaded from file
    private JLabel balanceLabel;

    public MainFrame() {
        super("Mobile Recharge System");
        setLayout(new BorderLayout(10, 10));
        
        // UPDATED: Now loads the entire system from the file
        rechargeSystem = FileHandler.loadSystemData();

        // Listen for the window closing to save the entire system
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FileHandler.saveSystemData(rechargeSystem);
                JOptionPane.showMessageDialog(MainFrame.this, "Data saved successfully!");
            }
        });

        // Create UI components
        balanceLabel = new JLabel("Current Balance: ₹0.00");
        JButton loginButton = new JButton("Login / New User");
        JButton rechargeButton = new JButton("Recharge Now");
        JButton historyButton = new JButton("View History");

        loginButton.addActionListener(e -> {
            String mobile = JOptionPane.showInputDialog(MainFrame.this, "Enter Mobile Number to login or register:");
            if (mobile == null || mobile.isEmpty()) return;

            User existingUser = rechargeSystem.findUser(mobile);
            if (existingUser != null) {
                // User exists, log them in
                rechargeSystem.setCurrentUser(mobile);
                updateBalanceDisplay();
                JOptionPane.showMessageDialog(MainFrame.this, "Welcome back, " + existingUser.getName() + "!");
            } else {
                // User does not exist, create a new one
                String name = JOptionPane.showInputDialog(MainFrame.this, "New user! Enter your name:");
                if (name == null || name.isEmpty()) return;
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

        historyButton.addActionListener(e -> {
            User currentUser = rechargeSystem.getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please log in first.");
                return;
            }
            
            List<RechargeRecord> history = currentUser.getHistory(); // UPDATED: Get history from the User object
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.this, "No recharge history found.");
                return;
            }

            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Date", "Plan", "Amount"}, 0);
            for (RechargeRecord record : history) {
                tableModel.addRow(new Object[]{record.getDate(), record.getPlanDescription(), "₹" + record.getAmount()});
            }

            JTable historyTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(historyTable);

            JOptionPane.showMessageDialog(MainFrame.this, scrollPane, "Recharge History for " + currentUser.getName(), JOptionPane.PLAIN_MESSAGE);
        });

        JPanel topPanel = new JPanel();
        topPanel.add(balanceLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        centerPanel.add(loginButton);
        centerPanel.add(rechargeButton);
        centerPanel.add(historyButton);
        add(centerPanel, BorderLayout.CENTER);

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
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}