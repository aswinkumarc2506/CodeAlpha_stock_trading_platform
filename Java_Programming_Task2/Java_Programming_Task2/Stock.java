package Java_Programming_Task2;

public class Stock {
    private String symbol;
    private String name;
    private double price;
    private double change;
    private double changePercent;

    public Stock(String symbol, String name, double price, double change, double changePercent) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
    }

    // Getters and setters
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getChange() { return change; }
    public double getChangePercent() { return changePercent; }

    public void setPrice(double price) {
        this.change = price - this.price;
        this.changePercent = (change / this.price) * 100;
        this.price = price;
    }

    public void updatePrice() {
        double fluctuation = (Math.random() - 0.5) * 4;
        setPrice(price + fluctuation);
    }

    @Override
    public String toString() {
        return String.format("%s (%s): $%.2f %s%.2f (%s%.2f%%)",
                name, symbol, price,
                change >= 0 ? "+" : "", change,
                changePercent >= 0 ? "+" : "", changePercent);
    }
}