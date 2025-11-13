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

public class AlphabetViewerFragment extends Fragment {
    private TracingView tracingView;
    private int currentLetterIndex = 0;
    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private Map<Character, Bitmap> letterBitmaps = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.alphabet_fragment, container, false);

        tracingView = view.findViewById(R.id.tracingView);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));

        return view;
    }
    public void clearCanvas() {
        if (tracingView != null){
            tracingView.clearCanvasBitmap();
        }
    }
    public boolean nextLetterCanvas() {
        if (tracingView == null) return false;
        boolean didWrite = tracingView.hasWriting();
        saveCurrentCanvas();
        currentLetterIndex++;
        if (currentLetterIndex >= alphabet.length) currentLetterIndex = 0;
        loadBitmap(alphabet[currentLetterIndex]);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
        tracingView.resetWritingFlag();
        return didWrite;
    }
    public void previousLetterCanvas() {
        if (tracingView == null) return;
        saveCurrentCanvas();
        currentLetterIndex--;
        if (currentLetterIndex < 0) currentLetterIndex = alphabet.length - 1;
        loadBitmap(alphabet[currentLetterIndex]);
        tracingView.setLetter(String.valueOf(alphabet[currentLetterIndex]));
    }

    private void saveCurrentCanvas(){
        if (tracingView == null || tracingView.getBitmap() == null) return;

        char currentLetter = alphabet[currentLetterIndex];
        Bitmap bitmapCopy = tracingView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        letterBitmaps.put(currentLetter, bitmapCopy);
    }

    private void loadBitmap(char letter){
        Bitmap bitmap = letterBitmaps.get(letter);
        tracingView.setBitmap(bitmap);
    }
}