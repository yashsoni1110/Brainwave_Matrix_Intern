import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ATMInterface {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Account account;
    private int attempts = 3;
    private boolean isAuthenticated = false;

    // Account class to manage balance, PIN, and transactions
    static class Account {
        private double balance;
        private String pin;
        private ArrayList<String> transactionHistory;

        public Account(double initialBalance, String initialPin) {
            this.balance = initialBalance;
            this.pin = initialPin;
            this.transactionHistory = new ArrayList<>();
            this.transactionHistory.add(getCurrentTime() + " - Account created with initial balance: $" + initialBalance);
        }

        public boolean validatePin(String enteredPin) {
            return this.pin.equals(enteredPin);
        }

        public double getBalance() {
            return this.balance;
        }

        public boolean withdraw(double amount) {
            if (amount > 0 && amount <= this.balance) {
                this.balance -= amount;
                addTransaction("Withdrawal: -$" + amount + ", New Balance: $" + this.balance);
                return true;
            }
            return false;
        }

        public void deposit(double amount) {
            if (amount > 0) {
                this.balance += amount;
                addTransaction("Deposit: +$" + amount + ", New Balance: $" + this.balance);
            }
        }

        public boolean changePin(String oldPin, String newPin) {
            if (validatePin(oldPin) && newPin.length() == 4 && newPin.matches("\\d+")) {
                this.pin = newPin;
                addTransaction("PIN changed successfully");
                return true;
            }
            return false;
        }

        public ArrayList<String> getTransactionHistory() {
            return this.transactionHistory;
        }

        private void addTransaction(String transaction) {
            this.transactionHistory.add(getCurrentTime() + " - " + transaction);
        }

        private String getCurrentTime() {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        }
    }

    public ATMInterface() {
        account = new Account(1000.00, "1234");
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("ATM Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Authentication Screen
        JPanel authPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel authTitle = new JLabel("Welcome to the ATM", SwingConstants.CENTER);
        authTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel authError = new JLabel("", SwingConstants.CENTER);
        authError.setForeground(Color.RED);
        JPasswordField pinInput = new JPasswordField(10);
        pinInput.setHorizontalAlignment(JTextField.CENTER);
        JButton authSubmit = new JButton("Submit");
        JLabel attemptsRemaining = new JLabel("", SwingConstants.CENTER);

        gbc.gridy = 0;
        authPanel.add(authTitle, gbc);
        gbc.gridy = 1;
        authPanel.add(authError, gbc);
        gbc.gridy = 2;
        authPanel.add(pinInput, gbc);
        gbc.gridy = 3;
        authPanel.add(authSubmit, gbc);
        gbc.gridy = 4;
        authPanel.add(attemptsRemaining, gbc);

        authSubmit.addActionListener(e -> {
            String pin = new String(pinInput.getPassword());
            if (account.validatePin(pin)) {
                isAuthenticated = true;
                authError.setText("");
                cardLayout.show(mainPanel, "mainMenu");
            } else {
                attempts--;
                authError.setText("Invalid PIN. " + attempts + " attempts remaining.");
                attemptsRemaining.setText("");
                if (attempts == 0) {
                    cardLayout.show(mainPanel, "lockedScreen");
                }
            }
            pinInput.setText("");
        });

        // Main Menu Screen
        JPanel mainMenuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel menuTitle = new JLabel("ATM Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JButton balanceButton = new JButton("Check Balance");
        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton changePinButton = new JButton("Change PIN");
        JButton historyButton = new JButton("Transaction History");
        JButton exitButton = new JButton("Exit");

        mainMenuPanel.add(menuTitle);
        mainMenuPanel.add(balanceButton);
        mainMenuPanel.add(withdrawButton);
        mainMenuPanel.add(depositButton);
        mainMenuPanel.add(changePinButton);
        mainMenuPanel.add(historyButton);
        mainMenuPanel.add(exitButton);

        // Balance Screen
        JPanel balancePanel = new JPanel(new GridBagLayout());
        JLabel balanceTitle = new JLabel("Balance", SwingConstants.CENTER);
        balanceTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel balanceDisplay = new JLabel("", SwingConstants.CENTER);
        JButton balanceBackButton = new JButton("Back to Menu");

        gbc.gridy = 0;
        balancePanel.add(balanceTitle, gbc);
        gbc.gridy = 1;
        balancePanel.add(balanceDisplay, gbc);
        gbc.gridy = 2;
        balancePanel.add(balanceBackButton, gbc);

        balanceButton.addActionListener(e -> {
            balanceDisplay.setText("Current Balance: $" + String.format("%.2f", account.getBalance()));
            cardLayout.show(mainPanel, "balanceScreen");
        });

        // Withdraw Screen
        JPanel withdrawPanel = new JPanel(new GridBagLayout());
        JLabel withdrawTitle = new JLabel("Withdraw", SwingConstants.CENTER);
        withdrawTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel withdrawError = new JLabel("", SwingConstants.CENTER);
        withdrawError.setForeground(Color.RED);
        JTextField withdrawAmount = new JTextField(10);
        withdrawAmount.setHorizontalAlignment(JTextField.CENTER);
        JButton withdrawSubmit = new JButton("Withdraw");
        JButton withdrawBack = new JButton("Back");

        gbc.gridy = 0;
        withdrawPanel.add(withdrawTitle, gbc);
        gbc.gridy = 1;
        withdrawPanel.add(withdrawError, gbc);
        gbc.gridy = 2;
        withdrawPanel.add(withdrawAmount, gbc);
        gbc.gridy = 3;
        withdrawPanel.add(withdrawSubmit, gbc);
        gbc.gridy = 4;
        withdrawPanel.add(withdrawBack, gbc);

        withdrawButton.addActionListener(e -> {
            withdrawAmount.setText("");
            withdrawError.setText("");
            cardLayout.show(mainPanel, "withdrawScreen");
        });

        withdrawSubmit.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(withdrawAmount.getText());
                if (account.withdraw(amount)) {
                    withdrawError.setForeground(Color.GREEN);
                    withdrawError.setText("Withdrawal successful. New Balance: $" + String.format("%.2f", account.getBalance()));
                } else {
                    withdrawError.setForeground(Color.RED);
                    withdrawError.setText("Insufficient funds or invalid amount.");
                }
            } catch (NumberFormatException ex) {
                withdrawError.setForeground(Color.RED);
                withdrawError.setText("Invalid input. Please enter a numeric value.");
            }
            withdrawAmount.setText("");
        });

        // Deposit Screen
        JPanel depositPanel = new JPanel(new GridBagLayout());
        JLabel depositTitle = new JLabel("Deposit", SwingConstants.CENTER);
        depositTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel depositError = new JLabel("", SwingConstants.CENTER);
        depositError.setForeground(Color.RED);
        JTextField depositAmount = new JTextField(10);
        depositAmount.setHorizontalAlignment(JTextField.CENTER);
        JButton depositSubmit = new JButton("Deposit");
        JButton depositBack = new JButton("Back");

        gbc.gridy = 0;
        depositPanel.add(depositTitle, gbc);
        gbc.gridy = 1;
        depositPanel.add(depositError, gbc);
        gbc.gridy = 2;
        depositPanel.add(depositAmount, gbc);
        gbc.gridy = 3;
        depositPanel.add(depositSubmit, gbc);
        gbc.gridy = 4;
        depositPanel.add(depositBack, gbc);

        depositButton.addActionListener(e -> {
            depositAmount.setText("");
            depositError.setText("");
            cardLayout.show(mainPanel, "depositScreen");
        });

        depositSubmit.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(depositAmount.getText());
                if (amount > 0) {
                    account.deposit(amount);
                    depositError.setForeground(Color.GREEN);
                    depositError.setText("Deposit successful. New Balance: $" + String.format("%.2f", account.getBalance()));
                } else {
                    depositError.setForeground(Color.RED);
                    depositError.setText("Invalid amount.");
                }
            } catch (NumberFormatException ex) {
                depositError.setForeground(Color.RED);
                depositError.setText("Invalid input. Please enter a numeric value.");
            }
            depositAmount.setText("");
        });

        // Change PIN Screen
        JPanel changePinPanel = new JPanel(new GridBagLayout());
        JLabel changePinTitle = new JLabel("Change PIN", SwingConstants.CENTER);
        changePinTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel changePinError = new JLabel("", SwingConstants.CENTER);
        changePinError.setForeground(Color.RED);
        JPasswordField currentPin = new JPasswordField(10);
        currentPin.setHorizontalAlignment(JTextField.CENTER);
        JPasswordField newPin = new JPasswordField(10);
        newPin.setHorizontalAlignment(JTextField.CENTER);
        JButton changePinSubmit = new JButton("Change PIN");
        JButton changePinBack = new JButton("Back");

        gbc.gridy = 0;
        changePinPanel.add(changePinTitle, gbc);
        gbc.gridy = 1;
        changePinPanel.add(changePinError, gbc);
        gbc.gridy = 2;
        changePinPanel.add(new JLabel("Current PIN:", SwingConstants.CENTER), gbc);
        gbc.gridy = 3;
        changePinPanel.add(currentPin, gbc);
        gbc.gridy = 4;
        changePinPanel.add(new JLabel("New 4-digit PIN:", SwingConstants.CENTER), gbc);
        gbc.gridy = 5;
        changePinPanel.add(newPin, gbc);
        gbc.gridy = 6;
        changePinPanel.add(changePinSubmit, gbc);
        gbc.gridy = 7;
        changePinPanel.add(changePinBack, gbc);

        changePinButton.addActionListener(e -> {
            currentPin.setText("");
            newPin.setText("");
            changePinError.setText("");
            cardLayout.show(mainPanel, "changePinScreen");
        });

        changePinSubmit.addActionListener(e -> {
            String oldPin = new String(currentPin.getPassword());
            String newPinStr = new String(newPin.getPassword());
            if (account.changePin(oldPin, newPinStr)) {
                changePinError.setForeground(Color.GREEN);
                changePinError.setText("PIN changed successfully.");
            } else {
                changePinError.setForeground(Color.RED);
                changePinError.setText("Failed to change PIN. Check current PIN or ensure new PIN is 4 digits.");
            }
            currentPin.setText("");
            newPin.setText("");
        });

        // Transaction History Screen
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel historyTitle = new JLabel("Transaction History", SwingConstants.CENTER);
        historyTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JTextArea historyList = new JTextArea(10, 30);
        historyList.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyList);
        JButton historyBack = new JButton("Back to Menu");

        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        historyPanel.add(historyBack, BorderLayout.SOUTH);

        historyButton.addActionListener(e -> {
            historyList.setText("");
            ArrayList<String> history = account.getTransactionHistory();
            if (history.isEmpty()) {
                historyList.setText("No transactions found.");
            } else {
                for (String transaction : history) {
                    historyList.append(transaction + "\n");
                }
            }
            cardLayout.show(mainPanel, "historyScreen");
        });

        // Exit Screen
        JPanel exitPanel = new JPanel(new GridBagLayout());
        JLabel exitTitle = new JLabel("Thank You", SwingConstants.CENTER);
        exitTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel exitMessage = new JLabel("Thank you for using the ATM. Goodbye!", SwingConstants.CENTER);

        gbc.gridy = 0;
        exitPanel.add(exitTitle, gbc);
        gbc.gridy = 1;
        exitPanel.add(exitMessage, gbc);

        exitButton.addActionListener(e -> cardLayout.show(mainPanel, "exitScreen"));

        // Locked Screen
        JPanel lockedPanel = new JPanel(new GridBagLayout());
        JLabel lockedTitle = new JLabel("Account Locked", SwingConstants.CENTER);
        lockedTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lockedTitle.setForeground(Color.RED);
        JLabel lockedMessage = new JLabel("Too many incorrect attempts. Account locked.", SwingConstants.CENTER);

        gbc.gridy = 0;
        lockedPanel.add(lockedTitle, gbc);
        gbc.gridy = 1;
        lockedPanel.add(lockedMessage, gbc);

        // Back button actions
        balanceBackButton.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        withdrawBack.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        depositBack.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        changePinBack.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        historyBack.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));

        // Add panels to main panel with CardLayout
        mainPanel.add(authPanel, "authScreen");
        mainPanel.add(mainMenuPanel, "mainMenu");
        mainPanel.add(balancePanel, "balanceScreen");
        mainPanel.add(withdrawPanel, "withdrawScreen");
        mainPanel.add(depositPanel, "depositScreen");
        mainPanel.add(changePinPanel, "changePinScreen");
        mainPanel.add(historyPanel, "historyScreen");
        mainPanel.add(exitPanel, "exitScreen");
        mainPanel.add(lockedPanel, "lockedScreen");

        frame.add(mainPanel);
        cardLayout.show(mainPanel, "authScreen");
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATMInterface::new);
    }
}