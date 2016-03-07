package com.mobile.bolt.mobilegrading;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 2/24/2016.
 */
public class DrawingView extends View {
    
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFFFF0000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //Debug tag
    private String TAG="MobileGrading";

    private boolean isErasing = false;
    private List<Path> moveList = null;
    private List<Path> undoList = null;
    private List<Path> currentMoveList = null;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        moveList = new ArrayList<Path>();
        undoList = new ArrayList<Path>();
        currentMoveList =  new ArrayList<Path>();
        setupDrawing();
    }
    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    //get drawing area setup for interaction
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        for (Path path : currentMoveList) {
            canvas.drawPath(path, drawPaint);
        }
        for (Path path : moveList) {
            canvas.drawPath(path, drawPaint);
        }
        canvas.drawPath(drawPath, drawPaint);
    }


    public void setPicture (Bitmap bitmap) {
        try {
//            drawCanvas.drawBitmap(bitmap, 0, 0, canvasPaint);
//            invalidate();
            while (moveList.size() > 0)
                undo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setPicture: exception");
        }
        setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    public void undo() {
        if (moveList.size() > 0) {
            undoList.add(moveList.remove(moveList.size() - 1));
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       float touchX = event.getX();
       float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                currentMoveList.add(drawPath);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                moveList.add(drawPath);
                drawPath = new Path();
                currentMoveList.clear();
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

}
