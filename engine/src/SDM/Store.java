package SDM;

import SDM.Location;
import SDM.Order;

import java.util.*;

public class Store
{
    private int id;
    private String name;
    private int deliveryPPK;
    private Location location;
    private Map<Integer, StoreItem> itemsThatSellInThisStore=new HashMap<>();
    private List<Order> orders=new LinkedList<Order>();

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getDeliveryPPK()
    {
        return deliveryPPK;
    }

    public void setDeliveryPPK(int deliveryPPK)
    {
        this.deliveryPPK = deliveryPPK;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public Map<Integer, StoreItem> getItemsThatSellInThisStore()
    {
        return itemsThatSellInThisStore;
    }

    public void setItemsThatSellInThisStore(Map<Integer, StoreItem> itemsThatSellInThisStore)
    {
        this.itemsThatSellInThisStore = itemsThatSellInThisStore;
    }

    public List<Order> getOrders()
    {
        return orders;
    }

    public void setOrders(List<Order> orders)
    {
        this.orders = orders;
    }
}