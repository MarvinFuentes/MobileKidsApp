package com.example.mobilekidsapp;

import android.graphics.Bitmap;
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
        tracingView.setLetter(String.valueOf(numberList[currentNumberIndex]));

        return view;
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

    private void saveCurrentCanvas(){
        if(tracingView == null || tracingView.getBitmap() == null){
            return;
        }

        String currentNumber = numberList[currentNumberIndex];
        Bitmap bitmapCopy = tracingView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        numberBitmaps.put(currentNumber, bitmapCopy);
    }

    private void loadBitmap(String number){
        Bitmap bitmap = numberBitmaps.get(number);
        tracingView.setBitmap(bitmap);
    }
}
