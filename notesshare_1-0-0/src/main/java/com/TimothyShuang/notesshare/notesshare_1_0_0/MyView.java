package com.TimothyShuang.notesshare.notesshare_1_0_0;

/**
 * Created by tbradford16 on 4/30/14.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class MyView extends View{

    public int pen_size = 1;
    public int pen_Color = 0xFF000000;

    private static Canvas mCanvas ;
    private static int width;
    private static int height;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private static Bitmap mBitmap;
    private Path mPath;
    float mX,mY;
    private static final float TOUCH_TOLERANCE = 4;
    //±£´æPathÂ·¾¶µÄ¼¯ºÏ£¬ÓÃList¼¯ºÏÀ´Ä£ÄâÕ»
    private static List<DrawPath> savePath;
    private static List<DrawPath> savePath_other;
    private static DrawPath dp;
    // private static List<Integer> saveColor;
    // private static List<Integer> saveSize;

    HomeActivity homeActivity = new HomeActivity();


    private class DrawPath{
        public Path path;//Â·¾¶
        public Paint paint;//»­±Ê
        //public int colors;
        //public int sizes;


        // private DrawPath(){
        //  invalidate();
        //  this.paint.setColor(pen_Color);
        //  this.paint.setStrokeWidth(pen_size);
        //}
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public MyView(Context context, AttributeSet attrs, int CanvasWidth, int CanvasHeight) {
        super(context,attrs);
        width = CanvasWidth;
        height = CanvasHeight;
        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //¿¹¾â³Ý

        mPaint.setColor(pen_Color);//ÏßÌõºÚÉ« // sets the pen color

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);//ÉèÖÃÍâ±ßÔµ
        mPaint.setStrokeCap(Paint.Cap.SQUARE);//ÏßµÄÀàÐÍ

        mPaint.setStrokeWidth(pen_size);//»­±Ê¿í¶È // set the pen thickness

        savePath = new ArrayList<DrawPath>();
        savePath_other = new ArrayList<DrawPath>();
    }

    public Canvas getmCanvas(){
        return mCanvas;
    }

    public void B_ground(Canvas canvas,Bitmap bitmap){
        canvas.drawBitmap(bitmap,getLeft(),getTop(),null);
    }

    public String file_save(Context context){//this will take the bitmap saved in cache and will put it in the gallery and return a copy of the bitmap
        return  MediaStore.Images.Media.insertImage(context.getContentResolver(), getDrawingCache(),UUID.randomUUID().toString()+".png", "drawn notes");
    }//http://developer.android.com/reference/android/provider/MediaStore.Images.Media.html

    public void color_change(int color){//this will change the color of the pen

        invalidate();//invalidating the View
        mPaint.setColor(color);// set the color to the new color

        pen_Color = color;
    }

    public void size_change(int size){//this will change the size of the pen
        mPaint.setStrokeWidth(size);

        pen_size = size;
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);

        if(mPath != null)
            canvas.drawPath(mPath , mPaint);
    }
    private void touch_start(float x,float y){
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x,float y){
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            mPath.quadTo(mX, mY, (x+mX)/2, (y+mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up(){
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        savePath.add(dp);
        mPath = null;

    }

    public static void undo_up() throws IOException{//undo
        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        if(savePath != null && savePath.size()>0){


            savePath_other.add(savePath.get(savePath.size() - 1));

            savePath.remove(savePath.size() - 1);
            Iterator<DrawPath> iter = savePath.iterator();
            DrawPath drawPath;
            while (iter.hasNext()) {

                drawPath = iter.next();

                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            HomeActivity.myview.invalidate();
        }
    }
    public static void undo_down(){//redo
        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        if(savePath_other != null && savePath_other.size()>0){

            savePath.add(savePath_other.get(savePath_other.size() - 1));

            savePath_other.remove(savePath_other.size() - 1);
            Iterator<DrawPath> iter = savePath.iterator();
            DrawPath drawPath;
            while (iter.hasNext()) {
                drawPath = iter.next();

                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            HomeActivity.myview.invalidate();
        }
    }

    public static void redo(){//clear
        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        savePath.clear();
        savePath_other.clear();
        HomeActivity.myview.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPath = new Path();
                dp = new DrawPath();


                dp.path = mPath;
                dp.paint = mPaint;

                touch_start(x, y);

                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
        }
        return true;
    }
}