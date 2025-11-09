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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class ExistingProfile extends AppCompatActivity {
    Button eDoneBtn, eBackBtn;
    TextToSpeech eTTS;
    ImageButton eTxtToSpeechBtn;
    LinearLayout eExistingProfileLayout;
    StudentDd eDbHelper;
    SQLiteDatabase eDb;
    Cursor eCursor;
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
        eDoneBtn = (Button) findViewById(R.id.eDoneBtn);
        eBackBtn = (Button) findViewById(R.id.eBackBtn);

        eTxtToSpeechBtn = findViewById(R.id.eSpeechBtn);
        eExistingProfileLayout = findViewById(R.id.existingProfileLinearLayout);

        eDbHelper = new StudentDd(this);
        eDb = eDbHelper.getReadableDatabase();

        eCursor = eDb.rawQuery("SELECT color, shape FROM profiles", null);

        eSelectedProfile = null;
        eSelectedColorName = null;
        eSelectedShapeName = null;

        while(eCursor.moveToNext()){
            String colorName = eCursor.getString(0);
            String shapeName = eCursor.getString(1);

            int shapeResId = getResources().getIdentifier(shapeName, "drawable", getPackageName());

            if(shapeResId == 0) continue;

            Drawable shapeDrawable = getResources().getDrawable(shapeResId, null).mutate();

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

            ImageView shapeView = new ImageView(this);
            shapeView.setImageDrawable(shapeDrawable);
            shapeView.setLayoutParams(new LinearLayout.LayoutParams(325, 325));
            shapeView.setPadding(10, 10, 10, 10);

            shapeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eSelectedProfile != null){
                        eSelectedProfile.setBackground(null);
                    }

                    // Set the newly selected ImageView
                    eSelectedProfile = (ImageView) v;

                    // Create a highlight around the image view that is selected
                    v.setBackgroundResource(R.drawable.selection_border);

                    eSelectedColorName = colorName;
                    eSelectedShapeName = shapeName;
                }
            });
            eExistingProfileLayout.addView(shapeView);
        }

        eCursor.close();
        eDb.close();

        eDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to subject selection page
                if(eSelectedProfile != null){
                    Intent intent = new Intent(ExistingProfile.this, SubjectSelection.class);
                    intent.putExtra("colorName", eSelectedColorName);
                    intent.putExtra("shapeName", eSelectedShapeName);
                    startActivity(intent);
                }
            }
        });

        eBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExistingProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        eTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                eTTS.setLanguage(Locale.ENGLISH);
            }
        });

        eTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToSpeech = getString(R.string.existing_profile);
                eTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(eTTS != null){
            eTTS.stop();
            eTTS.shutdown();
        }
        super.onDestroy();
    }
}
