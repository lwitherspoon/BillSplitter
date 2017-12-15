package csc201.billsplitter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class Receipt extends AppCompatActivity {

    private TextView names;
    private TextView owed;
    private TextView instructions;
    private Button startOverBtn;
    private Button sendEmailBtn;

    private ArrayList<String> people;
    private ArrayList<Double> amountPaid;
    private ArrayList<String> emails;
    private double billTotal;
    private double amountOwedPerPerson;

    private final static String TAG = "BillSplitter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        startOverBtn = (Button) findViewById(R.id.startOver);
        sendEmailBtn = (Button) findViewById(R.id.sendEmail);
        names = (TextView) findViewById(R.id.names);
        owed = (TextView) findViewById(R.id.owed);
        instructions = (TextView) findViewById(R.id.instructions);

        Intent intent = getIntent();
        people = intent.getExtras().getStringArrayList("NAMES");
        emails = intent.getExtras().getStringArrayList("EMAILS");
        amountPaid = (ArrayList<Double>) intent.getExtras().getSerializable("PAID");

        ArrayList<String> oweNames = new ArrayList<String>();
        ArrayList<Double> oweAmounts = new ArrayList<Double>();
        ArrayList<String> dueNames = new ArrayList<String>();
        ArrayList<Double> dueAmounts = new ArrayList<Double>();

        // Get total bill sum
        for (double amount : amountPaid) {
            billTotal += amount;
        }

        // Calculate $ amount owed per person
        amountOwedPerPerson = billTotal / people.size();

        // Build the string of emails to send to
        final StringBuilder emailList = new StringBuilder();

        for (String i : emails) {
            emailList.append(i + ",");
        }


        // Check if each person owes or is due money and add to corresponding arraylists
        for (int j = 0; j < people.size(); j++) {
            if (amountPaid.get(j) < amountOwedPerPerson) {
                oweNames.add(people.get(j));
                oweAmounts.add(amountOwedPerPerson - amountPaid.get(j));
            } else {
                dueNames.add(people.get(j));
                dueAmounts.add(amountPaid.get(j) - amountOwedPerPerson);
            }
        }

        // For each person who owes money, add name and $ owed to string for display
        for(int i = 0; i < oweNames.size(); i++){
            String namesOweText = String.format("%s%s\n", names.getText(), oweNames.get(i));
            String owedText = String.format("%s$%.2f\n", owed.getText(), oweAmounts.get(i));
            names.setText(namesOweText);
            owed.setText(owedText);
        }

        // For each person who is due money, add name and $ due to string for display
        for(int i = 0; i < dueNames.size(); i++){
            String namesDueText = String.format("%s%s\n", names.getText(), dueNames.get(i));
            String dueText = String.format("%s-$%.2f\n", owed.getText(), dueAmounts.get(i));
            names.setText(namesDueText);
            owed.setText(dueText);
        }

        /*
         * Loop through amounts owed and identify who should pay whom
         * For each person who owes money, loop through the people who are due money
         * and assign the "due amount" by checking whose $ value is greater
         *
         * For ex: if Bob owes $5, Jill is due $2.50, and Jack is due $2.50,
         * then on the first iteration, program will store that Bob owes Jill $2.50
         * On the second iteration, program will store that Bob owes Jack $2.50
         * Both "due amounts" will be stored and passed to receipt activity.
         */
        for (int k = 0; k < oweAmounts.size(); k++) {
            String name = oweNames.get(k);
            double payAmount = 0.0;

            // Loop through people due
            for (int l = 0; l < dueAmounts.size(); l++) {

                if (dueAmounts.get(l) != 0.0) {
                    // It the amount owed is greater than the amount due
                    if (oweAmounts.get(k) >= Math.abs(dueAmounts.get(l))) {
                        payAmount = Math.abs(dueAmounts.get(l));
                        oweAmounts.set(k, oweAmounts.get(k) - Math.abs(dueAmounts.get(l)));
                        dueAmounts.set(l, 0.0);
                    }

                    // If the amount due and owed are the equal
                    else if (oweAmounts.get(k).compareTo(dueAmounts.get(l)) == 0) {
                        payAmount = oweAmounts.get(k);
                        oweAmounts.set(k, 0.0);
                        dueAmounts.set(l, 0.0);
                    }

                    // If the amount due is greater than the amount owed
                    else {
                        payAmount = oweAmounts.get(k);
                        dueAmounts.set(l, Math.abs(dueAmounts.get(l) - oweAmounts.get(k)));
                        oweAmounts.set(k, 0.0);
                    }

                    String display = String.format("%s%s owes $%.2f to %s\n", instructions.getText(), name, payAmount, dueNames.get(l));
                    instructions.setText(display);
                }
            }
        }

        /*
         * Returns user to initial screen
         */
        startOverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Receipt.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*
         * Launch user's default email client with a new email message with the following:
         *  - The To: field pre-populated with the emails listed in Step 1,
         *  - The Subject: field pre-populated with "Bill Splitter: Receipt"
         *  - The Body: field pre-populated with the data displayed in Step 3
         */
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                StringBuilder emailMessage = new StringBuilder("To split the bill evenly:\n\n");
                emailMessage.append(instructions.getText());
                emailMessage.append("\nThanks!");

                emailIntent.setType("message/rfc822");
                emailIntent.setData(Uri.parse("mailto:" + emailList));

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Splitter: Receipt");
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage.toString());
                startActivity(emailIntent);

            }
        });


    }
}
