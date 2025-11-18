package com.example.mobilekidsapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class AlphabetViewerFragment extends Fragment {
    private TracingView tracingView;
    private int currentLetterIndex = 0;
    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private Map<Character, Bitmap> letterBitmaps = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.counting_fragment, container, false);

        tracingView = view.findViewById(R.id.tracingView);

        /*This segment of code loads the first bitmap after the TracingView is completely laid out.
        post() makes sure that the view has a valid width and height before drawing or loading the
        bitmap, preventing a crash.*/
        tracingView.post(() -> {
            loadBitmap(alphabet[currentLetterIndex]);
            tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
        });

        return view;
    }

    /*This method returns the current letter index in this case we used it to keep track of the phrases used for each letter.*/
    public int getCurrentLetterIndex(){
        return currentLetterIndex;
    }

    public void clearCanvas() {
        if (tracingView != null){
            tracingView.clearCanvasBitmap();
        }
    }
    public boolean nextLetterCanvas() {
        if (tracingView == null) {
            return false;
        }

        boolean didWrite = tracingView.hasWriting();

        saveCurrentCanvas();
        currentLetterIndex++;
        if (currentLetterIndex >= alphabet.length){
            currentLetterIndex = 0;
        }

        loadBitmap(alphabet[currentLetterIndex]);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
        tracingView.resetWritingFlag();

        return didWrite;
    }
    public void previousLetterCanvas() {
        if (tracingView == null){
            return;
        }

        saveCurrentCanvas();
        currentLetterIndex--;
        if (currentLetterIndex < 0){
            currentLetterIndex = alphabet.length - 1;
        }

        loadBitmap(alphabet[currentLetterIndex]);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
    }

    public void saveCurrentCanvas(){
        if (tracingView == null || tracingView.getBitmap() == null){
            return;
        }

        char letter = alphabet[currentLetterIndex];

        Bitmap bitmapCopy = tracingView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        letterBitmaps.put(letter, bitmapCopy);

        //Saving the letter drawing into the database
        if(getActivity() instanceof AlphabetActivity){
            AlphabetActivity activity = (AlphabetActivity) getActivity();

            activity.dbHelper.saveBitmap(activity.aaProfileColor, activity.aaProfileShape, "alphabet", String.valueOf(letter), bitmapCopy);
        }
    }

    private void loadBitmap(char letter){
        Bitmap bitmap = letterBitmaps.get(letter);

        if(bitmap == null && getActivity() instanceof AlphabetActivity){
            AlphabetActivity activity = (AlphabetActivity) getActivity();

            bitmap = activity.dbHelper.loadBitmap(activity.aaProfileColor, activity.aaProfileShape, "alphabet", String.valueOf(letter));

            if(bitmap != null){
                letterBitmaps.put(letter, bitmap);
            }
        }
        if(bitmap == null){
            bitmap = Bitmap.createBitmap(tracingView.getWidth(), tracingView.getHeight(), Bitmap.Config.ARGB_8888);
        }

        tracingView.setBitmap(bitmap);
    }

    /*This method sets the correct letter to the student's saved progress. Using post()
    delays the update until after the TracingView has been fully measured and laid out,
    making sure that the bitmap loads with the correct width and height.*/
    public void setCurrentLetterIndex(int index){
        if(index < 0 || index >= alphabet.length) return;

        currentLetterIndex = index;

        if (tracingView != null) {
            tracingView.post(() -> {
                loadBitmap(alphabet[currentLetterIndex]);
                tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
            });
        }
    }

}