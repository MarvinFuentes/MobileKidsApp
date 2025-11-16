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
        View view = inflater.inflate(R.layout.counting_fragment, container, false);

        tracingView = view.findViewById(R.id.tracingView);

        /*This segment of code loads the first bitmap after the TracingView is completely laid out.
        post() makes sure that the view has a valid width and height before drawing or loading the
        bitmap, preventing a crash.*/
        tracingView.post(() -> {
            loadBitmap(numberList[currentNumberIndex]);
            tracingView.setLetter(String.valueOf(numberList[currentNumberIndex]));
        });

        return view;
    }

    /*This method returns the current number index in this case we used it to keep track of the phrases used for each number.*/
    public int getCurrentNumberIndex(){
        return currentNumberIndex;
    }

    public void clearCanvas(){
        if(tracingView != null){
            tracingView.clearCanvasBitmap();
        }
    }

    public boolean nextNumberCanvas(){
        if(tracingView == null){
            return false;
        }

        boolean didWrite = tracingView.hasWriting();

        saveCurrentCanvas();
        currentNumberIndex++;
        if(currentNumberIndex >= numberList.length){
            currentNumberIndex = 0;
        }

        loadBitmap(numberList[currentNumberIndex]);
        tracingView.setLetter(numberList[currentNumberIndex]);
        tracingView.resetWritingFlag();
        return didWrite;
    }

    public void previousNumberCanvas(){
        if(tracingView == null){
            return;
        }

        saveCurrentCanvas();
        currentNumberIndex--;
        if(currentNumberIndex < 0){
            currentNumberIndex = numberList.length - 1;
        }

        loadBitmap(numberList[currentNumberIndex]);
        tracingView.setLetter(numberList[currentNumberIndex]);
    }

    public void saveCurrentCanvas(){
        if(tracingView == null || tracingView.getBitmap() == null){
            return;
        }

        Bitmap bitmapCopy = tracingView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        String number = numberList[currentNumberIndex];

        numberBitmaps.put(number, bitmapCopy);

        //Save the Bitmap into the database allowing us to later access it.
        if(getActivity() instanceof CountingActivity){
            CountingActivity activity = (CountingActivity) getActivity();

            activity.dbHelper.saveBitmap(activity.caProfileColor, activity.caProfileShape, "counting", number, bitmapCopy);
        }
    }

    private void loadBitmap(String number){
        Bitmap bitmap = numberBitmaps.get(number);

        if(bitmap == null && getActivity() instanceof CountingActivity){
            CountingActivity activity = (CountingActivity) getActivity();

            bitmap = activity.dbHelper.loadBitmap(activity.caProfileColor, activity.caProfileShape, "counting", number);

            if(bitmap != null){
                numberBitmaps.put(number, bitmap);
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
