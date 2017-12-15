package csc201.billsplitter;

public class Item {
    private String name;
    private double price;
    private Person whoPaid;

    Item(String name, double price, Person whoPaid){
        this.name = name;
        this.price = price;
        this.whoPaid = whoPaid;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public Person getWhoPaid() { return whoPaid; }
}
