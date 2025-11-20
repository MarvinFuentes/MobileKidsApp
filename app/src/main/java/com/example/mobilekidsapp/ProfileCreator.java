package com.example.mobilekidsapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class ProfileCreator extends AppCompatActivity {

    /* Setting all of our variables for buttons, TextToSpeech, ImageButton, LinerLayout, ImageViews,
    and the ints are used for storing selections.*/
    Button pDoneBtn, pBackBtn;
    TextToSpeech pTTS;
    ImageButton pTxtToSpeechBtn;
    LinearLayout pShapesLayout, pColorsLayout;
    private ImageView pSelectedShapeView;
    private ImageView pSelectedColorView;
    private int pSelectedShape;
    private int pSelectedColor;
    StudentDd pDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_creator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // We locate all of our Buttons by using findViewById().
        pDoneBtn = (Button) findViewById(R.id.pDoneBtn);
        pBackBtn = (Button) findViewById(R.id.pBackBtn);

        // We locate our Text to speech image button by using findViewById().
        pTxtToSpeechBtn = findViewById(R.id.pSpeechBtn);

        // We locate both of our LinearLayouts where the shapes and colors are being displayed
        // by using findViewById().
        pShapesLayout = findViewById(R.id.shapesLinearLayout);
        pColorsLayout = findViewById(R.id.colorsLinearLayout);

        // These keep track of which ImageView is being selected.
        pSelectedShapeView = null;
        pSelectedColorView = null;

        // These hold the selected drawable resource IDs.
        pSelectedShape = -1;
        pSelectedColor = -1;

        // Initialize the database helper so we can insert new profiles.
        pDbHelper = new StudentDd(this);

        // Array of drawable images for shapes so the students can select their favorite.
        int [] pShapeDrawables = {
                R.drawable.shape_circle,
                R.drawable.shape_pentagon,
                R.drawable.shape_square,
                R.drawable.shape_trapezoid,
                R.drawable.shape_triangle,
                R.drawable.shape_oval,
                R.drawable.shape_rectangle,
                R.drawable.shape_hexagon,
                R.drawable.shape_octagon
        };

        // Array of drawable images for colors so the students can select their favorite.
        int [] pColorDrawables = {
                R.drawable.color_select_blue,
                R.drawable.color_select_green,
                R.drawable.color_select_purple,
                R.drawable.color_select_red,
                R.drawable.color_select_yellow,
                R.drawable.color_select_pink,
                R.drawable.color_select_black,
                R.drawable.color_select_white,
                R.drawable.color_select_orange
        };

        /* We used a for loop to dynamically create a Imageview for all the shape
        options available. It places each Image view in the linear layout. Also perfect
        for expanding or modifying without redundant code. */
        for(int drawable : pShapeDrawables){

            // We create a new image view to represnt one shape option.
            ImageView imageView = new ImageView(this);

            // We set the size and padding for each shape icon.
            imageView.setImageResource(drawable);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(325, 325));
            imageView.setPadding(10, 10,10,10);

            // We set an setOnClickListener so when the student knows what shape they have selected.
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We clear the previous highlight if another shape was selected earlier.
                    if(pSelectedShapeView != null){
                        pSelectedShapeView.setBackground(null);
                    }

                    // Set this newly selected shape to the currently selected one.
                    pSelectedShapeView = (ImageView) v;
                    pSelectedShape = drawable;

                    // Create a highlight around the image view that is selected.
                    v.setBackgroundResource(R.drawable.selection_border);
                }
            });
            // We add the shape to the shape layout.
            pShapesLayout.addView(imageView);
        }

        /* Just like before we use a  for loop to dynamically create a Imageview for all the
        color options available. It places each Image view in the linear layout. Also perfect
        for expanding or modifying without redundant code. */
        for(int drawable : pColorDrawables){

            // We create a new image view to represent one color option.
            ImageView imageView = new ImageView(this);

            // We set the size and padding for each shape icon.
            imageView.setImageResource(drawable);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(325, 325));
            imageView.setPadding(10, 10,10,10);

            // We set an setOnClickListener so when the student knows what color they have selected.
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We clear the previous highlight if another shape was selected earlier.
                    if(pSelectedColorView != null){
                        pSelectedColorView.setBackground(null);
                    }

                    // Set this newly selected shape to the currently selected one.
                    pSelectedColorView = (ImageView) v;
                    pSelectedColor = drawable;

                    // Create a highlight around the image view that is selected.
                    v.setBackgroundResource(R.drawable.selection_border);
                }
            });
            // We add the color to the color layout.
            pColorsLayout.addView(imageView);
        }

        // We set an setOnClickListener for the done button.
        pDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We check to see if the selected color and shape are not null.
                if(pSelectedColorView != null && pSelectedShapeView != null){

                    // We convert the selected drawable IDs into readable names.
                    String colorName = getResources().getResourceEntryName(pSelectedColor);
                    String shapeName = getResources().getResourceEntryName(pSelectedShape);

                    // Now we insert the new profile into the database.
                    pDbHelper.insertData(colorName, shapeName);

                    // Now we create new intent and pass on the profile that was just created.
                    // This ensures that we move on forward to SubjectSelection using the same profile.
                    // All data will be recorded for whenever they decide to log back in.
                    Intent intent = new Intent(ProfileCreator.this, SubjectSelection.class);
                    intent.putExtra("colorName", colorName);
                    intent.putExtra("shapeName", shapeName);
                    startActivity(intent);
                }
            }
        });

        // We set an setOnClickListener for the back button.
        pBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the back button is clicked we create a new intent.
                // We use startActivity() to send the user back to MainActivity.
                Intent intent = new Intent(ProfileCreator.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /* We now initialize the TextToSpeech and if the setup is successful, then we set the
        language to English.*/
        pTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                pTTS.setLanguage(Locale.ENGLISH);
            }
        });

        // We set an onClickListen for the text to speech image button.
        pTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we load a string that explains the Profile Creator screen.
                // speak() reads it aloud, and QUEUE_FLUSH clears anything that was playing before.
                String txtToSpeech = getString(R.string.profile_creation);
                pTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    // onDestroy() is used to make sure Text-to-Speech does not interfere with other activities
    // and prevents memory leaks.
    @Override
    protected void onDestroy() {
        // We check if TextToSpeech is active. If so, we stop and shut it down properly.
        if(pTTS != null){
            pTTS.stop();
            pTTS.shutdown();
        }
        super.onDestroy();
    }
}
