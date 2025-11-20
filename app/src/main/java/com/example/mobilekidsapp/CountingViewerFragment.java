package com.example.mobilekidsapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class CountingViewerFragment extends Fragment {
    private TracingView tracingView;
    private int currentNumberIndex = 0;
    private final String[] numberList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private Map<String, Bitmap> numberBitmaps = new HashMap<String, Bitmap>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // We inflate the counting_fragment so we can switch between all of the other numbers in
        // the numberList array without needing multiple activities. This keeps the program easier to
        // manage and easier to grow if we ever need to add more features.
        View view = inflater.inflate(R.layout.counting_fragment, container, false);

        tracingView = view.findViewById(R.id.tracingView);

        /*This segment of code loads the first bitmap only after the TracingView is fully laid out.
        post() makes sure that the view has a valid width and height before loading anything, which
        helps prevent crashes.*/
        tracingView.post(() -> {
            loadBitmap(numberList[currentNumberIndex]);
            tracingView.setLetter(String.valueOf(numberList[currentNumberIndex]));
        });

        return view;
    }

    /*getCurrentNumberIndex() returns the current number index. We mainly use this
      to know which phrase to say for text-to-speech.*/
    public int getCurrentNumberIndex(){
        return currentNumberIndex;
    }

    /* clearCanvas() is used in CountingActivity when the clear canvas button is clicked.
     It simply calls tracingView.clearCanvasBitmap().*/
    public void clearCanvas(){
        if(tracingView != null){
            tracingView.clearCanvasBitmap();
        }
    }

    /* nextNumberCanvas() handles switching to the next letter. It loads the next bitmap,
      updates the number shown on screen, and saves the drawing.*/
    public boolean nextNumberCanvas(){

        // First we check if the tracingVew is null. If it is, we return.
        if(tracingView == null){
            return false;
        }

        // didWrite checks to see if the student drew anything before moving on.
        boolean didWrite = tracingView.hasWriting();

        // We save the current drawing before switching to the next one. We increment the
        // index of the current number.
        saveCurrentCanvas();
        currentNumberIndex++;

        // If we go past the numberList array then we reset back to the number 1.
        if(currentNumberIndex >= numberList.length){
            currentNumberIndex = 0;
        }

        // Finally, we load the next bitmap by calling the loadBitmap and passing the current index of the
        // numberList array and we update the number on the screen. We also reset the TracingView by calling
        // resetWritingFlag() so it is ready for the next drawing.
        loadBitmap(numberList[currentNumberIndex]);
        tracingView.setLetter(numberList[currentNumberIndex]);
        tracingView.resetWritingFlag();

        return didWrite;
    }

    /* previousLetterCanvas() loads the previous bitmap and lets the student go back.*/
    public void previousNumberCanvas(){
        // First we check to see if the tracingView is null. If it is, we return.
        if(tracingView == null){
            return;
        }

        // We save the current canvas before we go backwards by subtracting the current number index.
        saveCurrentCanvas();
        currentNumberIndex--;

        // We check if we have not gone past the beginning number, if so then we set the current number to 10.
        if(currentNumberIndex < 0){
            currentNumberIndex = numberList.length - 1;
        }

        // We load the previous bitmap and update the displayed number.
        loadBitmap(numberList[currentNumberIndex]);
        tracingView.setLetter(numberList[currentNumberIndex]);
    }

    /* saveCurrentCanvas() is responsible for saving the student's drawing both in memory (numberBitmaps HashMap)
    and inside of the database.*/
    public void saveCurrentCanvas(){
        // First we check to see if the tracingView is null or if the bitmap is null.
        // If either one is null, then we return;
        if(tracingView == null || tracingView.getBitmap() == null){
            return;
        }

        // We create a copy of the current bitmap so we don't modify the original canvas directly.
        Bitmap bitmapCopy = tracingView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        String number = numberList[currentNumberIndex];

        numberBitmaps.put(number, bitmapCopy);

        // We save the number drawing into the database
        if(getActivity() instanceof CountingActivity){
            CountingActivity activity = (CountingActivity) getActivity();

            activity.dbHelper.saveBitmap(activity.caProfileColor, activity.caProfileShape, "counting", number, bitmapCopy);
        }
    }

    /* loadBitmap() loads the saved drawing if it exits. It it does not, we create a blank
       bitmap as a placeholder. The blank bitmap is the same size as the TracingView to avoid width
       or height errors and ensures the canvas is displayed correctly.*/
    private void loadBitmap(String number){
        Bitmap bitmap = numberBitmaps.get(number);

        // We check to see if the bitmap is already stored in the memory. If not, then the database is checked.
        if(bitmap == null && getActivity() instanceof CountingActivity){
            CountingActivity activity = (CountingActivity) getActivity();

            bitmap = activity.dbHelper.loadBitmap(activity.caProfileColor, activity.caProfileShape, "counting", number);

            // If the bitmap is loaded from the database, we store it in memory for faster access next time.
            if(bitmap != null){
                numberBitmaps.put(number, bitmap);
            }
        }

        // If the bitmap is still null at this point, we create a blank canvas as a placeholder.
        if(bitmap == null){
            bitmap = Bitmap.createBitmap(tracingView.getWidth(), tracingView.getHeight(), Bitmap.Config.ARGB_8888);
        }

        // Finally, we set the bitmap into the TracingView.
        tracingView.setBitmap(bitmap);
    }

    /* setCurrentLetterIndex sets the correct starting number when the student returns. post()
    is used so the bitmap loads after the view has a proper size, making sure that the
    bitmap loads with the correct width and height. This ensures the drawing loads correctly. */
    public void setCurrentNumberIndex(int index){
        if(index < 0 || index >= numberList.length) return;

        currentNumberIndex = index;

        if (tracingView != null) {
            tracingView.post(() -> {
                loadBitmap(numberList[currentNumberIndex]);
                tracingView.setLetter(String.valueOf(numberList[currentNumberIndex]));
            });
        }
    }
}
