package com.example.galacticdrift;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class HUD {

    private final int LIFE_COUNT = 7;
    private static final int TEXT_SIZE = 90;

    private Paint scorePaint;
    private int score, life;
    private GameView gameView;
    private HealthBar healthBar;
    private int screenW;

    public HUD(GameView gameView) {
        this.gameView = gameView;
        this.screenW = gameView.getScreenWidth();
        this.life = LIFE_COUNT;
        this.score = 0;

        scorePaint = new Paint();
        scorePaint.setColor(Color.parseColor("#00FFFF"));
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        healthBar = new HealthBar();
    }

    public void draw(Canvas canvas) {
        canvas.drawText(String.valueOf(score * 10), 70, 150, scorePaint);
        healthBar.drawHealthBar(canvas, life, screenW);
    }

    public boolean handleCollision() {
        if (life > 0) {
            life--;
            return true;
        } else {
            gameView.setGameOver();
            return false;
        }
    }

    public void increaseScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public void resetLife() {
        life = LIFE_COUNT;
    }

    public void resetScore() {
        score = 0;
    }
}