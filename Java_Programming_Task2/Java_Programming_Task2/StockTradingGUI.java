package Java_Programming_Task2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockTradingGUI extends JFrame {
    private List<Stock> stocks;
    private Portfolio portfolio;
    private final String DATA_FILE = "portfolio.dat";

    private JTable marketTable;
    private JTable portfolioTable;
    private JLabel cashLabel;
    private JLabel valueLabel;

    public StockTradingGUI() {
        initializeStocks();
        portfolio = Portfolio.loadFromFile(DATA_FILE);

        setTitle("Java Stock Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        createMenuBar();
        createMainPanel();

        setVisible(true);
    }

    private void initializeStocks() {
        stocks = java.util.Arrays.asList(
                new Stock("AAPL", "Apple Inc.", 165.42, 1.25, 0.76),
                new Stock("MSFT", "Microsoft Corp.", 315.76, -2.34, -0.74),
                new Stock("GOOGL", "Alphabet Inc.", 130.25, 3.12, 2.46),
                new Stock("AMZN", "Amazon.com Inc.", 145.18, 0.87, 0.60),
                new Stock("TSLA", "Tesla Inc.", 245.63, -5.42, -2.16)
        );
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Portfolio");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(e -> portfolio.saveToFile(DATA_FILE));
        exitItem.addActionListener(e -> {
            portfolio.saveToFile(DATA_FILE);
            System.exit(0);
        });

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu actionMenu = new JMenu("Actions");
        JMenuItem refreshItem = new JMenuItem("Refresh Market");
        JMenuItem buyItem = new JMenuItem("Buy Stock");
        JMenuItem sellItem = new JMenuItem("Sell Stock");

        refreshItem.addActionListener(e -> refreshMarket());
        buyItem.addActionListener(e -> showBuyDialog());
        sellItem.addActionListener(e -> showSellDialog());

        actionMenu.add(refreshItem);
        actionMenu.addSeparator();
        actionMenu.add(buyItem);
        actionMenu.add(sellItem);

        menuBar.add(fileMenu);
        menuBar.add(actionMenu);

        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Market tab
        JPanel marketPanel = new JPanel(new BorderLayout());
        marketTable = createMarketTable();
        marketPanel.add(new JScrollPane(marketTable), BorderLayout.CENTER);

        JPanel marketButtonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Market");
        JButton buyButton = new JButton("Buy Selected");

        refreshButton.addActionListener(e -> refreshMarket());
        buyButton.addActionListener(e -> buySelectedStock());

        marketButtonPanel.add(refreshButton);
        marketButtonPanel.add(buyButton);
        marketPanel.add(marketButtonPanel, BorderLayout.SOUTH);

        // Portfolio tab
        JPanel portfolioPanel = new JPanel(new BorderLayout());

        JPanel portfolioInfoPanel = new JPanel(new GridLayout(1, 2));
        cashLabel = new JLabel("Cash: $10000.00");
        valueLabel = new JLabel("Portfolio Value: $10000.00");
        portfolioInfoPanel.add(cashLabel);
        portfolioInfoPanel.add(valueLabel);

        portfolioTable = createPortfolioTable();

        portfolioPanel.add(portfolioInfoPanel, BorderLayout.NORTH);
        portfolioPanel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);

        JPanel portfolioButtonPanel = new JPanel();
        JButton sellButton = new JButton("Sell Selected");
        sellButton.addActionListener(e -> sellSelectedStock());
        portfolioButtonPanel.add(sellButton);
        portfolioPanel.add(portfolioButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Market", marketPanel);
        tabbedPane.addTab("Portfolio", portfolioPanel);

        add(tabbedPane);
        updatePortfolioInfo();
    }

    private JTable createMarketTable() {
        String[] columns = {"Symbol", "Name", "Price", "Change", "Change %"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Stock stock : stocks) {
            Object[] row = {
                    stock.getSymbol(),
                    stock.getName(),
                    String.format("$%.2f", stock.getPrice()),
                    String.format("%s%.2f", stock.getChange() >= 0 ? "+" : "", stock.getChange()),
                    String.format("%s%.2f%%", stock.getChangePercent() >= 0 ? "+" : "", stock.getChangePercent())
            };
            model.addRow(row);
        }

        return new JTable(model);
    }

    private JTable createPortfolioTable() {
        String[] columns = {"Symbol", "Quantity", "Current Price", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        return new JTable(model);
    }

    private void refreshMarket() {
        for (Stock stock : stocks) {
            stock.updatePrice();
        }

        DefaultTableModel model = (DefaultTableModel) marketTable.getModel();
        model.setRowCount(0);

        for (Stock stock : stocks) {
            Object[] row = {
                    stock.getSymbol(),
                    stock.getName(),
                    String.format("$%.2f", stock.getPrice()),
                    String.format("%s%.2f", stock.getChange() >= 0 ? "+" : "", stock.getChange()),
                    String.format("%s%.2f%%", stock.getChangePercent() >= 0 ? "+" : "", stock.getChangePercent())
            };
            model.addRow(row);
        }

        updatePortfolioInfo();
    }

    private void updatePortfolioInfo() {
        cashLabel.setText(String.format("Cash: $%.2f", portfolio.getCashBalance()));
        valueLabel.setText(String.format("Portfolio Value: $%.2f", portfolio.getPortfolioValue(stocks)));

        DefaultTableModel model = (DefaultTableModel) portfolioTable.getModel();
        model.setRowCount(0);

        for (String symbol : portfolio.getHoldings().keySet()) {
            Stock stock = findStock(symbol);
            if (stock != null) {
                int quantity = portfolio.getHoldings().get(symbol);
                double value = stock.getPrice() * quantity;
                Object[] row = {
                        symbol,
                        quantity,
                        String.format("$%.2f", stock.getPrice()),
                        String.format("$%.2f", value)
                };
                model.addRow(row);
            }
        }
    }

    private void showBuyDialog() {
        int selectedRow = marketTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock to buy.");
            return;
        }

        String symbol = (String) marketTable.getValueAt(selectedRow, 0);
        Stock stock = findStock(symbol);

        String input = JOptionPane.showInputDialog(this,
                "Enter quantity to buy of " + symbol + ":", "1");

        if (input != null) {
            try {
                int quantity = Integer.parseInt(input);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                    return;
                }

                if (portfolio.buyStock(stock, quantity)) {
                    JOptionPane.showMessageDialog(this,
                            String.format("Bought %d shares of %s for $%.2f",
                                    quantity, symbol, stock.getPrice() * quantity));
                    updatePortfolioInfo();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }
    }

    private void buySelectedStock() {
        showBuyDialog();
    }

    private void showSellDialog() {
        int selectedRow = portfolioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock to sell.");
            return;
        }

        String symbol = (String) portfolioTable.getValueAt(selectedRow, 0);
        int maxQuantity = (Integer) portfolioTable.getValueAt(selectedRow, 1);
        Stock stock = findStock(symbol);

        String input = JOptionPane.showInputDialog(this,
                "Enter quantity to sell of " + symbol + " (max: " + maxQuantity + "):", "1");

        if (input != null) {
            try {
                int quantity = Integer.parseInt(input);
                if (quantity <= 0 || quantity > maxQuantity) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity.");
                    return;
                }

                if (portfolio.sellStock(stock, quantity)) {
                    JOptionPane.showMessageDialog(this,
                            String.format("Sold %d shares of %s for $%.2f",
                                    quantity, symbol, stock.getPrice() * quantity));
                    updatePortfolioInfo();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to sell stock.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }
    }

    private void sellSelectedStock() {
        showSellDialog();
    }

    private Stock findStock(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockTradingGUI::new);
    }
}