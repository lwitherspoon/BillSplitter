package csc201.billsplitter;

import java.util.ArrayList;

public class Person {
    private String name;
    private String email;
    private ArrayList<Item> itemsPaidFor;
    private double paid;
    private int numItems;

    Person(String name) {
        this.name = name;
        itemsPaidFor = new ArrayList<>(0);
    }

    Person(String name, String email) {
        this(name);
        this.email = email;
    }

    public void addItem(Item i) {
        itemsPaidFor.add(i);
        paid += i.getPrice();
        numItems++;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public ArrayList<Item> itemsBought(){
        return itemsPaidFor;
    }

    public double moneyPaid(){
        return paid;
    }

    public int numItems(){
        return numItems;
    }
}
