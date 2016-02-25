package com.mobile.bolt.mobilegrading;

import android.graphics.PorterDuff;
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
/**
 * Created by Neeraj on 2/24/2016.
 */
public class DrawingView extends View{
    
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
    //Back up Bitmap.
    private  Bitmap bitmapBackup;
    //Back up canvas
    private Canvas bitmapBackupCanvas;
    //Debug tag
    private String TAG="MobileGrading";
    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
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
        bitmapBackup = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        bitmapBackupCanvas=new Canvas(bitmapBackup);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }


    public void setPicture (Bitmap bitmap) {
        try {
            drawCanvas.drawBitmap(bitmap, 0, 0, canvasPaint);
            invalidate();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "setPicture: exception");
        }
        setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    
    private void touchStarted()
    {
        bitmapBackupCanvas.drawBitmap(canvasBitmap, 0, 0, null);
    }

    private void undo()
    {
        // TODO: 2/25/2016 finish undo porcess  
        drawCanvas.drawBitmap(bitmapBackup, 0, 0, null); // restore from backup

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
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

}
