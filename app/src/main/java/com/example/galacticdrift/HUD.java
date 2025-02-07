package com.example.galacticdrift;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class HUD {
    private Paint scorePaint;
    private int score, life;
    private static final int TEXT_SIZE = 120;
    private GameView gameView;
    private HealthBar healthBar;

    public HUD(GameView gameView) {
        this.gameView = gameView;
        this.life = 7;
        this.score = 0;

        scorePaint = new Paint();
        scorePaint.setColor(Color.rgb(255, 156, 0));
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        healthBar = new HealthBar();
    }

    public void draw(Canvas canvas) {
        canvas.drawText(String.valueOf(score * 10), 50, 150, scorePaint);
        healthBar.drawHealthBar(canvas, life);
    }

    public void handleCollision() {
        if (life > 0) {
            life--;
        } else {
//            gameView.setGameOver();
        }
    }

    public void increaseScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public void resetLife() {
        life = 5;
    }

    public void resetScore() {
        score = 0;
    }
}