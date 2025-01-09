package Projects;
import java.io.*;
import java.util.*;
import java.io.Serializable;

class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String accountHolderName;
    private String pin;
    private double balance;

    public BankAccount(String accountNumber, String accountHolderName, String pin) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = 0.0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public boolean verifyPin(String inputPin) {
        return pin.equals(inputPin);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient balance or invalid amount.");
        }
    }
}

class BankManagementSystem {
    private static final String DATA_FILE = "accounts.dat";
    private Map<String, BankAccount> accounts = new HashMap<>();

    public BankManagementSystem() {
        loadAccounts();
    }

    public void createAccount(String accountHolderName, String accountNumber, String pin) {
        if (accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account number already exists.");
        }
        BankAccount account = new BankAccount(accountNumber, accountHolderName, pin);
        accounts.put(accountNumber, account);
        saveAccounts();
    }

    public BankAccount getAccount(String accountNumber, String pin) {
        BankAccount account = accounts.get(accountNumber);
        if (account == null || !account.verifyPin(pin)) {
            throw new IllegalArgumentException("Invalid account number or PIN.");
        }
        return account;
    }

    public void deleteAccount(String accountNumber, String pin) {
        BankAccount account = accounts.get(accountNumber);
        if (account == null || !account.verifyPin(pin)) {
            throw new IllegalArgumentException("Invalid account number or PIN.");
        }

        // Remove the account from the map
        accounts.remove(accountNumber);

        // Save the updated accounts to the file
        saveAccounts();
        System.out.println("Account deleted successfully.");
    }

    public void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAccounts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            accounts = (Map<String, BankAccount>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }
}

public class SecureBankApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BankManagementSystem bankSystem = new BankManagementSystem();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Bank Management System ===");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Check Balance");
            System.out.println("5. Delete Account");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> createAccount();
                    case 2 -> depositMoney();
                    case 3 -> withdrawMoney();
                    case 4 -> checkBalance();
                    case 5 -> deleteAccount();
                    case 6 -> {
                        System.out.println("Exiting the system...");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void createAccount() {
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Set a 4-digit PIN: ");
        String pin = scanner.nextLine();

        if (pin.length() != 4 || !pin.matches("\\d+")) {
            System.out.println("PIN must be a 4-digit number.");
            return;
        }

        bankSystem.createAccount(name, accountNumber, pin);
        System.out.println("Account created successfully!");
    }

    private static void depositMoney() {
        BankAccount account = authenticate();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        account.deposit(amount);
        bankSystem.saveAccounts();
        System.out.println("Deposit successful. New balance: $" + account.getBalance());
    }

    private static void withdrawMoney() {
        BankAccount account = authenticate();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        try {
            account.withdraw(amount);
            bankSystem.saveAccounts();
            System.out.println("Withdrawal successful. Remaining balance: $" + account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkBalance() {
        BankAccount account = authenticate();
        System.out.println("Your current balance: $" + account.getBalance());
    }

    private static void deleteAccount() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        try {
            bankSystem.deleteAccount(accountNumber, pin);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static BankAccount authenticate() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        return bankSystem.getAccount(accountNumber, pin);
    }
}
