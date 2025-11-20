package com.example.mobilekidsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;
import java.util.Random;

public class MathActivity extends AppCompatActivity {
    Button maCheckBtn;
    TextView maNum1, maNum2, maOperation;
    ImageButton maForwardBtn, maBackBtn, maSpeechBtn;
    TextToSpeech mTTS;
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
        maSpeechBtn = findViewById(R.id.maSpeechBtn);

        maProgressBar = (ProgressBar) findViewById(R.id.maProgressBar);

        maNum1 = (TextView) findViewById(R.id.maNum1);
        maNum2 = (TextView) findViewById(R.id.maNum2);
        maOperation = (TextView) findViewById(R.id.maOperation);

        studentColor = getIntent().getStringExtra("colorName");
        studentShape = getIntent().getStringExtra("shapeName");

        random = new Random();

        //gets math questions from the question list based on studentColor and StudentShape
        StudentDd db = new StudentDd(this);
        questionList = db.getMathQuestionsForStudent(studentColor, studentShape);

        //generates random equation if questionlist is empty
        if (questionList.isEmpty()) {
            generateRandEq();
        }

        displayQuestion(questionList.get(currentIndex));


        //gets progress bar progress from studentShape and studentColor
        //sets saved progress bar
        int progress = db.getProgress(studentColor, studentShape, "math");
        maProgressBar.setProgress(progress);

        //Button to advance to the next question
        maForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save users input for the current question
                MathQuestion q = questionList.get(currentIndex);
                q.userInput = maAnswer.getText().toString();
                saveQuestionToDB(q);

                //advances the progress bar if current answer is correct then saves it to the db
                if (currentAnswerCorrect) {
                    int newProgress = maProgressBar.getProgress() + 1;
                    maProgressBar.setProgress(maProgressBar.getProgress() + 1);

                    StudentDd db = new StudentDd(MathActivity.this);
                    db.updateProgress(studentColor, studentShape, "math", newProgress);
                    db.close();
                }
                currentAnswerCorrect = false;   // reset for next question

                //checks to see if max question amount has been reached, if it hasnt generate a new question
                if (currentIndex == questionList.size() - 1) {
                    if (questionList.size() < MAX_QUESTION_AMOUNT) {
                        generateRandEq();
                    } 
                } else {
                    currentIndex++;
                    displayQuestion(questionList.get(currentIndex));
                }
            }
        });

        //Button to go to the previous question
        maBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save users input for current question
                questionList.get(currentIndex).userInput = maAnswer.getText().toString();

                //checks to see if you are at the first question so it stops
                if (currentIndex > 0) {
                    currentIndex--;
                    displayQuestion(questionList.get(currentIndex));
                }
            }
        });

        //Button to check if user input is correct
        maCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerIn = maAnswer.getText().toString();

                //convert all attributes to integers
                int userAnswer = Integer.parseInt(answerIn);
                int num1 = Integer.parseInt(maNum1.getText().toString());
                int num2 = Integer.parseInt(maNum2.getText().toString());
                String op = maOperation.getText().toString();

                //calculate correct answer
                int correctAnswer = op.equals("+") ? num1 + num2 : num1 - num2;

                //displays toast message if answer is correct or incorrect
                if (userAnswer == correctAnswer) {
                    Toast.makeText(MathActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
                    currentAnswerCorrect = true;
                } else {
                    Toast.makeText(MathActivity.this, "Incorrect: ", Toast.LENGTH_SHORT).show();
                    currentAnswerCorrect = false;
                }
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
        maSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we load a string resource that explains the subject selection screen.
                // speak() reads it aloud, and QUEUE_FLUSH clears anything that was playing before.
                String txtToSpeech = getString(R.string.math_string);
                mTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    //Inflater for math menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.math_menu, menu);
        return true;
    }

    //setting menu buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        //save button only displays a toast message
        if(itemId == R.id.mmSaveBtn){
            Toast.makeText(this, "Save button pressed!", Toast.LENGTH_LONG).show();
            return true;
        }
        //menu button returns to SubjectSelection.class
        else if(itemId == R.id.mmMenuBtn){
            Intent intent = new Intent(MathActivity.this, SubjectSelection.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //method to generate random equation
    private void generateRandEq(){
        //creates 2 random integers between 1 and 10
        int randomNum1 = random.nextInt(10) + 1;
        int randomNum2 = random.nextInt(10) + 1;
        String randOperation = random.nextBoolean() ? "+" : "-";

        //ensures the answer cannot be negative by making sure num1 is larger than num2 when subtracting
        if (randOperation.equals("-") && randomNum2 > randomNum1) {
            int temp = randomNum1;
            randomNum1 = randomNum2;
            randomNum2 = temp;
        }
        //creates math question and adds it to the question list
        MathQuestion q = new MathQuestion(randomNum1, randomNum2, randOperation);
        questionList.add(q);

        currentIndex = questionList.size() - 1;
        displayQuestion(q);
    }

    //method to display question
    private void displayQuestion(MathQuestion q) {
        maNum1.setText(String.valueOf(q.num1));
        maNum2.setText(String.valueOf(q.num2));
        maOperation.setText(q.operation);
        maAnswer.setText(q.userInput);
    }

    //method to save question to db
    private void saveQuestionToDB(MathQuestion q) {
        if (q.userInput == null || q.userInput.isEmpty()) return; // skip empty input
        StudentDd db = new StudentDd(this);
        //insert question into db
        db.insertOrUpdateMathProgress(studentColor, studentShape, q.num1, q.operation, q.num2, q.userInput);
        db.close();
    }

    //class that holds the layout for the math questions
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
