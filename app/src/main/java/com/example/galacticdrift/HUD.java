package com.example.galacticdrift;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

public class HUD {
    private Paint scorePaint;
    private int score, life;
    private static final int TEXT_SIZE = 90;
    private GameView gameView;
    private HealthBar healthBar;

    public HUD(GameView gameView) {
        this.gameView = gameView;
        this.life = 7;
        this.score = 0;

        scorePaint = new Paint();
        scorePaint.setColor(Color.parseColor("#00FFFF"));
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        scorePaint.setTypeface(Typeface.create(ResourcesCompat.getFont(gameView.context, R.font.nunito), 1300, false));


        healthBar = new HealthBar();
    }

    public void draw(Canvas canvas) {
        canvas.drawText(String.valueOf(score * 10), 50, 150, scorePaint);
        healthBar.drawHealthBar(canvas, life);
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
        life = 5;
    }

    public void resetScore() {
        score = 0;
    }
}