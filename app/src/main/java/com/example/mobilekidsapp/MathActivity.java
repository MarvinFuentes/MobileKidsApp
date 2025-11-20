package com.example.mobilekidsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class MathActivity extends AppCompatActivity {
    Button maCheckBtn;
    TextView maNum1, maNum2, maOperation;
    ImageButton maForwardBtn, maBackBtn;
    EditText maAnswer;
    Random random;
    ProgressBar maProgressBar;
    ArrayList<MathQuestion> questionList = new ArrayList<>();
    int currentIndex = 0;
    private static final int MAX_QUESTION_AMOUNT = 20;
    boolean currentAnswerCorrect = false;
    String studentColor;
    String studentShape;


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

        maProgressBar = (ProgressBar) findViewById(R.id.maProgressBar);

        maNum1 = (TextView) findViewById(R.id.maNum1);
        maNum2 = (TextView) findViewById(R.id.maNum2);
        maOperation = (TextView) findViewById(R.id.maOperation);

        studentColor = getIntent().getStringExtra("colorName");
        studentShape = getIntent().getStringExtra("shapeName");

        random = new Random();

        StudentDd db = new StudentDd(this);
        questionList = db.getMathQuestionsForStudent(studentColor, studentShape);


        if (questionList.isEmpty()) {
            generateRandEq();
        }

        displayQuestion(questionList.get(currentIndex));

        int progress = db.getProgress(studentColor, studentShape, "math");
        maProgressBar.setProgress(progress);

        maForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MathQuestion q = questionList.get(currentIndex);
                q.userInput = maAnswer.getText().toString();
                saveQuestionToDB(q);

                if (currentAnswerCorrect) {
                    int newProgress = maProgressBar.getProgress() + 1;
                    maProgressBar.setProgress(maProgressBar.getProgress() + 1);

                    StudentDd db = new StudentDd(MathActivity.this);
                    db.updateProgress(studentColor, studentShape, "math", newProgress);
                    db.close();
                }
                currentAnswerCorrect = false;   // reset for next question


                if (currentIndex == questionList.size() - 1) {
                    if (questionList.size() < MAX_QUESTION_AMOUNT) {
                        generateRandEq();  // create new question
                    } 
                } else {
                    currentIndex++;
                    displayQuestion(questionList.get(currentIndex));
                }
            }
        });

        maBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionList.get(currentIndex).userInput = maAnswer.getText().toString();

                if (currentIndex > 0) {
                    currentIndex--;
                    displayQuestion(questionList.get(currentIndex));
                }
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
                    currentAnswerCorrect = true;
                } else {
                    Toast.makeText(MathActivity.this, "Incorrect: ", Toast.LENGTH_SHORT).show();
                    currentAnswerCorrect = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.math_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.mmSaveBtn){
            Toast.makeText(this, "Save button pressed!", Toast.LENGTH_LONG).show();
            return true;
        }
        else if(itemId == R.id.mmMenuBtn){
            Intent intent = new Intent(MathActivity.this, SubjectSelection.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void generateRandEq(){
        int randomNum1 = random.nextInt(10) + 1;
        int randomNum2 = random.nextInt(10) + 1;
        String randOperation = random.nextBoolean() ? "+" : "-";

        if (randOperation.equals("-") && randomNum2 > randomNum1) {
            int temp = randomNum1;
            randomNum1 = randomNum2;
            randomNum2 = temp;
        }
        MathQuestion q = new MathQuestion(randomNum1, randomNum2, randOperation);
        questionList.add(q);

        currentIndex = questionList.size() - 1;
        displayQuestion(q);
    }
    private void displayQuestion(MathQuestion q) {
        maNum1.setText(String.valueOf(q.num1));
        maNum2.setText(String.valueOf(q.num2));
        maOperation.setText(q.operation);
        maAnswer.setText(q.userInput);
    }

    private void saveQuestionToDB(MathQuestion q) {
        if (q.userInput == null || q.userInput.isEmpty()) return; // skip empty input
        StudentDd db = new StudentDd(this);
        db.insertOrUpdateMathProgress(studentColor, studentShape, q.num1, q.operation, q.num2, q.userInput);
        db.close();
    }
    public static class MathQuestion {
        int num1;
        int num2;
        String operation;
        String userInput = "";

        public MathQuestion(int num1, int num2, String operation) {
            this.num1 = num1;
            this.num2 = num2;
            this.operation = operation;
        }
    }
}
