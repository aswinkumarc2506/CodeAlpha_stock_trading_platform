package Java_Programming_Task2;

import java.util.*;

public class StockTradingPlatform {
    private static List<Stock> stocks;
    private static Portfolio portfolio;
    private static Scanner scanner;
    private static final String DATA_FILE = "portfolio.dat";

    public static void main(String[] args) {
        initializeStocks();
        portfolio = Portfolio.loadFromFile(DATA_FILE);
        scanner = new Scanner(System.in);

        System.out.println("=== Java Stock Trading Platform ===");

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    viewMarket();
                    break;
                case 2:
                    viewPortfolio();
                    break;
                case 3:
                    buyStock();
                    break;
                case 4:
                    sellStock();
                    break;
                case 5:
                    viewTransactions();
                    break;
                case 6:
                    simulateMarket();
                    System.out.println("Market data updated!");
                    break;
                case 7:
                    portfolio.saveToFile(DATA_FILE);
                    break;
                case 8:
                    running = false;
                    portfolio.saveToFile(DATA_FILE);
                    System.out.println("Thank you for using Java Stock Trading Platform!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    private static void initializeStocks() {
        stocks = new ArrayList<>();
        stocks.add(new Stock("AAPL", "Apple Inc.", 165.42, 1.25, 0.76));
        stocks.add(new Stock("MSFT", "Microsoft Corp.", 315.76, -2.34, -0.74));
        stocks.add(new Stock("GOOGL", "Alphabet Inc.", 130.25, 3.12, 2.46));
        stocks.add(new Stock("AMZN", "Amazon.com Inc.", 145.18, 0.87, 0.60));
        stocks.add(new Stock("TSLA", "Tesla Inc.", 245.63, -5.42, -2.16));
    }

    private static void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. View Market");
        System.out.println("2. View Portfolio");
        System.out.println("3. Buy Stock");
        System.out.println("4. Sell Stock");
        System.out.println("5. View Transaction History");
        System.out.println("6. Simulate Market Change");
        System.out.println("7. Save Portfolio");
        System.out.println("8. Exit");
    }

    private static void viewMarket() {
        System.out.println("\n=== STOCK MARKET ===");
        System.out.println("Symbol     Name                 Price      Change     Change %");
        System.out.println("-------------------------------------------------------------");

        for (Stock stock : stocks) {
            System.out.printf("%-10s %-20s $%-9.2f %s%-9.2f %s%-7.2f%%%n",
                    stock.getSymbol(),
                    stock.getName(),
                    stock.getPrice(),
                    stock.getChange() >= 0 ? "+" : "",
                    stock.getChange(),
                    stock.getChangePercent() >= 0 ? "+" : "",
                    stock.getChangePercent());
        }
    }

    private static void viewPortfolio() {
        System.out.println("\n=== PORTFOLIO ===");
        System.out.printf("Cash Balance: $%.2f%n", portfolio.getCashBalance());
        System.out.printf("Portfolio Value: $%.2f%n", portfolio.getPortfolioValue(stocks));

        System.out.println("\nHoldings:");
        System.out.println("Symbol     Quantity    Current Price    Value");
        System.out.println("---------------------------------------------");

        Map<String, Integer> holdings = portfolio.getHoldings();
        if (holdings.isEmpty()) {
            System.out.println("No holdings yet.");
        } else {
            for (String symbol : holdings.keySet()) {
                Stock stock = findStock(symbol);
                if (stock != null) {
                    int quantity = holdings.get(symbol);
                    double value = stock.getPrice() * quantity;
                    System.out.printf("%-10s %-11d $%-15.2f $%.2f%n",
                            symbol, quantity, stock.getPrice(), value);
                }
            }
        }
    }

    private static void buyStock() {
        viewMarket();
        System.out.print("\nEnter stock symbol to buy: ");
        String symbol = scanner.nextLine().toUpperCase();

        Stock stock = findStock(symbol);
        if (stock == null) {
            System.out.println("Invalid stock symbol.");
            return;
        }

        int quantity = getIntInput("Enter quantity: ");
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        if (portfolio.buyStock(stock, quantity)) {
            System.out.printf("Successfully bought %d shares of %s for $%.2f%n",
                    quantity, symbol, stock.getPrice() * quantity);
        } else {
            System.out.println("Failed to buy stock. Insufficient funds.");
        }
    }

    private static void sellStock() {
        Map<String, Integer> holdings = portfolio.getHoldings();
        if (holdings.isEmpty()) {
            System.out.println("You don't have any stocks to sell.");
            return;
        }

        System.out.println("\nYour Holdings:");
        for (String symbol : holdings.keySet()) {
            System.out.printf("%s: %d shares%n", symbol, holdings.get(symbol));
        }

        System.out.print("\nEnter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();

        if (!holdings.containsKey(symbol)) {
            System.out.println("You don't own any shares of " + symbol);
            return;
        }

        Stock stock = findStock(symbol);
        if (stock == null) {
            System.out.println("Invalid stock symbol.");
            return;
        }

        int maxQuantity = holdings.get(symbol);
        int quantity = getIntInput("Enter quantity (max: " + maxQuantity + "): ");

        if (quantity <= 0 || quantity > maxQuantity) {
            System.out.println("Invalid quantity.");
            return;
        }

        if (portfolio.sellStock(stock, quantity)) {
            System.out.printf("Successfully sold %d shares of %s for $%.2f%n",
                    quantity, symbol, stock.getPrice() * quantity);
        } else {
            System.out.println("Failed to sell stock.");
        }
    }

    private static void viewTransactions() {
        List<Transaction> transactions = portfolio.getTransactionHistory();
        System.out.println("\n=== TRANSACTION HISTORY ===");

        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private static void simulateMarket() {
        for (Stock stock : stocks) {
            stock.updatePrice();
        }
    }

    private static Stock findStock(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }
        return null;
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return value;
    }
}