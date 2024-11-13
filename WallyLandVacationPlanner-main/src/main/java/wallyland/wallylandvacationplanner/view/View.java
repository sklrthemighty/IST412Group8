/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wallyland.wallylandvacationplanner.view;

import wallyland.wallylandvacationplanner.view.UserInterface;
import wallyland.wallylandvacationplanner.view.AdminInterface;
import wallyland.wallylandvacationplanner.model.PurchaseService;
/**
 *
 * @author chris
 */


public class View {
    private UserInterface userInterface;
    private AdminInterface adminInterface;

    public View(PurchaseService purchaseService) {
        userInterface = new UserInterface(purchaseService); // Pass the PurchaseService here
        adminInterface = new AdminInterface();
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public AdminInterface getAdminInterface() {
        return adminInterface;
    }
}