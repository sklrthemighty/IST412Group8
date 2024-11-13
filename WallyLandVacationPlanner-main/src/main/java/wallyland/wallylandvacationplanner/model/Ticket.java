/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wallyland.wallylandvacationplanner.model;

/**
 * Represents a ticket.
 * This class stores and manages ticket details such as type and validity.
 */
public class Ticket {
    private String ticketId;
    private String type;
    private double price;
    private boolean isValid;

    public Ticket(String ticketId, String type, double price, boolean isValid) {
        this.ticketId = ticketId;
        this.type = type;
        this.price = price;
        this.isValid = isValid;
    }

    public String getTicketId() { return ticketId; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isValid() { return isValid; }

    @Override
    public String toString() {
        return type + " Ticket ($" + String.format("%.2f", price) + ") " + (isValid ? "Available" : "Out of Stock");
    }
}