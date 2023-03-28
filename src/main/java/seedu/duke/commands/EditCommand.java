package seedu.duke.commands;

import seedu.duke.exceptions.MissingParametersException;
import seedu.duke.objects.Inventory;
import seedu.duke.objects.Item;
import seedu.duke.utils.SessionManager;
import seedu.duke.utils.Ui;
import seedu.duke.exceptions.EditErrorException;

import java.util.ArrayList;

/**
 * Represents the command to edit an item in the inventory.
 */
public class EditCommand extends Command {

    private final String[] editInfo;
    private static final String NAME_LABEL = "n/";
    private static final String QUANTITY_LABEL = "qty/";
    private static final String PRICE_LABEL = "p/";

    public EditCommand(Inventory inventory, String[] editInfo) {
        super(inventory);
        this.editInfo = editInfo;
    }

    /**
     * Searches the Hashmap to obtain the item required to be interacted with by the user.
     *
     * @param editInfo The array of strings that contain the user inputs.
     * @return Returns the variable of type "Item", which is the item in question to be interacted with by the user.
     * @throws EditErrorException Exception related to all errors generated by the edit command.
     */
    public Item retrieveItemFromHashMap(final String[] editInfo) throws EditErrorException {
        String upcCode = editInfo[0].replaceFirst("upc/", "");
        if (!upcCodes.containsKey(upcCode)) {
            throw new EditErrorException();
        }
        return upcCodes.get(upcCode);
    }

    /**
     * Executes method to edit item attributes in the list and prints an error string if the user's edit command
     * inputs were incorrectly written.
     *
     * @param item The target item in which the user wants to edit.
     * @param data The user input which contains the information to be used to update the item attributes.
     * @throws MissingParametersException Exception related to all errors due to missing parameters.
     * @throws NumberFormatException Exception related to all invalid number formats inputted.
     */
    public void updateItemInfo(final Item item, final String[] data) throws
            MissingParametersException, NumberFormatException {
        try {
            handleUserEditCommands(item, data);
        } catch (MissingParametersException mpe) {
            throw new MissingParametersException();
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException();
        }
    }

    /**
     * Detects specific chars in the array of individual strings, and executes the change of item attribute values
     * (i.e, Name, Quantity, Price) based on the first few chars detected in the individual string.
     *
     * @param item The target item in which the user wants to edit.
     * @param data The user input which contains the information to be used to update the item attributes.
     * @throws MissingParametersException Exception related to all errors due to missing parameters.
     * @throws NumberFormatException Exception related to all invalid number formats inputted.
     */
    private void handleUserEditCommands(Item item, String[] data) throws
            MissingParametersException, NumberFormatException {
        String currentLabel = "null";
        for (int dataSequence = 1; dataSequence < data.length; dataSequence += 1) {
            if (data[dataSequence].contains("n/")) {
                String newName = data[dataSequence].replaceFirst("n/", "");
                item.setName(newName);
                currentLabel = NAME_LABEL;
            } else if (data[dataSequence].contains("qty/")) {
                String updatedQuantity = data[dataSequence].replaceFirst("qty/", "");
                setItemQuantity(item, updatedQuantity);
                currentLabel = QUANTITY_LABEL;
            } else if (data[dataSequence].contains("p/")) {
                String updatedPrice = data[dataSequence].replaceFirst("p/", "");
                setItemPrice(item, updatedPrice);
                currentLabel = PRICE_LABEL;
            } else {
                if (currentLabel.equals(NAME_LABEL)) {
                    System.out.println(dataSequence);
                    System.out.println(data[dataSequence]);
                    item.setName(item.getName() + " " + data[dataSequence]);
                } else {
                    throw new MissingParametersException();
                }
            }
        }
    }

    /**
     * Sets the item price to a specific value according to the user input.
     *
     * @param item The target item in which the user wants to edit.
     * @param updatedPrice The new price of the item.
     * @throws NumberFormatException Exception related to all number conversion errors.
     */
    private static void setItemPrice(Item item, String updatedPrice) throws NumberFormatException{
        try {
            Double newPrice = Double.valueOf(updatedPrice);
            item.setPrice(newPrice);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException();
        }
    }

    /**
     * Sets the item quantity to a specific value according to the user input.
     *
     * @param item The target item in which the user wants to edit.
     * @param updatedQuantity The new quantity of the item.
     * @throws NumberFormatException Exception related to all number conversion errors.
     */
    private static void setItemQuantity(Item item, String updatedQuantity) throws NumberFormatException{
        try {
            Integer newQuantity = Integer.valueOf(updatedQuantity);
            item.setQuantity(newQuantity);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException();
        }
    }

    /**
     * Edit Command that searches for the item in the ArrayList and changes the item attributes according
     * to the wishes of the user.
     */
    public void setEditInfo() {
        try {
            Item updatedItem = retrieveItemFromHashMap(editInfo);
            Item oldItem = new Item(updatedItem.getName(), updatedItem.getUpc(), updatedItem.getQuantity(),
                    updatedItem.getPrice(), updatedItem.getCategory(), updatedItem.getTags());
            //for (int data = 1; data < editInfo.length; data += 1) {
                updateItemInfo(updatedItem, editInfo);
            //}
            Item itemForHistory = new Item(updatedItem.getName(), updatedItem.getUpc(), updatedItem.getQuantity(),
                    updatedItem.getPrice(), updatedItem.getCategory(), updatedItem.getTags());
            handleTrie(updatedItem, oldItem);
            upcCodes.remove(oldItem.getUpc());
            upcCodes.put(updatedItem.getUpc(), updatedItem);
            Ui.printEditDetails(oldItem, updatedItem);

            inventory.getAlertList().checkAlerts(updatedItem.getUpc(), updatedItem.getName(),
                    upcCodes.get(updatedItem.getUpc()).getQuantity().intValue());

            inventory.getUpcCodesHistory().get(oldItem.getUpc()).add(itemForHistory);
            if (SessionManager.getAutoSave()) {
                SessionManager.writeSession(inventory);
            }
        } catch (EditErrorException eee) {
            Ui.printItemNotFound();
        } catch (MissingParametersException mpe) {
            Ui.printInvalidEditCommand();
        } catch (NumberFormatException nfe) {
            Ui.printInvalidPriceOrQuantityEditInput();
        }
    }

    public void handleTrie(Item updatedItem, Item oldItem) {
        String[] oldItemNames = oldItem.getName().toLowerCase().split(" ");
        String newItemNamesFull = updatedItem.getName().toLowerCase();
        for(String oldItemName: oldItemNames) {
            if (!newItemNamesFull.contains(oldItemName) && itemNameHash.get(oldItemName).size() == 1) {
                itemNameHash.remove(oldItemName);
                itemsTrie.remove(oldItemName);
            } else {
                itemNameHash.get(oldItemName).remove(oldItem);
            }
        }
        String[] newItemNames = newItemNamesFull.split(" ");
        for(String newItemName: newItemNames){
            if(!itemNameHash.containsKey(newItemName)){
                itemNameHash.put(newItemName, new ArrayList<>());
            }
            itemNameHash.get(newItemName).add(updatedItem);
            itemsTrie.add(newItemName);
        }
    }

    /**
     * Executes the Edit Command
     */
    @Override
    public void run() {
        setEditInfo();
    }
}
