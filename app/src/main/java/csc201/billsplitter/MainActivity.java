package csc201.billsplitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private TextView names;
    private EditText email;
    private static ArrayList<String> people;

    private final static String TAG = "BillSplitter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.names);
        names = (TextView)findViewById(R.id.people);
        email = (EditText)findViewById(R.id.emails);

        people = new ArrayList<>(0);
    }

    /**
     * Takes the user input (a person's name and email) and creates a new Person object
     * then stores the new Person in an array list for use in calculations later.
     * @param v the current view
     */
    public void addPerson(View v) {
        String newName = name.getText().toString().trim();
        String newEmail = email.getText().toString().trim();

        // If no name, display error message
        if(newName.equals("")) {
            Toast.makeText(this, "You must enter a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If trying to add too many people, display error message
        if(people.size() >= 20) {
            Toast.makeText(this, "You can only add up to 20 people.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If no name, display error message
        if(newEmail.equals("")) {
            Toast.makeText(this, "You must enter an email.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If names matches, display error message
        if(people.contains(newName + "/" + newEmail)) {
            Toast.makeText(this, "Name and email combination is already in the list.", Toast.LENGTH_SHORT).show();
            return;
        }

        people.add(newName + "/" + newEmail);

        // Replace initial text with name of people
        if(people.size() == 1) {
            names.setText("");
        }

        names.setText(names.getText() + newName + " - " + newEmail + "\n");

        // Reset input field for next name
        name.setText("");
        email.setText("");
        name.requestFocus();
    }

    public void next(View v) {
        if(people.size() < 2) {
            Toast.makeText(this, "You need at least 2 people to split a bill.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, AddItemsActivity.class);
        intent.putExtra("PEOPLE", people);
        startActivity(intent);
    }

}