package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thenotoriousrog.myapplication.R;

/**
 * Created by thenotoriousrog on 6/3/17.
 * This class handles the behavior for when a user selects ManualEntry of their music folder path
 */

public class ManualEntry extends Activity {

    final public int USER_INPUTED_PATH = 300; // result code so that we can get the path from the user.
    protected EditText enteredPath; // the textField where users can enter their path to their music folder.
    protected Button submit; // button for when a user submits their folder path.

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.thenotoriousrog.myapplication.R.layout.manual_entry); // display the window so a user can add the path to their music folder.

        enteredPath = (EditText) findViewById(com.example.thenotoriousrog.myapplication.R.id.enteredPath); // view for the EditText field.
        submit = (Button) findViewById(com.example.thenotoriousrog.myapplication.R.id.Submit); // view for the Submit button.
        submit.setOnClickListener(new View.OnClickListener() {

            // when clicked, pull the data from the user enteredPath TextField.
            @Override
            public void onClick(View v)
            {
                String userInput = enteredPath.getText().toString(); // get the text entered by the user.

                if(userInput.length() == 0 || userInput == null) // if user enters nothing, or if something fails, let the user know.
                {
                    Toast.makeText(ManualEntry.this, "You did not enter a path to your music folder :P", Toast.LENGTH_LONG).show();
                }
                else
                {
                    sendPath(userInput); // prepare to send the path back to the MainActivity.
                }
            }
        });

    }

    // takes in the path and prepares as well as sends the path back to the MainActivity.
    public void sendPath(String path)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("pathToFolder", path); // send the path back to MainActivity.

        setContentView(com.example.thenotoriousrog.myapplication.R.layout.activity_main);
        setResult(USER_INPUTED_PATH, resultIntent); // set the result from the user.

        finish(); // end the activity and close window.

        setContentView(R.layout.startup_layout);
    }

}
