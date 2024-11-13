package wallyland.wallylandvacationplanner.view;

import wallyland.wallylandvacationplanner.model.PurchaseService;
import wallyland.wallylandvacationplanner.model.Food;
import wallyland.wallylandvacationplanner.model.Drinks;
import wallyland.wallylandvacationplanner.model.Ticket;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import wallyland.wallylandvacationplanner.model.PurchaseService.Purchase; // Import Purchase class

public class UserInterface extends JFrame {
    // Interface for purchase events
    public interface PurchaseListener {
        boolean onPurchase(String userId, String itemType, String itemId, int quantity);
    }

    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JMenuBar menuBar;

    // Event listener
    private PurchaseListener purchaseListener;

    // Reference to PurchaseService
    private PurchaseService purchaseService;

    // Payment attempt counter
    private int paymentAttemptCount = 0; // Moved here

    public UserInterface(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
        initializeComponents();
        setupMenuBar();
        setupLayout();
        displayUserDashboard();
    }

    private void initializeComponents() {
        setTitle("WallyLand Vacation Planner");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();

        // Create Shop menu
        JMenu shopMenu = new JMenu("Shop");
        JMenuItem ticketsItem = new JMenuItem("Tickets");
        JMenuItem foodItem = new JMenuItem("Food & Drinks");
        shopMenu.add(ticketsItem);
        shopMenu.add(foodItem);

        // Add actions to menu items
        ticketsItem.addActionListener(e -> showPurchaseForm("TICKET"));
        foodItem.addActionListener(e -> showPurchaseForm("FOOD"));

        // Create Cart menu
        JMenu cartMenu = new JMenu("Cart");
        JMenuItem viewCartItem = new JMenuItem("View Cart");
        JMenuItem checkoutItem = new JMenuItem("Checkout");
        cartMenu.add(viewCartItem);
        cartMenu.add(checkoutItem);

        // Add actions to cart items
        viewCartItem.addActionListener(e -> showCart());
        checkoutItem.addActionListener(e -> processCheckout());

        menuBar.add(shopMenu);
        menuBar.add(cartMenu);
        setJMenuBar(menuBar);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public void displayUserDashboard() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to WallyLand!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel instructionsLabel = new JLabel(
            "<html><center>Use the menu above to:<br>" +
            "• Browse and purchase tickets<br>" +
            "• Order food and drinks<br>" +
            "• View your shopping cart</center></html>",
            SwingConstants.CENTER
        );

        dashboardPanel.add(welcomeLabel, BorderLayout.NORTH);
        dashboardPanel.add(instructionsLabel, BorderLayout.CENTER);

        mainPanel.add(dashboardPanel, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    public void showPurchaseForm(String itemType) {
        JPanel purchasePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Item Type Selection
        gbc.gridx = 0; gbc.gridy = 0;
        purchasePanel.add(new JLabel("Item Type:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> itemTypeCombo = new JComboBox<>(new String[]{"TICKET", "FOOD", "DRINK"});
        purchasePanel.add(itemTypeCombo, gbc);

        // Item Selection
        gbc.gridx = 0; gbc.gridy = 1;
        purchasePanel.add(new JLabel("Item:"), gbc);

        gbc.gridx = 1;
        JComboBox<Object> itemCombo = new JComboBox<>();
        updateItemCombo(itemCombo, itemType);
        purchasePanel.add(itemCombo, gbc);

        // Update items when type changes
        itemTypeCombo.addActionListener(e -> {
            String selectedType = (String) itemTypeCombo.getSelectedItem();
            updateItemCombo(itemCombo, selectedType);
        });

        // Quantity Selection
        gbc.gridx = 0; gbc.gridy = 2;
        purchasePanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        purchasePanel.add(quantitySpinner, gbc);

        // Total Price Display
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel totalLabel = new JLabel("Total: $0.00");
        purchasePanel.add(totalLabel, gbc);

        // Update total when quantity or item changes
        Runnable updateTotal = () -> {
            Object selectedItem = itemCombo.getSelectedItem();
            double total = 0.0;
            if (selectedItem instanceof Ticket) {
                total = ((Ticket) selectedItem).getPrice() * (Integer) quantitySpinner.getValue();
            } else if (selectedItem instanceof Food) {
                total = ((Food) selectedItem).getPrice() * (Integer) quantitySpinner.getValue();
            } else if (selectedItem instanceof Drinks) {
                total = ((Drinks) selectedItem).getPrice() * (Integer) quantitySpinner.getValue();
            }
            totalLabel.setText(String.format("Total: $%.2f", total));
        };

        itemCombo.addActionListener(e -> updateTotal.run());
        quantitySpinner.addChangeListener(e -> updateTotal.run());

        // Add to Cart Button
        gbc.gridx = 0; gbc.gridy = 4;
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            Object selectedItem = itemCombo.getSelectedItem();
            if (selectedItem != null) {
                String itemId = "";
                if (selectedItem instanceof Ticket) {
                    itemId = ((Ticket) selectedItem).getTicketId();
                } else if (selectedItem instanceof Food) {
                    itemId = ((Food) selectedItem).getFoodId();
                } else if (selectedItem instanceof Drinks) {
                    itemId = ((Drinks) selectedItem).getDrinkId();
                }
                purchaseService.addToCart("U001", itemId, (Integer) quantitySpinner.getValue());
                showSuccessMessage("Item added to cart!");
            }
        });
        purchasePanel.add(addToCartButton, gbc);

        // Purchase Button
        gbc.gridx = 1; // Move to next column for Purchase button
        JButton purchaseButton = new JButton("Purchase");
        purchaseButton.addActionListener(e -> {
            Object selectedItem = itemCombo.getSelectedItem();
            String itemId = "";
            String itemTypeName = "";
            if (selectedItem != null) {
                if (selectedItem instanceof Ticket) {
                    itemId = ((Ticket) selectedItem).getTicketId();
                    itemTypeName = "TICKET";
                } else if (selectedItem instanceof Food) {
                    itemId = ((Food) selectedItem).getFoodId();
                    itemTypeName = "FOOD";
                } else if (selectedItem instanceof Drinks) {
                    itemId = ((Drinks) selectedItem).getDrinkId();
                    itemTypeName = "DRINK";
                }
                triggerPurchase("U001", itemTypeName, itemId, (Integer) quantitySpinner.getValue());
            }
        });
        purchasePanel.add(purchaseButton, gbc);

        mainPanel.add(purchasePanel, "PURCHASE");
        cardLayout.show(mainPanel, "PURCHASE");
    }

    public void showCart() {
        String userId = "U001"; // Ideally, this should be dynamic
        List<Purchase> purchases = purchaseService.getCartItems(userId);

        JPanel cartPanel = new JPanel(new BorderLayout());
        JTextArea cartTextArea = new JTextArea(15, 50);
        cartTextArea.setEditable(false);
        
        if (purchases.isEmpty()) {
            cartTextArea.setText("Your cart is empty.");
        } else {
            StringBuilder cartContents = new StringBuilder("Items in your cart:\n\n");
            for (Purchase purchase : purchases) {
                cartContents.append(purchase.toString()).append("\n");
            }
            cartTextArea.setText(cartContents.toString());
        }

        cartPanel.add(new JScrollPane(cartTextArea), BorderLayout.CENTER);
        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.addActionListener(e -> processCheckout());
        cartPanel.add(checkoutButton, BorderLayout.SOUTH);

        mainPanel.add(cartPanel, "CART");
        cardLayout.show(mainPanel, "CART");
    }

    private void updateItemCombo(JComboBox<Object> itemCombo, String type) {
        itemCombo.removeAllItems();
        // Update items from PurchaseService
        if (type.equals("TICKET")) {
            for (Object item : purchaseService.getAvailableItems("TICKET")) {
                itemCombo.addItem(item);
            }
        } else if (type.equals("FOOD")) {
            for (Object item : purchaseService.getAvailableItems("FOOD")) {
                itemCombo.addItem(item);
            }
        } else if (type.equals("DRINK")) {
            for (Object item : purchaseService.getAvailableItems("DRINK")) {
                itemCombo.addItem(item);
            }
        }
    }

    public void displaySchedule() {
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.add(new JLabel("Schedule will appear here", SwingConstants.CENTER));

        mainPanel.add(schedulePanel, "SCHEDULE");
        cardLayout.show(mainPanel, "SCHEDULE");
    }

    // Panel management methods
    public void addPanel(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    // Purchase handling methods
    public void addPurchaseListener(PurchaseListener listener) {
        this.purchaseListener = listener;
    }

    protected void triggerPurchase(String userId, String itemType, String itemId, int quantity) {
        if (purchaseListener != null) {
            if (purchaseListener.onPurchase(userId, itemType, itemId, quantity)) {
                showSuccessMessage("Purchase successful!");
            } else {
                showErrorMessage("Purchase failed. Please try again.");
            }
        }
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void processCheckout() {
        String[] paymentOptions = {"Credit", "Debit", "App"};
        String paymentMethod = (String) JOptionPane.showInputDialog(
            this,
            "Select Payment Method:",
            "Checkout",
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentOptions,
            paymentOptions[0] // default selection
        );

        if (paymentMethod != null) { // User didn't cancel
            paymentAttemptCount++; // Increment the payment attempt count

            // Determine payment success: every third payment fails
            boolean paymentSuccess = (paymentAttemptCount % 3) != 0;

            if (paymentSuccess) {
                showSuccessMessage("Payment successful! Thank you for your purchase.");
                // Optionally, you can clear the cart here
                // purchaseService.clearCart("U001");
            } else {
                showErrorMessage("Payment failed. Please try again.");
            }
        } else {
            showErrorMessage("Checkout canceled.");
        }
    }
}
