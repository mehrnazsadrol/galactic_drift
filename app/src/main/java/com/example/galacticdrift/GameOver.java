package com.example.galacticdrift;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class GameOver {
    private GameView gameView;
    private Paint backgroundPaint, textPaint, buttonPaint, buttonTextPaint;
    private Rect buttonRect;
    private boolean isGameOver;
    private int screenH, screenW;

    public GameOver(GameView gameView) {
        this.gameView = gameView;
        this.screenH = gameView.getScreenHeight();
        this.screenW = gameView.getScreenWidth();
        this.isGameOver = false;

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setAlpha(200); // Semi-transparent black

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(150);
        textPaint.setTextAlign(Paint.Align.CENTER);

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
        if (!isGameOver) return;

        // Draw semi-transparent background
        canvas.drawRect(0, 0, screenW, screenH, backgroundPaint);

        // Draw "Game Over" text
        canvas.drawText("Game Over", screenW / 2, screenH / 3, textPaint);

        // Draw final score
        canvas.drawText("Score: " + (finalScore * 10), screenW / 2, screenH / 2, textPaint);

        // Draw restart button
        canvas.drawRect(buttonRect, buttonPaint);
        canvas.drawText("Restart", screenW / 2, buttonRect.centerY() + 30, buttonTextPaint);
    }

    public void setGameOver() {

        this.isGameOver = !isGameOver;
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
//        gameView.resetGame();
    }
}
