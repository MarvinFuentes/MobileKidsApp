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

        // We inflate the the alphabet_fragment so we can switch between all of the other letters in
        // the alphabet array without needing multiple activities. This keeps the program easier to
        // manage and easier to grow if we ever need to add more features.
        View view = inflater.inflate(R.layout.alphabet_fragment, container, false);

        tracingView = view.findViewById(R.id.tracingView);

        /* This part loads the first bitmap only after the TracingView is fully laid out.
          post() makes sure that the view has a valid width and height before loading anything,
          which prevents crashes*/
        tracingView.post(() -> {
            loadBitmap(alphabet[currentLetterIndex]);
            tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
        });

        return view;
    }

    /*getCurrentLetterIndex() returns the current letter index. We mainly use this
      to know which phrase to say for text-to-speech.*/
    public int getCurrentLetterIndex(){
        return currentLetterIndex;
    }

    /* clearCanvas() is used in AlphabetActivity when the clear canvas button is clicked.
     It simply calls tracingView.clearCanvasBitmap().*/
    public void clearCanvas() {
        if (tracingView != null){
            tracingView.clearCanvasBitmap();
        }
    }

    /* nextLetterCanvas() handles switching to the next letter. It loads the next bitmap,
      updates the letter shown on screen, and saves the drawing.*/
    public boolean nextLetterCanvas() {

        // First we check if the tracingVew is null. If it is, we return.
        if (tracingView == null) {
            return false;
        }

        // didWrite checks to see if the student drew anything before moving on.
        boolean didWrite = tracingView.hasWriting();

        // We save the current drawing before switching to the next one. We increment the
        // index of the current letter.
        saveCurrentCanvas();
        currentLetterIndex++;

        // If we go past the alphabet array then we reset back to the letter A.
        if (currentLetterIndex >= alphabet.length){
            currentLetterIndex = 0;
        }

        // Finally, we load the next bitmap by calling the loadBitmap method pass on the current index of the
        // alphabet index and we update the letter on the screen. We also reset the TracingView by calling
        // resetWritingFlag() so it is ready for the next drawing.
        loadBitmap(alphabet[currentLetterIndex]);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
        tracingView.resetWritingFlag();

        return didWrite;
    }

    /* previousLetterCanvas() loads the previous bitmap and lets the student go back.*/
    public void previousLetterCanvas() {
        // First we check to see if the tracingView is null. If it is, we return.
        if (tracingView == null){
            return;
        }

        // We save the current canvas before we go backwards by subtracting the current letter index.
        saveCurrentCanvas();
        currentLetterIndex--;

        // We check to see if we have not gone past the beginning letter, if so then we set the current letter to Z.
        if (currentLetterIndex < 0){
            currentLetterIndex = alphabet.length - 1;
        }

        // We load the previous bitmap and update the displayed letter.
        loadBitmap(alphabet[currentLetterIndex]);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
    }

    /* saveCurrentCanvas() is responsible for saving the student's drawing both in memory (letterBitmaps HashMap)
    and inside of the database.*/
    public void saveCurrentCanvas(){
        // First we check to see if the tracingView is null or if the bitmap is null.
        // If either one is null, then we return;
        if (tracingView == null || tracingView.getBitmap() == null){
            return;
        }

        // We locate the character by using the currentLetterIndex in the alphabet array.
        char letter = alphabet[currentLetterIndex];

        // If the bitmap is still null at this point, we create a blank canvas as a placeholder.
        Bitmap bitmapCopy = tracingView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        letterBitmaps.put(letter, bitmapCopy);

        // We save the letter drawing into the database.
        if(getActivity() instanceof AlphabetActivity){
            AlphabetActivity activity = (AlphabetActivity) getActivity();

            activity.dbHelper.saveBitmap(activity.aaProfileColor, activity.aaProfileShape, "alphabet", String.valueOf(letter), bitmapCopy);
        }
    }

    /* loadBitmap() loads the saved drawing if it exits. It it does not, we create a blank
    bitmap as a placeholder. The blank bitmap is the same size as the TracingView to avoid width
    or height errors and ensures the canvas is displayed correctly.*/
    private void loadBitmap(char letter){
        Bitmap bitmap = letterBitmaps.get(letter);

        // We check to see if the bitmap is already stored in the memory. If not, then the database is checked.
        if(bitmap == null && getActivity() instanceof AlphabetActivity){
            AlphabetActivity activity = (AlphabetActivity) getActivity();

            bitmap = activity.dbHelper.loadBitmap(activity.aaProfileColor, activity.aaProfileShape, "alphabet", String.valueOf(letter));

            // If the bitmap is loaded from the database, we store it in memory for faster access next time.
            if(bitmap != null){
                letterBitmaps.put(letter, bitmap);
            }
        }

        // If the bitmap is still null at this point, we create a blank canvas as a placeholder.
        if(bitmap == null){
            bitmap = Bitmap.createBitmap(tracingView.getWidth(), tracingView.getHeight(), Bitmap.Config.ARGB_8888);
        }

        // Finally, we set the bitmap into the TracingView.
        tracingView.setBitmap(bitmap);
    }

    /* setCurrentLetterIndex sets the correct starting letter when the student returns. post()
    is used so that the bitmap loads after the view has a proper size, making sure that the
    bitmap loads with the correct width and height. This ensures the drawing loads correctly. */
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