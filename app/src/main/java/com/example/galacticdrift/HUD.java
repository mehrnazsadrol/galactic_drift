package com.example.galacticdrift;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Class Name: HUD (Heads-Up Display)
 *
 * Description:
 *      This class manages the display of the player's score and health bar.
 *      It is responsible for updating and drawing the score and lives on the screen.
 *
 * Created By:
 *      Instantiated by GameView.
 */
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


    /**
     * Handles collision events by reducing the player's life count.
     * If the life count reaches zero, it signals game over.
     *
     * @return True if the player still has lives remaining, false if game over is triggered.
     */
    public boolean handleCollision() {
        if (life > 1) {
            life--;
            return true;
        } else {
            gameView.setGameOver();
            return false;
        }
    }

    /**
     * Increases the player's score when a comet is dodged.
     */
    public void increaseScore() {
        score++;
    }


    /**
     * @return The current player score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Resets the player's life count back to the maximum.
     */
    public void resetLife() {
        life = LIFE_COUNT;
    }


    /**
     * Resets the player's score to zero.
     */
    public void resetScore() {
        score = 0;
    }

    /**
     * Increases the player's life count when a reward is earned.
     * Does not exceed the maximum life count.
     */
    public void addLife() {
        if ( life < 7)
            life ++;
    }
}