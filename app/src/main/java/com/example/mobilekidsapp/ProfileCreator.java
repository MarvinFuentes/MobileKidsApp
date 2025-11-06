package com.example.mobilekidsapp;

import android.content.Intent;
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

public class ProfileCreator extends AppCompatActivity {

    Button pDoneBtn, pBackBtn;
    TextToSpeech ptts;
    ImageButton pTxtToSpeechBtn;
    LinearLayout pShapesLayout, pColorsLayout;
    private ImageView pSelectedShapeView = null;
    private ImageView pSelectedColorView = null;
    private int pSelectedShape = -1;
    private int pSelectedColor = -1;

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
        pDoneBtn = (Button) findViewById(R.id.pDoneBtn);
        pBackBtn = (Button) findViewById(R.id.pBackBtn);

        pTxtToSpeechBtn = findViewById(R.id.pSpeechBtn);

        pShapesLayout = findViewById(R.id.shapesLinearLayout);
        pColorsLayout = findViewById(R.id.colorsLinearLayout);

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

        for(int drawable : pShapeDrawables){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(drawable);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(325, 325));
            imageView.setPadding(10, 10,10,10);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pSelectedShapeView != null){
                        pSelectedShapeView.setBackground(null);
                    }

                    // Set the newly selected ImageView
                    pSelectedShapeView = (ImageView) v;
                    pSelectedShape = drawable;

                    // Create a highlight around the image view that is selected
                    v.setBackgroundResource(R.drawable.selection_border);
                }
            });


            pShapesLayout.addView(imageView);
        }

        for(int drawable : pColorDrawables){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(drawable);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(325, 325));
            imageView.setPadding(10, 10,10,10);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pSelectedColorView != null){
                        pSelectedColorView.setBackground(null);
                    }

                    // Set the newly selected ImageView
                    pSelectedColorView = (ImageView) v;
                    pSelectedColor = drawable;

                    // Create a highlight around the image view that is selected
                    v.setBackgroundResource(R.drawable.selection_border);
                }
            });
            pColorsLayout.addView(imageView);
        }


        pDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to subject selection page
            }
        });

        pBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileCreator.this, MainActivity.class);
                startActivity(intent);
            }
        });


        ptts = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                ptts.setLanguage(Locale.ENGLISH);
            }
        });

        pTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToSpeech = getString(R.string.profile_creation);
                ptts.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

    }
}
