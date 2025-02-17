package com.example.galacticdrift;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

public class GameOver {
    private GameView gameView;
    private Paint textPaint, buttonPaint, buttonTextPaint;
    private Rect buttonRect;
    private boolean isGameOver;
    private int screenH, screenW;
    private Bitmap background;
    private Rect rectCanvas;

    public GameOver(GameView gameView) {
        this.gameView = gameView;
        this.screenH = gameView.getScreenHeight();
        this.screenW = gameView.getScreenWidth();
        this.isGameOver = false;

        Bitmap oBackground = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.main_background);
        int oWidth = oBackground.getWidth();
        int oHeight = oBackground.getHeight();

        float screenRatio = (float) screenW / screenH;
        float imageRatio = (float) oWidth / oHeight;

        int cropWidth = oWidth;
        int cropHeight = oHeight;
        int cropX = 0;
        int cropY = 0;

        if (imageRatio > screenRatio) {
            cropWidth = (int) (oHeight * screenRatio);
            cropX = (oWidth - cropWidth) / 2;
        } else if (imageRatio < screenRatio) {
            cropHeight = (int) (oWidth / screenRatio);
            cropY = (oHeight - cropHeight) / 2;
        }
        Bitmap croppedBackground = Bitmap.createBitmap(oBackground, cropX, cropY, cropWidth, cropHeight);
        imageRatio = (float) croppedBackground.getWidth()/croppedBackground.getHeight();
        background = Bitmap.createScaledBitmap(croppedBackground, screenW, screenH, true);
        oBackground.recycle();
        croppedBackground.recycle();

        rectCanvas = new Rect(0, 0, screenW, screenH);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(ResourcesCompat.getFont(gameView.getContext(), R.font.quicksand_semibold));

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.rgb(255, 156, 0));
        buttonPaint.setStyle(Paint.Style.FILL);


        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(80);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        int buttonWidth = 400;
        int buttonHeight = 150;
        int centerX = screenW / 2;
        int centerY = screenH / 2 + 200;
        buttonRect = new Rect(centerX - buttonWidth / 2, centerY - buttonHeight / 2,
            centerX + buttonWidth / 2, centerY + buttonHeight / 2);
    }

    public void draw(Canvas canvas, int finalScore) {
        canvas.drawBitmap(background, null, rectCanvas, null);
        textPaint.setTextSize(150);
        canvas.drawText("Game Over", screenW / 2, screenH / 3, textPaint);
        textPaint.setTextSize(100);
        canvas.drawText("Final Score: " + (finalScore * 10), screenW / 2, screenH * 5 / 12, textPaint);
        canvas.drawRoundRect(new RectF(buttonRect), 50f, 50f, buttonPaint);
        canvas.drawText("Restart", screenW / 2, buttonRect.centerY() + 30, buttonTextPaint);
    }

    public void setGameOver() {
        this.isGameOver = !isGameOver;
    }

    public boolean isGameOver(){
        return this.isGameOver;
    }

    public boolean handleTouchEvent(MotionEvent event) {
        if (!isGameOver) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (buttonRect.contains((int) event.getX(), (int) event.getY())) {
                restartGame();
                return true;
            }
        }
        return false;
    }

    private void restartGame() {
        isGameOver = false;
        gameView.resetGame();
    }
}
