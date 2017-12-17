package csc201.billsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;


public class AddItemsActivity extends AppCompatActivity {

    private EditText name;
    private EditText price;
    private TextView itemList;

    private ArrayList<Person> people;
    private ArrayList<Item> items;
    private ArrayList<RadioButton> radioButtons;

    private final static String TAG = "BillSplitter";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        name = (EditText) findViewById(R.id.itemName);
        price = (EditText) findViewById(R.id.itemPrice);
        itemList = (TextView) findViewById(R.id.itemList);

        people = new ArrayList<>(0);
        items = new ArrayList<>(0);

        // get names from MainActivity and put into ArrayList of people
        Intent intent = getIntent();
        ArrayList<String> names = intent.getExtras().getStringArrayList("PEOPLE");

        for (String n : names) {
            String name = n.split("/")[0];
            String email = n.split("/")[1];
            Person p = new Person(name, email);
            people.add(p);
        }

        // make array of radio buttons, one for each person
        radioButtons = new ArrayList<>(people.size());
        for (int i = 0; i < people.size(); i++) {
            addRadioButton(i);
        }
    }

    public void addItem(View v) {

        /*
         * Display error messages if inputs are invalid
         */
        String newItemName = name.getText().toString().trim();
        if (newItemName.equals("")) {
            Toast.makeText(this, "You must provide an item name.", Toast.LENGTH_SHORT).show();
            return;
        }
        String newPrice = price.getText().toString();
        if (newPrice.equals("")) {
            Toast.makeText(this, "You must provide a price.", Toast.LENGTH_SHORT).show();
            return;
        }
        float newItemPrice = Float.valueOf(newPrice);
        if (newItemPrice <= 0) {
            Toast.makeText(this, "Price cannot be negative!", Toast.LENGTH_SHORT).show();
            return;
        }


        int buttonCount = 0;
        Person personWhoPaid = null; // initialize person

        // Check to see if a person is selected for given item
        for (int j = 0; j < radioButtons.size(); j++) {
            if (radioButtons.get(j).isChecked()) {
                personWhoPaid = people.get(j);
                buttonCount++;
            }
        }

        // If no person selected, display error message
        if (buttonCount < 1) {
            Toast.makeText(this, "You must select who paid for the item.", Toast.LENGTH_SHORT).show();
            return;
        }

        // create new item
        Item i = new Item(newItemName, newItemPrice, personWhoPaid);
        personWhoPaid.addItem(i);


        // add item to items list
        items.add(i);

        // remove empty list text and add name to list
        if (items.size() == 1) {
            itemList.setText("");
        }
        itemList.setText(itemList.getText() + newItemName + "\n");

        // reset text fields and radio buttons
        name.setText("");
        price.setText("");
        name.requestFocus();
        for (int j = 0; j < people.size(); j++) {
            radioButtons.get(j).setChecked(false);
        }
    }

    /*
     * Create radio button for each person listed and add to radio group
     */
    private void addRadioButton(int number) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        RadioButton radioBtn = new RadioButton(this);
        radioBtn.setId(number);
        radioBtn.setText(people.get(number).getName());
        radioButtons.add(radioBtn);
        radioGroup.addView(radioBtn);
    }

    /*
     * Change to activity screen, passing the names and amounts paid for items in the bill
     * If no items added, display error message
     */
    public void calculate(View v) {
        if (items.size() < 1) {
            Toast.makeText(this, "You need at least 1 item to split a bill.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(AddItemsActivity.this, ReceiptActivity.class);

        ArrayList<String> names = new ArrayList<>(people.size());
        ArrayList<Double> paid = new ArrayList<>(people.size());
        ArrayList<String> emails = new ArrayList<>(people.size());

        for (Person p : people) {
            names.add(p.getName());
            paid.add(p.moneyPaid());
            emails.add(p.getEmail());
        }
        intent.putExtra("NAMES", names);
        intent.putExtra("PAID", paid);
        intent.putExtra("EMAILS", emails);
        startActivity(intent);
    }
}
