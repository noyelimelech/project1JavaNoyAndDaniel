package SDM;

import SDM.Exception.NegativeAmountOfItemInException;
import SDM.Item;
import SDM.Store;
import com.sun.xml.internal.bind.v2.TODO;

public class StoreItem
{
    private Item item;
    private int price;
    private Store store;
    private double totalAmountSoldInThisStore = 0;

    //constracture that call item constracture;?

    public double getTotalAmountSoldInThisStore() {
        return totalAmountSoldInThisStore;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public Store getStore()
    {
        return store;
    }

    public void setStore(Store store)
    {
        this.store = store;
    }

    public void addAmountThatSold(int amountToAdd) throws NegativeAmountOfItemInException {
        addAmountThatSold((double)amountToAdd);
    }

    public void addAmountThatSold(double amountToAdd) throws NegativeAmountOfItemInException {
        if(amountToAdd + totalAmountSoldInThisStore < 0) {
            throw new NegativeAmountOfItemInException(String.valueOf(totalAmountSoldInThisStore), String.valueOf(amountToAdd));
        }
        else {
            totalAmountSoldInThisStore += amountToAdd;
        }
    }
}
