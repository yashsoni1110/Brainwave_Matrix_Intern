import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Account {
    private double balance;
    private String pin;
    private ArrayList<String> transactionHistory;

    public Account(double initialBalance, String initialPin) {
        this.balance = initialBalance;
        this.pin = initialPin;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with initial balance: $" + initialBalance);
    }

    public boolean validatePin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public double getBalance() {
        return balance;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdrawal: -$" + amount + ", New Balance: $" + balance);
            return true;
        }
        return false;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposit: +$" + amount + ", New Balance: $" + balance);
        }
    }

    public boolean changePin(String oldPin, String newPin) {
        if (validatePin(oldPin) && newPin.length() == 4 && newPin.matches("\\d+")) {
            pin = newPin;
            addTransaction("PIN changed successfully");
            return true;
        }
        return false;
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    private void addTransaction(String transaction) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        transactionHistory.add(timestamp + " - " + transaction);
    }
}

class ATM {
    private Account account;
    private Scanner scanner;
    private boolean isAuthenticated;

    public ATM(Account account) {
        this.account = account;
        this.scanner = new Scanner(System.in);
        this.isAuthenticated = false;
    }

    public void start() {
        System.out.println("Welcome to the ATM");
        authenticateUser();
        if (isAuthenticated) {
            showMainMenu();
        }
        scanner.close();
        System.out.println("Thank you for using the ATM. Goodbye!");
    }

    private void authenticateUser() {
        int attempts = 3;
        while (attempts > 0 && !isAuthenticated) {
            System.out.print("Enter your PIN: ");
            String enteredPin = scanner.nextLine();
            if (account.validatePin(enteredPin)) {
                isAuthenticated = true;
                System.out.println("Authentication successful!");
            } else {
                attempts--;
                System.out.println("Invalid PIN. " + attempts + " attempts remaining.");
            }
        }
        if (!isAuthenticated) {
            System.out.println("Too many incorrect attempts. Account locked.");
        }
    }

    private void showMainMenu() {
        while (isAuthenticated) {
            System.out.println("\n=== ATM Menu ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Change PIN");
            System.out.println("5. Transaction History");
            System.out.println("6. Exit");
            System.out.print("Choose an option (1-6): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    checkBalance();
                    break;
                case "2":
                    withdraw();
                    break;
                case "3":
                    deposit();
                    break;
                case "4":
                    changePin();
                    break;
                case "5":
                    showTransactionHistory();
                    break;
                case "6":
                    isAuthenticated = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void checkBalance() {
        System.out.printf("Current Balance: $%.2f%n", account.getBalance());
    }

    private void withdraw() {
        System.out.print("Enter amount to withdraw: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (account.withdraw(amount)) {
                System.out.printf("Withdrawal successful. New Balance: $%.2f%n", account.getBalance());
            } else {
                System.out.println("Insufficient funds or invalid amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric value.");
        }
    }

    private void deposit() {
        System.out.print("Enter amount to deposit: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            account.deposit(amount);
            System.out.printf("Deposit successful. New Balance: $%.2f%n", account.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric value.");
        }
    }

    private void changePin() {
        System.out.print("Enter current PIN: ");
        String oldPin = scanner.nextLine();
        System.out.print("Enter new 4-digit PIN: ");
        String newPin = scanner.nextLine();
        if (account.changePin(oldPin, newPin)) {
            System.out.println("PIN changed successfully.");
        } else {
            System.out.println("Failed to change PIN. Check current PIN or ensure new PIN is 4 digits.");
        }
    }

    private void showTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        ArrayList<String> history = account.getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (String transaction : history) {
                System.out.println(transaction);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Initialize account with $1000 and PIN "1234"
        Account account = new Account(1000.00, "1234");
        ATM atm = new ATM(account);
        atm.start();
    }
}