package com.example.mobilekidsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button mNewStudentBtn, mExistingStudentBtn;
    TextToSpeech mTTS;
    ImageButton mTxtToSpeechBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mNewStudentBtn = (Button) findViewById(R.id.newStudentBtn);
        mExistingStudentBtn = (Button) findViewById(R.id.ExistingStudentBtn);
        mTxtToSpeechBtn = findViewById(R.id.speechBtn);

        //Button to move to ProfileCreator.class
        mNewStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileCreator.class);
                startActivity(intent);
            }
        });

        //Button to move to ExistingProfile.class
        mExistingStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExistingProfile.class);
                startActivity(intent);
            }
        });

        // We now initialize the TextToSpeech and if the setup is successful, then we set the
        // language to English.
        mTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                mTTS.setLanguage(Locale.ENGLISH);
            }
        });

        // We set an onClickListen for the text to speech image button.
        mTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we load a string resource that explains the subject selection screen.
                // speak() reads it aloud, and QUEUE_FLUSH clears anything that was playing before.
                String txtToSpeech = getString(R.string.main_page_speech);
                mTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}