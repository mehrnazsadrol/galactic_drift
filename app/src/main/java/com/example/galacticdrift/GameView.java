package com.example.galacticdrift;

import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.Display;
import android.app.Activity;


public class GameView extends View {

    Context context;
    Bitmap background, spaceship;
    static int screenWidth, screenHeight;
    float spaceshipX, spaceShipY;
    Rect rectCanvas;
    Handler handler;
    Runnable runnable;
    
    public GameView(Context context) {
        super(context);
        this.context = context;

        background = BitmapFactory.decodeResource(getResources(), R.drawable.galaxy_background);
        spaceship = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        rectCanvas = new Rect( 0,0, screenWidth, screenHeight);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run(){
                invalidate();
            }
        };


    }
}
