package com.example.mobilekidsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class SubjectSelection extends AppCompatActivity {
    // Setting all of our variables for buttons, TextToSpeech, ImageButton, and TextViews.
    Button sBackBtn;
    TextToSpeech sTTS;
    ImageButton sTxtToSpeechBtn;
    TextView sAlphabetBtn, sCountingBtn, sMathBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.subject_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // We locate all of our TextsViews and Button by using findViewById().
        sBackBtn = (Button) findViewById(R.id.sBackBtn);
        sAlphabetBtn = (TextView) findViewById(R.id.sAlphabetBtn);
        sCountingBtn = (TextView) findViewById(R.id.sCountingBtn);
        sMathBtn = (TextView) findViewById(R.id.sMathBtn);

        // We locate our Text to speech image button by using findViewById().
        sTxtToSpeechBtn = findViewById(R.id.sSpeechBtn);

        // We set an setOnClickListener for the back button.
        sBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the back button is clicked we create a new intent.
                // We use startActivity() to send the user back to ExistingProfile activity.
                Intent intent = new Intent(SubjectSelection.this, ExistingProfile.class);
                startActivity(intent);
            }
        });

        // We set an setOnClickListener for the Alphabet activity button.
        sAlphabetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // When the alphabet activity button is click we use getIntent() and getStringExtra()
                // to locate the the exact profile that was selected from ExistingProfile.
                String colorName = getIntent().getStringExtra("colorName");
                String shapeName = getIntent().getStringExtra("shapeName");

                // Now we create new intent and pass on the profile that is selected over to the alphabetActivity.
                // This ensures that we move on forward using the same profile from ExistingProfile()
                // all data will be recorded for whenever they decide to log back in.
                Intent intent = new Intent(SubjectSelection.this, AlphabetActivity.class);
                intent.putExtra("colorName", colorName);
                intent.putExtra("shapeName", shapeName);
                startActivity(intent);
            }
        });

        // We set an setOnClickListener for the counting activity button.
        sCountingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the counting activity button is click we use getIntent() and getStringExtra()
                // to locate the the exact profile that was selected from ExistingProfile.
                String colorName = getIntent().getStringExtra("colorName");
                String shapeName = getIntent().getStringExtra("shapeName");

                // Now we create new intent and pass on the profile that is selected over to the countingActivity.
                // This ensures that we move on forward using the same profile from ExistingProfile()
                // all data will be recorded for whenever they decide to log back in.
                Intent intent = new Intent(SubjectSelection.this, CountingActivity.class);
                intent.putExtra("colorName", colorName);
                intent.putExtra("shapeName", shapeName);
                startActivity(intent);
            }
        });

        // We set an setOnClickListener for the math activity button.
        sMathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the math activity button is click we use getIntent() and getStringExtra()
                // to locate the the exact profile that was selected from ExistingProfile.
                String colorName = getIntent().getStringExtra("colorName");
                String shapeName = getIntent().getStringExtra("shapeName");

                // Now we create new intent and pass on the profile that is selected over to the mathActivity.
                // This ensures that we move on forward using the same profile from ExistingProfile()
                // all data will be recorded for whenever they decide to log back in.
                Intent intent = new Intent(SubjectSelection.this, MathActivity.class);
                intent.putExtra("colorName", colorName);
                intent.putExtra("shapeName", shapeName);
                startActivity(intent);
            }
        });

        /* We now initialize the TextToSpeech and if the setup is successful, then we set the
        language to English.*/
        sTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                sTTS.setLanguage(Locale.ENGLISH);
            }
        });

        // We set an onClickListen for the text to speech image button.
        sTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we load a string that explains the subject selection screen.
                // speak() reads it aloud, and QUEUE_FLUSH clears anything that was playing before.
                String txtToSpeech = getString(R.string.subject_selection);
                sTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    // onDestroy() is used to make sure Text-to-Speech does not interfere with other activities
    // and prevents memory leaks.
    @Override
    protected void onDestroy() {
        // We check if TextToSpeech is active. If so, we stop and shut it down properly.
        if(sTTS != null){
            sTTS.stop();
            sTTS.shutdown();
        }
        super.onDestroy();
    }
}
