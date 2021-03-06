import SDM.*;
import SDM.Exception.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class SDMConsoleUI
{
    private final SDMEngine engine = new SDMEngine();
    private final String[] mainMenu = {"Load new XML file.", "Show all stores information", "Show all items information.", "Make new order.", "Show all orders.", "Exit."};

    public SDMConsoleUI() {
    }

    public void start()
    {
        boolean isFinished;
        int optionChoose;

        do {
            showMainMenu();
            optionChoose = getValidOption(mainMenu.length);
            isFinished = executeOptionChoose(optionChoose, mainMenu.length);
            System.out.println();
        } while (!isFinished);
    }

    //region main menu showing and option choosing
    private void showMainMenu() {
        for (int i = 0; i < mainMenu.length; i++) {
            System.out.println(i + 1 + "." + mainMenu[i]);
        }
    }

    private int getValidOption(int optionMaxBound) {
        Scanner scannerFromConsole = new Scanner(System.in);
        boolean isValidOption = false;
        int optionChoose = 1;

        do {
            try {
                System.out.print("Please enter your choice: ");
                optionChoose = Integer.parseInt(scannerFromConsole.nextLine());
                if (optionChoose >= 1 && optionChoose <= optionMaxBound) {
                    isValidOption = true;
                } else {
                    System.out.println("Please give a number in the range of 1 to " + optionMaxBound + "\n");
                }
            } catch (Exception ex) {
                System.out.println("Please give an integer in the range of 1 to " + optionMaxBound + "\n");
            }
        } while (!isValidOption);

        return optionChoose;
    }

    private boolean executeOptionChoose(int choice, int exitOption) {
        if (choice == exitOption) {
            return true;
        }
        if(choice == 1) {
            loadNewXML();
        }
        else {
            if(engine.isXMLFileLoaded()){
                switch (choice) {
                    case 2:
                        showAllStores();
                        break;
                    case 3:
                        showAllItems();
                        break;
                    case 4:
                        MakeNewOrder();
                        break;
                    case 5:
                        showAllOrders();
                        break;
                }
            }
            else{
                System.out.println("You must load an XML file before using feature from 2-5.");
            }
        }

        return false;
    }
    //endregion

    //region load new XML file
    private void loadNewXML() {
        Scanner xmlFilePathScanner = new Scanner(System.in);
        System.out.println("Please enter your XML file path: ");
        String filePath = xmlFilePathScanner.nextLine();

        try {
            engine.updateAllStoresAndAllItems(filePath);
            System.out.println("XML file loaded successfully!");
        }
        catch (FileNotFoundException ex) {
            System.out.println("ERROR: The file does not exist in the path given, please try again.");
        }
        catch(FileNotEndWithXMLException ex) {
            System.out.println("ERROR: The file you given extension is" + ex.getFileType() + " and not an XML file, please make sure it ends with .xml and try again.");
        }
        catch(LocationIsOutOfBorderException ex) {
            System.out.println("ERROR: The object of type " +  ex.getLocatableType()+
                    " with id of: " + ex.getId() + " is located out of allowed borders which are between "
                    + ex.getMinBorder() + "to " + ex.getMaxBorder() + ".Please fix this.");
        }
        catch(DuplicateStoreItemException ex) {
            System.out.println("ERROR: The store item with ID of " + ex.getId() + " appears more than once in the XML file.");
        }
        catch(DuplicateItemException ex) {
            System.out.println("ERROR: The item with ID of " + ex.getId() + " appears more than once in the XML file.");
        }
        catch (TryingToGiveDifferentPricesForSameStoreItemException e) {
            System.out.println("ERROR: The file has store with ID" + e.getStoreId() + "that try to give an item price multiple time. ");
        }
        catch (TryingToGivePriceOfItemWhichIDNotExistException ex) {
            System.out.println("ERROR: The file has store which trying to give a price of item which is ID "+ex.getId()+" does not exist");
        }
        catch(DuplicateStoreIDException ex) {
            System.out.println("ERROR: The store with ID of " + ex.getId() + " appears more than once in the XML file");
        }
        catch (ItemNoOneSellException ex) {
            System.out.println("ERROR: The item with ID " + ex.getId() + " doesnt sold by any store.");
        }
        catch (StoreWithNoItemException ex) {
            System.out.println("ERROR: The store with ID " + ex.getId() + " doesnt sell any items");
        }
        catch(Exception ex) {
            System.out.println("ERROR: Unknown error has happen, the error message is: " + ex.getMessage());
        }
    }
    //endregion

    //region show all stores
    private void showAllStores() {
        int i = 1;
        System.out.println();
        for (Store store : engine.getAllStores()) {
            System.out.println("####STORE NUMBER " + i + "####");
            showStore(store);
            i++;
            System.out.println();
        }
    }

    private void showStore(Store store) {
        final AtomicInteger i = new AtomicInteger(1); //the only way to have an "i" in foreach loop inside of map is in this way, atomic integer
        //and the getAndIncrement method...

        System.out.println("1.ID:" + store.getId());
        System.out.println("2.Name:" + store.getName());
        System.out.println("3.Items sold by this store:");
        store.getItemsThatSellInThisStore().forEach(((storeItemID, storeItem) -> {
            //System.out.print(i.getAndIncrement() + ".");
            System.out.println("    ##Store-Item number " + i.getAndIncrement() + "##");
            showStoreItem(storeItem);
        }));


        System.out.println("4.Orders made by this store:");
        i.set(1);
        if (store.getOrders().isEmpty()) {
            System.out.println("This store hadn't made any orders yet.");
        } else {
            for (Order order : store.getOrders()) {
                System.out.println("\t##ORDER NUMBER " + i.getAndIncrement() + "##");
                showOrderInShowStoreChoice(order);
            }
        }
        System.out.println("5.PPK: " + store.getDeliveryPPK());
        System.out.println(String.format("6.Amount of money got only for deliveries: %.2f", store.getTotalAmountForDeliveries()));
    }

    private void showStoreItem(StoreItem storeItem) {
        showItemBasicData(storeItem.getItem(), "\t", 'a');
        if (storeItem.getItem().getType() == Item.ItemType.QUANTITY) {
            System.out.println('\t' + "    d.price for 1 item is: " + storeItem.getPrice());
        } else {
            System.out.println('\t' + "    d.price for 1kg is: " + storeItem.getPrice());
        }


        //System.out.println('\t' + "    e.Total sold: " + storeItem.getTotalAmountSoldInThisStore());
        double amountThatSold=storeItem.getTotalAmountSoldInThisStore();
        if(amountThatSold==0)
        {
            System.out.println('\t' + "    e.Total sold: 0 ");
        }
        else{
            System.out.println('\t' + String.format("    e.Total sold: %.2f", amountThatSold));
        }

    }

    private void showOrderInShowStoreChoice(Order order) {

        System.out.println("\t\ta.Date of order: " + (new SimpleDateFormat("dd/MM-HH:mm")).format(order.getDate()));
        System.out.println("\t\tb.Total items: " + order.getTotalItemsInOrder());//Noy's job---> method in Order "getTotalItemsInOrder".
        System.out.println(String.format("\t\tc.Total price of all items: %.2f", order.getPriceOfAllItems()));
        System.out.println(String.format("\t\td.Delivery price: %.2f", order.getDeliveryPrice()));
        System.out.println(String.format("\t\te.Total order price: %.2f", order.getTotalPrice()));
    }

    //endregion

    //region show all items
    private void showAllItems() {
        int i = 1;
        for (Item item : engine.getAllItems()) {
            System.out.println("####ITEM NUMBER " + i + "####");
            showItem(item);
            i++;
            System.out.println();
        }
    }

    private void showItem(Item item) {
        showItemBasicData(item, "", '1');
        System.out.println("    4.Number of stores sell this item: " + item.getStoresSellThisItem().size());

        //System.out.println("    5.Average price of this item: " +item.getAveragePrice());
        double avgPrice=item.getAveragePrice();
        avgPrice = Math.floor(avgPrice * 100) / 100;
        System.out.println("    5.Average price of this item: " +avgPrice);

        System.out.println("    6.Total amount that has been sold: " + item.getTotalAmountSoldOnAllStores() + (item.getType() == Item.ItemType.QUANTITY ? " pieces" : " KG"));
    }
    //endregion

    //region make new order
    private void MakeNewOrder()
    {
        int chooseStore = getFromUserChooseStore();
        Date dateOrder = getValidDateFromCostumer();
        Location costumerLocation = getValidCostumerLocation();
        Costumer costumerEX1 = new Costumer(1234, "user", costumerLocation);
        engine.createNewOrder(costumerEX1, dateOrder, engine.getAllStoresMap().get(chooseStore));

        getFromUserItemsAndAmountAndUpdateTheOrder(chooseStore);
        if(engine.getCurrentOrder().isEmpty()) {
            System.out.println("You have not added any item to your order, going back to main menu.");
        }
        else {
            showOrderSummary(engine.getCurrentOrder());
            System.out.println("Do you want to confirm the order? (Y/N)");
            String confirmationChoice = getValidOrderConfirmation();
            if (confirmationChoice.toLowerCase().equals("y")) {
                engine.completeCurrentOrder();
                System.out.println("Order confirmed, thank you for using our SDM system.");
            } else {
                System.out.println("Order canceled, hope to see you waisting your money on us at other time");
            }
        }
    }

    private int getFromUserChooseStore() {
        boolean flagIsValidChoose;
        int idStoreChoose;
        int i=1;
        do {
            System.out.println("Please select an id store from the stores below: ");

            for (Store st : engine.getAllStores()) {
                System.out.println();
                showStoreBasicDetails(st,i);
                i++;
            }

            idStoreChoose = getChooseFromUser();

            try {
                engine.CheckIfIsValidStoreId(idStoreChoose);
                flagIsValidChoose = true;

            } catch (InvalidIdStoreChooseException e) {
                flagIsValidChoose = false;
                System.out.println("the id store" + e.getInvalidId() + " is not correct, please try again ");
            }
        }
        while (!flagIsValidChoose);

        return (idStoreChoose);
    }

    private void showStoreBasicDetails(Store st, int i) {
        System.out.println("###STORE NUMBER "+i+"###");
        System.out.println("    ID:" + st.getId());
        System.out.println("    Name:" + st.getName());
        System.out.println("    PPK: " + st.getDeliveryPPK());
    }

    private int getChooseFromUser() {
        Scanner scan = new Scanner(System.in);
        String choose = scan.nextLine();
        int chooseNum=0;//miss mining
        boolean flagIsValidNumber;

        do{
            try
            {
                chooseNum=Integer.parseInt(choose);
                flagIsValidNumber = true;
            }
            catch (NumberFormatException e)
            {
                flagIsValidNumber=false;
                System.out.println("Please give an integer");
                choose = scan.nextLine();
            }

        }while (!flagIsValidNumber);

        return (chooseNum);
    }

    private Date getValidDateFromCostumer() {
        Scanner scanner = new Scanner(System.in);
        Date date2 = null;
        boolean flagIsValidDate;

        do {
            System.out.println("Enter the Date ,in format: dd/MM-HH:mm");
            String date = scanner.next();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM-HH:mm");
            dateFormat.setLenient(false);

            try {
                //Parsing the String
                date2 = dateFormat.parse(date);
                flagIsValidDate=true;
            } catch (ParseException e) {
                System.out.println("the date is not in the flowing format:  dd/MM-HH:mm \n please try again");
                flagIsValidDate = false;
            }
        }
        while (!flagIsValidDate);
        return (date2);


    }

    private Location getValidCostumerLocation() {

        boolean flagIsValidCostumerLocation;
        Location costumerLocation;
        do {
            System.out.println("Please insert your X location ");
            int x = getValidOption(Location.maxBorder);
            System.out.println("Please insert your y location ");
            int y = getValidOption(Location.maxBorder);
            costumerLocation = new Location(new Point(x, y));

            flagIsValidCostumerLocation = engine.checkIfThisLocationInUsedOfStore(costumerLocation);
            if(!flagIsValidCostumerLocation) {
                System.out.println("This location is already used by another store, you cant order if you are located in same location as " +
                        "store, please try again");
            }
        }
        while (!flagIsValidCostumerLocation);

        return (costumerLocation);
    }

    private void getFromUserItemsAndAmountAndUpdateTheOrder(int choosedStore){
        int choosedItem = 0;//no mining of this initialize
        boolean flagIsQ=false;

        engine.updateAllStoreItemsForSaleInCurrentStoreOrder(engine.getAllStoresMap().get(choosedStore));
        do {
            showAllItemsInAllStores();
            try
            {
                choosedItem = getFromUserValidItemId();
            }
            catch (QPressedException e)
            {
                System.out.println("bottom q is pushed, Preparing the order details");
                flagIsQ=true;
            }

            if (!flagIsQ) {

                String choosedAmountOfItem = getFromUserValidChooseAmount(choosedItem);
                try {
                    engine.addItemToCurrentOrder(choosedItem, choosedAmountOfItem);

                }
                catch (NegativeAmountOfItemInException ex) {
                    System.out.println("ERROR: Unknown error happend, probably tried to add an amount that made the total amount negative" +
                            " amount tried to add " + ex.getAmountTriedToAdd() + " amount in the order " + ex.getCurrentAmount());
                }
            }
        }
        while (!flagIsQ);
    }

    private void showAllItemsInAllStores() {
        int i=1;

        for (StoreItem stItem : engine.getAllStoreItemsWithPriceForSpecificStore())
        {
            System.out.println("##Item number: "+i+"##");
            showItemBasicData(stItem.getItem(), "", '1');
            System.out.println("    " + "4." +
                    (stItem.getPrice() == 0 ? "This store doesnt sell this item" : ("Price: " + stItem.getPrice())));
            System.out.println();
            i++;
        }
    }

    private int getFromUserValidItemId()throws QPressedException
    {
        //boolean flagIsQ;
        boolean flagIsValidItemId=false;
        int choosedItemNumber = 0;

        do {
            System.out.println("Please enter Item Id you would like to order");
            Scanner scan = new Scanner(System.in);
            String stringChoose = scan.nextLine();
            if (stringChoose.toLowerCase().equals("q"))
            {
                //flagIsQ = true;
                //return (0);
                throw(new QPressedException());
            }

            else {
                //flagIsQ = false;
                try {
                    choosedItemNumber = Integer.parseInt(stringChoose);

                    flagIsValidItemId = engine.checkIfThisValidItemId(choosedItemNumber);

                    if (flagIsValidItemId)
                    {
                        flagIsValidItemId = engine.checkIfItemPriceIsNotZero(choosedItemNumber);
                        if(!flagIsValidItemId)
                        {
                            System.out.println("This item is not sell in this store");
                        }
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID Please try again ");
                }
            }//else
        }while (!flagIsValidItemId);

        return (choosedItemNumber);
    }

    private String getFromUserValidChooseAmount(int itemId)
    {

        boolean flagIsValidItemAmount;
        String stringChooseAmount;

        do {
            System.out.println("Please enter amount of Item you would like to order");
            Scanner scan = new Scanner(System.in);
            stringChooseAmount = scan.nextLine();
            flagIsValidItemAmount = checkIfIsValidAmountOfthisItem(stringChooseAmount, itemId);
        } while (!flagIsValidItemAmount);

        return (stringChooseAmount);
    }

    private boolean checkIfIsValidAmountOfthisItem(String choosedItemAmount, int itemId)
    {
        boolean flagIsValidAmount = true;
        Item.ItemType itemType = engine.getItemTypeByID(itemId);

        switch (itemType) {
            case QUANTITY: {
                try {
                    int quantityOfItem = Integer.parseInt(choosedItemAmount);
                    if(quantityOfItem <= 0) {
                        flagIsValidAmount = false;
                        System.out.println("You gave an negative amount or 0, please give an positive number.");
                    }
                    /////flagIsValidAmount = true;
                } catch (NumberFormatException e1) {
                    flagIsValidAmount = false;
                    System.out.println("You must give an positive integer number, please try again");
                }
                break;
            }
            case WEIGHT: {
                try {
                    double weightOfItem = Double.parseDouble(choosedItemAmount);
                    if(weightOfItem <= 0) {
                        flagIsValidAmount = false;
                        System.out.println("You gave an negative amount or 0, please give an positive number.");
                    }
                    /////flagIsValidAmount = true;
                } catch (NumberFormatException e2) {
                    flagIsValidAmount = false;
                    System.out.println("You must give an positive decimal number, please try again");
                }
                break;
            }
        }
        return (flagIsValidAmount);
    }

    private void showOrderSummary(Order order) {
        int i = 1;

        System.out.println("Summary of order:");
        System.out.println("Order item:");
        for(OrderItem orderItem : order.getOrderItemCart().values()) {
            System.out.println("\t###ITEM " + i++ + "###");
            showOrderItem(orderItem);
        }
        System.out.println("Delivery Information:");
        System.out.println(String.format("\tDistance of costumer from store: %.2f", order.distanceBetweenCostumerAndStore()));
        System.out.println("\tStore Delivery PPK: " + order.getStoreOrderMadeFrom().getDeliveryPPK());
        System.out.println(String.format("\tDelivery price: %.2f",order.getDeliveryPrice()));
    }

    private void showOrderItem(OrderItem orderItem) {
        System.out.println("\t\t1.ID: " + orderItem.getItemInOrder().getItem().getId());
        System.out.println("\t\t2.Name: " + orderItem.getItemInOrder().getItem().getName());
        System.out.println("\t\t3.Type: " + orderItem.getItemInOrder().getItem().getType());
        System.out.println("\t\t4.Price: " + orderItem.getItemInOrder().getPrice());
        System.out.println(String.format("\t\t5.Amount in order: %.2f", Double.parseDouble(orderItem.getAmount())));
        System.out.println(String.format("\t\t6.Total price in order: %.2f", orderItem.getTotalPrice()));
    }

    private String getValidOrderConfirmation() {
        Scanner inputScanner = new Scanner(System.in);
        String input;
        boolean inputValid = false;
        do {

            input = inputScanner.nextLine().toLowerCase();

            if(input.equals("y") || input.equals("n")) {
                inputValid = true;
            }
            else {
                System.out.println("You must enter an Y or N to confirm or cancel the order");
            }
        } while(!inputValid);

        return input;
    }
    //endregion

    void showItemBasicData(Item itemToShow, String linePrefix, char countingPrefix) {
        System.out.println("    " + linePrefix + countingPrefix + ".ID: " + itemToShow.getId());
        System.out.println("    " + linePrefix + (++countingPrefix) + ".Name: " + itemToShow.getName());
        System.out.println("    " + linePrefix + (++countingPrefix) + ".Purchase type: " + itemToShow.getType());

    }

    //region show all orders
    private void showAllOrders() {
        int i = 1;
        if(engine.getAllOrders().size() == 0) {
            System.out.println("No orders have been made yet, go and make the first one!");
        }
        else {
            System.out.println("All orders made in the system:");
            for (Order order : engine.getAllOrders()) {
                System.out.println("###ORDER NUMBER " + i++ + "###");
                showOrder(order);
                System.out.println();
            }
        }
    }

    private void showOrder(Order order) {
        System.out.println("1.ID: " + order.getId());
        System.out.println("2.Date: " + (new SimpleDateFormat("dd/MM-HH:mm")).format(order.getDate()));
        System.out.println("3.Store order made from - ID: " + order.getStoreOrderMadeFrom().getId() + " name: " + order.getStoreOrderMadeFrom().getName());
        System.out.println("4.Number of types of items in the order: " + order.getOrderItemCart().size() + ". AND number items in order: " + order.getTotalItemsInOrder());
        System.out.println(String.format("5.Total price of items in order: %.2f", order.getPriceOfAllItems()));
        System.out.println(String.format("6.Price of delivery: %.2f", order.getDeliveryPrice()));
        System.out.println(String.format("7.Total price of order: %.2f", order.getTotalPrice()));
    }
    //endregion
}