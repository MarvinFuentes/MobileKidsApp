package com.example.mobilekidsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MathActivity extends AppCompatActivity {
    Button maCheckBtn;
    TextView maNum1, maNum2, maOperation;
    ImageButton maForwardBtn, maBackBtn;
    EditText maAnswer;
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.math_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        maCheckBtn = (Button) findViewById(R.id.maCheckBtn);
        maForwardBtn = findViewById(R.id.maFowardBtn);
        maBackBtn = findViewById(R.id.maBackBtn);
        maAnswer = findViewById(R.id.maAnswer);

        maNum1 = (TextView) findViewById(R.id.maNum1);
        maNum2 = (TextView) findViewById(R.id.maNum2);
        maOperation = (TextView) findViewById(R.id.maOperation);

        random = new Random();
        generateRandEq();

        maForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRandEq();
                maAnswer.setText("");
            }
        });

        maBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        maCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerIn = maAnswer.getText().toString();

                int userAnswer = Integer.parseInt(answerIn);
                int num1 = Integer.parseInt(maNum1.getText().toString());
                int num2 = Integer.parseInt(maNum2.getText().toString());
                String op = maOperation.getText().toString();

                int correctAnswer = op.equals("+") ? num1 + num2 : num1 - num2;

                if (userAnswer == correctAnswer) {
                    Toast.makeText(MathActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MathActivity.this, "Incorrect: ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alphabet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.mmSaveBtn){
            Toast.makeText(this, "Save button pressed!", Toast.LENGTH_LONG).show();
        }
        else if(itemId == R.id.mmMenuBtn){
            Intent intent = new Intent(MathActivity.this, SubjectSelection.class);
            /*
            intent.putExtra("colorName", existingProfile.eSelectedColorName);
            intent.putExtra("shapeName", existingProfile.eSelectedShapeName);*/
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void generateRandEq(){
        int randomNum1 = random.nextInt(10) + 1;
        int randomNum2 = random.nextInt(10) + 1;
        String randOperation = random.nextBoolean() ? "+" : "−";

        if (randOperation.equals("−") && randomNum2 > randomNum1) {
            int temp = randomNum1;
            randomNum1 = randomNum2;
            randomNum2 = temp;
        }
        maNum1.setText(String.valueOf(randomNum1));
        maNum2.setText(String.valueOf(randomNum2));
        maOperation.setText(randOperation);
    }
}