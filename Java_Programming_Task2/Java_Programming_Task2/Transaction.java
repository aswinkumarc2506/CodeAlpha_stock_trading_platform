package Java_Programming_Task2;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String type;
    private String symbol;
    private int quantity;
    private double price;
    private double total;
    private Date timestamp;

    public Transaction(String type, String symbol, int quantity,
                       double price, double total) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %d shares of %s at $%.2f (Total: $%.2f) - %s",
                timestamp, type, quantity, symbol, price, total, timestamp);
    }

    // Getters
    public String getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
    public Date getTimestamp() { return timestamp; }
}