package Java_Programming_Task2;

import java.io.*;
import java.util.*;

public class Portfolio implements Serializable {
    private Map<String, Integer> holdings;
    private double cashBalance;
    private List<Transaction> transactionHistory;

    public Portfolio() {
        this.holdings = new HashMap<>();
        this.cashBalance = 10000.00;
        this.transactionHistory = new ArrayList<>();
    }

    public boolean buyStock(Stock stock, int quantity) {
        double cost = stock.getPrice() * quantity;

        if (cost > cashBalance) {
            return false;
        }

        holdings.put(stock.getSymbol(),
                holdings.getOrDefault(stock.getSymbol(), 0) + quantity);
        cashBalance -= cost;

        transactionHistory.add(new Transaction(
                "BUY", stock.getSymbol(), quantity, stock.getPrice(), cost));

        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        if (!holdings.containsKey(stock.getSymbol()) ||
                holdings.get(stock.getSymbol()) < quantity) {
            return false;
        }

        double saleValue = stock.getPrice() * quantity;
        holdings.put(stock.getSymbol(), holdings.get(stock.getSymbol()) - quantity);

        if (holdings.get(stock.getSymbol()) == 0) {
            holdings.remove(stock.getSymbol());
        }

        cashBalance += saleValue;

        transactionHistory.add(new Transaction(
                "SELL", stock.getSymbol(), quantity, stock.getPrice(), saleValue));

        return true;
    }

    public double getPortfolioValue(List<Stock> stocks) {
        double totalValue = cashBalance;
        for (String symbol : holdings.keySet()) {
            for (Stock stock : stocks) {
                if (stock.getSymbol().equals(symbol)) {
                    totalValue += stock.getPrice() * holdings.get(symbol);
                    break;
                }
            }
        }
        return totalValue;
    }

    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Portfolio saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving portfolio: " + e.getMessage());
        }
    }

    public static Portfolio loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (Portfolio) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading portfolio: " + e.getMessage());
            return new Portfolio();
        }
    }

    // Getters
    public Map<String, Integer> getHoldings() { return holdings; }
    public double getCashBalance() { return cashBalance; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
}