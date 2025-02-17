package com.example.galacticdrift;

import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

    Context context;
    Bitmap background, spaceship;
    private static final long UPDATE_INTERVAL_MS = 20;
    private static final float MOVE_SHIFT = 20f;
    private final int screenWidth, screenHeight;
    private int spaceshipWidth, spaceshipHeight;
    private float spaceshipX, spaceshipY;
    private float targetX;
    private boolean isTouchHeld = false;
    private Rect rectCanvas;
    private Handler handler;
    private Runnable runnable;
    private Comet comet;
    private HUD hud;
    private GameOver gameOver;



    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        initBackground();
        iniSpaceship();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run(){
                if (isTouchHeld) {
                    moveSpaceshipTowardsTarget();
                }
                comet.updatePositions(spaceshipX, spaceshipY, spaceshipWidth, spaceshipHeight);
                invalidate();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };

        gameOver = new GameOver(this);
        hud = new HUD(this);
        comet = new Comet(this);

        handler.post(runnable);
    }

    private void moveSpaceshipTowardsTarget() {
        float spaceshipCenterX = spaceshipX + spaceshipWidth / 2;
        float distanceToTarget = targetX - spaceshipCenterX;

        if (Math.abs(distanceToTarget) <= MOVE_SHIFT) {
            spaceshipX += distanceToTarget;
        } else if (distanceToTarget > 0) {
            // Move right
            spaceshipX += MOVE_SHIFT;
        } else {
            // Move left
            spaceshipX -= MOVE_SHIFT;
        }

        if (spaceshipX < 0) {
            spaceshipX = 0;
        } else if (spaceshipX + spaceshipWidth > screenWidth) {
            spaceshipX = screenWidth - spaceshipWidth;
        }
    }


    private void initBackground() {
        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.game_background);
        int originalWidth = originalBackground.getWidth();
        int originalHeight = originalBackground.getHeight();

        float screenRatio = (float) screenWidth / screenHeight;
        float imageRatio = (float) originalWidth / originalHeight;

        int cropWidth = originalWidth;
        int cropHeight = originalHeight;
        int cropX = 0;
        int cropY = 0;

        if (imageRatio > screenRatio) {
            cropWidth = (int) (originalHeight * screenRatio);
            cropX = (originalWidth - cropWidth) / 2;
        } else if (imageRatio < screenRatio) {
            cropHeight = (int) (originalWidth / screenRatio);
            cropY = (originalHeight - cropHeight) / 2;
        }
        Bitmap croppedBackground = Bitmap.createBitmap(originalBackground, cropX, cropY, cropWidth, cropHeight);
        background = Bitmap.createScaledBitmap(croppedBackground, screenWidth, screenHeight, true);
        originalBackground.recycle();
        croppedBackground.recycle();
    }

    private void iniSpaceship(){

        Bitmap originalSpaceship = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
        int scaledWidth = (int) ((float) screenWidth / 10);
        int originalWidth = originalSpaceship.getWidth();
        int originalHeight = originalSpaceship.getHeight();
        int scaledHeight = (int) ((float) originalHeight * scaledWidth / originalWidth );
        spaceship = Bitmap.createScaledBitmap(originalSpaceship, scaledWidth, scaledHeight, true);
        originalSpaceship.recycle();

        spaceshipWidth = scaledWidth;
        spaceshipHeight = scaledHeight;
        rectCanvas = new Rect(0, 0, screenWidth, screenHeight);
        spaceshipX = screenWidth/2 - spaceshipWidth/2;
        spaceshipY = screenHeight -  ((float) 1.75 * scaledHeight);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectCanvas, null);
        canvas.drawBitmap(spaceship, spaceshipX, spaceshipY, null);
        comet.draw(canvas);
        hud.draw(canvas);

        if (gameOver.isGameOver()) {
            gameOver.draw(canvas, hud.getScore());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if (gameOver.isGameOver()) {
            gameOver.handleTouchEvent(event);
            return true;
        }

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

    public void resetGame() {
        hud.resetScore();
        hud.resetLife();
        comet.reset();

        spaceshipX = screenWidth / 2 - spaceshipWidth / 2;
        spaceshipY = screenHeight - (1.75f * spaceshipHeight);

        invalidate();
    }


}
