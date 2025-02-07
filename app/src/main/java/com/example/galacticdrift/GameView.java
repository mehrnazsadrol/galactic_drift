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
    static int screenWidth, screenHeight, spaceshipWidth, spaceshipHeight;
    float spaceshipX, spaceshipY;
    float targetX;
    Rect rectCanvas;
    Handler handler;
    Runnable runnable;
    Comet comet;
    private boolean isTouchHeld = false;
    private GameOver gameOver;
    private HUD hud;



    public GameView(Context context) {
        super(context);
        this.context = context;

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        int navigationBarHeight = getNavigationBarHeight();
        screenHeight = size.y  - navigationBarHeight;

        Bitmap oBackground = BitmapFactory.decodeResource(getResources(), R.drawable.game_background_three);
        int oWidth = oBackground.getWidth();
        int oHeight = oBackground.getHeight();
        int scaledWidth = (int) ((float) screenHeight / oHeight * oWidth);
        background = Bitmap.createScaledBitmap(oBackground, scaledWidth, screenHeight, true);
        oBackground.recycle();

        Bitmap oSpaceship = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
        scaledWidth = (int) ((float) screenWidth / 10);
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

//    public void resetGame() {
//        spaceshipX = screenWidth / 2 - spaceshipWidth / 2;
//        spaceshipY = screenHeight - ((float) 1.75 * spaceshipHeight);
//        hud.resetLife();
//        hud.resetScore();
//        comet = new Comet(this);
//        gameOver.setGameOver(false);
//    }

}
