package com.example.galacticdrift;

import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Display;
import android.app.Activity;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;


public class GameView extends View {

    Context context;
    Bitmap background, spaceship;
    final long UPDATE_MILLIS = 20;
    final long SHIFT = 20;
    int screenWidth, screenHeight, spaceshipWidth, spaceshipHeight;
    float spaceshipX, spaceshipY;
    float targetX;
    Rect rectCanvas;
    Handler handler;
    Runnable runnable;
    Comet comet;
    private boolean isTouchHeld = false;
    private GameOver gameOver;
    private HUD hud;
    private Paint borderPaint;



    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

//        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        screenWidth = size.x;
//        int navigationBarHeight = getNavigationBarHeight();
//        screenHeight = size.y  - navigationBarHeight;
//
//        Bitmap oBackground = BitmapFactory.decodeResource(getResources(), R.drawable.game_background_three);
//        int oWidth = oBackground.getWidth();
//        int oHeight = oBackground.getHeight();
//        int scaledWidth = (int) ((float) screenHeight / oHeight * oWidth);
//        background = Bitmap.createScaledBitmap(oBackground, scaledWidth, screenHeight, true);
//        oBackground.recycle();

//        Rect windowsRect = context.getWindowManager().getCurrentWindowMetrics().getBounds();
//        screenWidth = windowsRect.width();
//        int navigationBarHeight = getNavigationBarHeight();
//        screenHeight = windowsRect.height();

        Log.d("game", "screen width: "+ screenWidth+", screen height: "+ screenHeight);


        Bitmap oBackground = BitmapFactory.decodeResource(getResources(), R.drawable.game_background_three);
        int oWidth = oBackground.getWidth();
        int oHeight = oBackground.getHeight();
        Log.d("game", "oWidth: "+ oWidth+", oHeight: "+ oHeight);

        float screenRatio = (float) screenWidth / screenHeight;
        float imageRatio = (float) oWidth / oHeight;
        Log.d("game", "original ratio: "+ screenRatio);
        Log.d("game", "our ratio: "+imageRatio);

        int cropWidth = oWidth;
        int cropHeight = oHeight;
        int cropX = 0;
        int cropY = 0;

        if (imageRatio > screenRatio) {
            Log.d("game", "cropped width");
            // Image is wider than screen, crop the width
            cropWidth = (int) (oHeight * screenRatio);
            cropX = (oWidth - cropWidth) / 2;
        } else if (imageRatio < screenRatio) {
            Log.d("game", "cropped height");
            // Image is taller than screen, crop the height
            cropHeight = (int) (oWidth / screenRatio);
            cropY = (oHeight - cropHeight) / 2;
        }
        Bitmap croppedBackground = Bitmap.createBitmap(oBackground, cropX, cropY, cropWidth, cropHeight);
        imageRatio = (float) croppedBackground.getWidth()/croppedBackground.getHeight();
        Log.d("game", "cropped aspec ratio: "+ imageRatio + " width: "+ croppedBackground.getWidth()+", heihgt: "+ croppedBackground.getHeight());
        background = Bitmap.createScaledBitmap(croppedBackground, screenWidth, screenHeight, true);
        oBackground.recycle();
        croppedBackground.recycle();

        borderPaint = new Paint();
        borderPaint.setColor(Color.YELLOW);    // Set the border color to yellow
        borderPaint.setStyle(Paint.Style.STROKE);  // Stroke style for borders
        borderPaint.setStrokeWidth(20);


        Bitmap oSpaceship = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
        int scaledWidth = (int) ((float) screenWidth / 10);
        int originalWidth = oSpaceship.getWidth();
        int originalHeight = oSpaceship.getHeight();
        int scaledHeight = (int) ((float) originalHeight * scaledWidth / originalWidth );
        spaceship = Bitmap.createScaledBitmap(oSpaceship, scaledWidth, scaledHeight, true);
        oSpaceship.recycle();

        spaceshipWidth = scaledWidth;
        spaceshipHeight = scaledHeight;
        rectCanvas = new Rect(0, 0, screenWidth, screenHeight);
        spaceshipX = screenWidth/2 - spaceshipWidth/2;
        spaceshipY = screenHeight -  ((float) 1.75 * scaledHeight);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run(){
                if (isTouchHeld) {
                    float spaceshipCenterX = spaceshipX + spaceshipWidth / 2;
                    float distanceToTarget = targetX - spaceshipCenterX;

                    if (Math.abs(distanceToTarget) <= SHIFT) {
                        spaceshipX += distanceToTarget;
                    } else if (distanceToTarget > 0) {
                        // Move right
                        spaceshipX += SHIFT;
                    } else {
                        // Move left
                        spaceshipX -= SHIFT;
                    }

                    if (spaceshipX < 0) {
                        spaceshipX = 0;
                    } else if (spaceshipX + spaceshipWidth > screenWidth) {
                        spaceshipX = screenWidth - spaceshipWidth;
                    }
                }
                comet.updatePositions(spaceshipX, spaceshipY, spaceshipWidth, spaceshipHeight);
                invalidate();
                handler.postDelayed(this, UPDATE_MILLIS);
            }
        };
        hud = new HUD(this);
        comet = new Comet(this);
        gameOver = new GameOver(this);
        handler.post(runnable);
    }
    private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectCanvas, null);
        canvas.drawRect(0, 0, screenWidth, screenHeight, borderPaint);
//        canvas.drawRect(0,0, screenWidth, screenHeight+ getNavigationBarHeight(), borderPaint);
        canvas.drawBitmap(spaceship, spaceshipX, spaceshipY, null);
        comet.draw(canvas);
        hud.draw(canvas);
        gameOver.draw(canvas, hud.getScore());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        targetX = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isTouchHeld = true;
                break;
            case MotionEvent.ACTION_UP:
                isTouchHeld = false;
                break;
        }

        return true;
    }

    public boolean handleCollision() {
        return hud.handleCollision();
    }
    public void increaseScore () {
        hud.increaseScore();
    }

    public void setGameOver(){
        gameOver.setGameOver();
    }
    public int getScreenWidth() {
       return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }

//    public void resetGame() {
//        spaceshipX = screenWidth / 2 - spaceshipWidth / 2;
//        spaceshipY = screenHeight - ((float) 1.75 * spaceshipHeight);
//        hud.resetLife();
//        hud.resetScore();
//        comet = new Comet(this);
//        gameOver.setGameOver(false);
//    }

}
