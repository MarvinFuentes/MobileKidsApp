package com.example.mobilekidsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TracingView extends View {
    private Path afDrawPath;
    private Paint afDrawPaint;
    private Paint afCanvasPaint;
    private Canvas alphabetCanvas;
    private Bitmap alphabetBitmap;
    private Paint letterPaint;
    private boolean hasWriting = false;


    public TracingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //sets the style of brush
    //sets the style for the letter canvas (also used for counting activity)
    private void setupDrawing(){
        afDrawPath = new Path();
        afDrawPaint = new Paint();
        afDrawPaint.setColor(Color.BLUE);
        afDrawPaint.setAntiAlias(true);
        afDrawPaint.setStrokeWidth(40);
        afDrawPaint.setStyle(Paint.Style.STROKE);
        afDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        afDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        afCanvasPaint = new Paint(Paint.DITHER_FLAG);

        letterPaint = new Paint();
        letterPaint.setColor(Color.LTGRAY);
        letterPaint.setTextSize(600);
        letterPaint.setTextAlign(Paint.Align.CENTER);
        letterPaint.setAlpha(100);
    }

    // Called when the size of the view changes; creates the bitmap to draw on
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        alphabetBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        alphabetCanvas = new Canvas(alphabetBitmap);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //draw current letter in the background
        canvas.drawText(currentLetter, getWidth() / 2f, getHeight() / 1.5f, letterPaint);

        canvas.drawBitmap(alphabetBitmap, 0, 0, afCanvasPaint);
        //draw current path user is actively drawing
        canvas.drawPath(afDrawPath, afDrawPaint);
    }

    //handles touch events so the brush tracks with the finger
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                afDrawPath.moveTo(x, y); //new path
                hasWriting = true;
                break;
            case MotionEvent.ACTION_MOVE:
                afDrawPath.lineTo(x, y); //extend path as finger moves
                hasWriting = true;
                break;
            case MotionEvent.ACTION_UP:
                // Commit the path to the bitmap
                alphabetCanvas.drawPath(afDrawPath, afDrawPaint);
                afDrawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    //method to clear the current drawing
    public void clearCanvasBitmap() {
        afDrawPath.reset();
        //clears the bitmap
        alphabetBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    private String currentLetter = "A";

    //sets the background letter in the bitmap
    public void setLetter(String letter){
        this.currentLetter = letter;
        invalidate();
    }

    //gets bitmap with users input
    public Bitmap getBitmap(){
        return alphabetBitmap;
    }

    //set the bitmap, used to restore previous bitmaps
    public void setBitmap(Bitmap bitmap){
        if (bitmap != null) {
            //make a copy of the bitmap if it exists
            alphabetBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            alphabetCanvas = new Canvas(alphabetBitmap);
        } else {
            clearCanvasBitmap();
        }
        invalidate();
    }

    //resets the flag marking if a user has drawn or not
    public void resetWritingFlag(){
        hasWriting = false;
    }

    //check if user has drawn
    public boolean hasWriting(){
        return hasWriting;
    }

}
