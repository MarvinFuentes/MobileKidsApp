package com.example.mobilekidsapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class ExistingProfile extends AppCompatActivity {
    /* Setting all of our variables for buttons, TextToSpeech, ImageButton, LinerLayout, Cursor,
    database helper, and ImageView that tracks which profile is selected.*/
    Button eDoneBtn, eBackBtn;
    TextToSpeech eTTS;
    ImageButton eTxtToSpeechBtn;
    LinearLayout eExistingProfileLayout;
    StudentDd eDbHelper;
    SQLiteDatabase eDb;
    Cursor eCursor;
    // These variables store the selected profiles's color and shape.
    private ImageView eSelectedProfile;
    private String eSelectedColorName;
    private String eSelectedShapeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.existing_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // We locate all of our Buttons by using findViewById().
        eDoneBtn = (Button) findViewById(R.id.eDoneBtn);
        eBackBtn = (Button) findViewById(R.id.eBackBtn);

        // We locate our Text to speech image button by using findViewById().
        eTxtToSpeechBtn = findViewById(R.id.eSpeechBtn);

        // We locate of our LinearLayout where all existing profiles will be displayed
        // by using findViewById().
        eExistingProfileLayout = findViewById(R.id.existingProfileLinearLayout);

        // Initialize the database helper and open the database.
        eDbHelper = new StudentDd(this);
        eDb = eDbHelper.getReadableDatabase();

        // Query used to get every saved profile by color and shape from the 'profiles' table.
        eCursor = eDb.rawQuery("SELECT color, shape FROM profiles", null);

        // We track which profile has been clicked.
        eSelectedProfile = null;
        eSelectedColorName = null;
        eSelectedShapeName = null;

        /* We loop though all of the student profiles. Each profile has a color and a shape
        saved in the database. Here we recreate the exact same icon by tinting the shape with
        the saved color. */
        while(eCursor.moveToNext()){

            // We retrieve the saved color and shape from the database.
            String colorName = eCursor.getString(0);
            String shapeName = eCursor.getString(1);

            // We convert the saved shape name into a drawable resource ID.
            int shapeResId = getResources().getIdentifier(shapeName, "drawable", getPackageName());

            // We skip profiles with missing drawables.
            if(shapeResId == 0) continue;

            // Get the base drawable and make it mutable so we can change the color.
            Drawable shapeDrawable = getResources().getDrawable(shapeResId, null).mutate();

            /* We change the color of the shape based off the color saved in the database. With multiple            if statements we check what color was picked during profile creation and then use setTint()
            to change the color of the shape by accessing the color.xml file where all the colors are saved .*/
            if(colorName.equals("color_select_black")) shapeDrawable.setTint(getColor(R.color.black));

            else if(colorName.equals("color_select_blue")) shapeDrawable.setTint(getColor(R.color.blue));

            else if(colorName.equals("color_select_green")) shapeDrawable.setTint(getColor(R.color.green));

            else if(colorName.equals("color_select_orange")) shapeDrawable.setTint(getColor(R.color.orange));

            else if(colorName.equals("color_select_pink")) shapeDrawable.setTint(getColor(R.color.pink));

            else if(colorName.equals("color_select_purple")) shapeDrawable.setTint(getColor(R.color.purple));

            else if(colorName.equals("color_select_red")) shapeDrawable.setTint(getColor(R.color.red));

            else if(colorName.equals("color_select_white")) shapeDrawable.setTint(getColor(R.color.white));

            else if(colorName.equals("color_select_yellow")) shapeDrawable.setTint(getColor(R.color.yellow));

            else continue;

            // We create a ImageView for the profile icon.
            ImageView shapeView = new ImageView(this);

            // We set the size and padding for each profile icon.
            shapeView.setImageDrawable(shapeDrawable);
            shapeView.setLayoutParams(new LinearLayout.LayoutParams(325, 325));
            shapeView.setPadding(10, 10, 10, 10);

            // We set an setOnClickListener so when the student knows what profile they have selected.
            shapeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We clear the previous highlight if another shape was selected earlier.
                    if (eSelectedProfile != null){
                        eSelectedProfile.setBackground(null);
                    }

                    // Set this newly selected shape to the currently selected one.
                    eSelectedProfile = (ImageView) v;

                    // Create a highlight around the image view that is selected
                    v.setBackgroundResource(R.drawable.selection_border);

                    // Store the selected profile's saved color and shape.
                    eSelectedColorName = colorName;
                    eSelectedShapeName = shapeName;
                }
            });
            // We add the profile to the profile layout.
            eExistingProfileLayout.addView(shapeView);
        }

        // We close the cursor and database after loading the available profiles.
        eCursor.close();
        eDb.close();

        // We set an setOnClickListener for the done button.
        eDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* We check with multiple conditions before we move on to creating a new intent and pass over the
                profile data. If for some reason the student does not select a profile then they will receive the
                a Toast message telling them to do so. */
                if(eSelectedProfile != null && eSelectedColorName != null && eSelectedShapeName != null){
                    Intent intent = new Intent(ExistingProfile.this, SubjectSelection.class);
                    intent.putExtra("colorName", eSelectedColorName);
                    intent.putExtra("shapeName", eSelectedShapeName);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ExistingProfile.this, "Please select a profile first!", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        // We set an setOnClickListener for the back button.
        eBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the back button is clicked we create a new intent.
                // We use startActivity() to send the user back to MainActivity.
                Intent intent = new Intent(ExistingProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /* We now initialize the TextToSpeech and if the setup is successful, then we set the
        language to English.*/
        eTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                eTTS.setLanguage(Locale.ENGLISH);
            }
        });

        // We set an onClickListen for the text to speech image button.
        eTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we load a string that explains the Existing Profile screen.
                // speak() reads it aloud, and QUEUE_FLUSH clears anything that was playing before.
                String txtToSpeech = getString(R.string.existing_profile);
                eTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    /* onDestroy() is used to make sure Text-to-Speech does not interfere with other activities
    and prevents memory leaks. */
    @Override
    protected void onDestroy() {
        // We check if TextToSpeech is active. If so, we stop and shut it down properly.
        if(eTTS != null){
            eTTS.stop();
            eTTS.shutdown();
        }
        super.onDestroy();
    }
}
