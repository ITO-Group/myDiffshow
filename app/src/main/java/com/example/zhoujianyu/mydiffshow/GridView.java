package com.example.zhoujianyu.mydiffshow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static java.lang.Math.max;

/**
 * TODO: document your custom view class.
 */
public class GridView extends View {
    int screenHeight;
    int screenWidth;

    final static int ROW_NUM = 28;
    final static int COL_NUM = 16;
    final static int maxVal = 2800;
    final static int minVal = 0;
    static int capaWidth;
    static int capaHeight;

    short diffData[][];
    Rect rects[][];
    Paint paints[][];

    public GridView(Context context) {
        super(context);
        Log.e("collect","before init");
        Log.e("collect","after init");
    }

    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("collect","before2");
        Log.e("collect","after2");
    }

    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(){
        // init capa height and width: should be called right after mainactivity pass screen size to it
        capaWidth = screenWidth/COL_NUM;
        capaHeight = screenHeight/ROW_NUM;
        // initialize diffData
        diffData = new short[ROW_NUM][COL_NUM];
        for(int i = 0;i<ROW_NUM;i++){
            for(int j = 0;j<COL_NUM;j++){
                diffData[i][j] = 0;
            }
        }

        // initialize rects
        rects = new Rect[ROW_NUM][COL_NUM];
        for(int i = 0;i<ROW_NUM;i++){
            for(int j = 0;j<COL_NUM;j++){
                int left = j*capaWidth;
                int top = (i-1)*capaHeight;
                int right=left+capaWidth;
                int bottom = top+capaHeight;
                rects[i][j] = new Rect(left,top,right,bottom);
            }
        }

        // initialize paints
        paints = new Paint[ROW_NUM][COL_NUM];
        for(int i = 0;i<ROW_NUM;i++){
            for(int j = 0;j<COL_NUM;j++){
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                paints[i][j] = paint;
            }
        }
    }

    public void updateCapa(short[] data){
        for(int i = 0;i<ROW_NUM;i++){
            for(int j = 0;j<COL_NUM;j++){
                diffData[i][j] = data[i*COL_NUM+j];
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0;i<ROW_NUM;i++){
            for(int j = 0;j<COL_NUM;j++){
                // draw frame
                paints[i][j].setStyle(Paint.Style.STROKE);
                paints[i][j].setColor(Color.BLACK);
                canvas.drawRect(rects[i][j],paints[i][j]);
                // fill in rect with proper color
                paints[i][j].setStyle(Paint.Style.FILL);
                int r=255;int g=255;int b=255;
                int tmp = (Math.abs(diffData[i][j])) / 5;
                g = max(255 - tmp,0);
                r = max(255-tmp,0);
                b = max(255-tmp,0);
                paints[i][j].setColor(Color.rgb(r,g,b));
                canvas.drawRect(rects[i][j],paints[i][j]);
                // draw capacity number
                paints[i][j].setColor(Color.BLACK);
                paints[i][j].setTextSize(30);
                canvas.drawText(Short.toString(diffData[i][j]),(float)(rects[i][j].left)+15,(float)(rects[i][j].top)+30,paints[i][j]);
            }
        }
    }
}