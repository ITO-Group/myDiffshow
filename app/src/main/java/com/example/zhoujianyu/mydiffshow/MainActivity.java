package com.example.zhoujianyu.mydiffshow;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    GridView mGridView;


    public ArrayList<String> getRawCapacityData()throws Exception{
        String line = "";
        ArrayList<String> rawData = new ArrayList<>();
        String command[] = {"aptouch_daemon_debug", "diffdata"};
        Process process = new ProcessBuilder(new String[] {"aptouch_daemon_debug", "diffdata"}).start();
        InputStream procInputStream = process.getInputStream();
        InputStreamReader reader = new InputStreamReader(procInputStream);
        BufferedReader bufferedreader = new BufferedReader(reader);
        while ((line = bufferedreader.readLine()) != null) {
            rawData.add(line);
        }
        return rawData;
    }

    public void refresh()throws Exception{
        ArrayList<String> rawData = getRawCapacityData();
        short data[] = new short[28*16];
        short tmp[][] = new short[16][28];
        for(int i = 0;i<rawData.size();i++){
            StringTokenizer t = new StringTokenizer(rawData.get(i));
            int j = 0;
            while(t.hasMoreTokens()){
                tmp[i][j++] = Short.parseShort(t.nextToken());
            }
        }
        int k = 0;
        for(int j = 0;j<28;j++){
            for(int i = 0;i<16;i++){
                data[k++] = tmp[i][j];
            }
        }

        mGridView.updateCapa(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGridView.invalidate();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sample_grid_view);
        mGridView = findViewById(R.id.grid_view);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        mGridView.screenHeight = screenHeight;
        mGridView.screenWidth = screenWidth;

        mGridView.init();
        readDiffStart();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        refresh();
//                    }catch (Exception e){
//
//                    }
//                }
//            }
//        }).start();

    }
    /**
     * callback method after everytime native_lib.cpp read an image of capacity data
     * The function first convert
     * @param data: 32*16 short array
     */
    public void processDiff(short[] data) throws InterruptedException{
        // convert short array to a single string in convenience of data storage
        String capaString = "";
        for(int i = 0;i<data.length;i++){
            capaString+=(" "+Short.toString(data[i]));
        }
        mGridView.updateCapa(data);
        mGridView.invalidate();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void readDiffStart ();
    public native void readDiffStop ();
}
