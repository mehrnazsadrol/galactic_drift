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

/**
 * Class Name: GameOver
 *
 * Description:
 *     Manages the game over screen, displaying the final score and restart button.
 *     Handles player input for restarting the game.
 *
 * Created By:
 *     Instantiated by GameView when game ends.
 */
public class GameOver {
    private final GameView gameView;
    private final int screenH, screenW;

    private boolean isGameOver;
    private Bitmap background;
    private Rect rectCanvas;
    private Rect buttonRect;

    private Paint textPaint, buttonPaint, buttonTextPaint;

    // Constants for UI design
    private static final int TEXT_SIZE_GAME_OVER = 150;
    private static final int TEXT_SIZE_SCORE = 100;
    private static final int TEXT_SIZE_BUTTON = 80;
    private static final int BUTTON_WIDTH = 400;
    private static final int BUTTON_HEIGHT = 150;
    private static final int BUTTON_CORNER_RADIUS = 50;
    private static final int BUTTON_OFFSET_Y = 200;

    public GameOver(GameView gameView) {
        this.gameView = gameView;
        this.screenH = gameView.getScreenHeight();
        this.screenW = gameView.getScreenWidth();
        this.isGameOver = false;

        initBackground();
        initUI();
    }

    /**
     * Initializes the background, ensuring it scales properly.
     */
    private void initBackground() {
        Bitmap oBackground = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.main_background);
        int oWidth = oBackground.getWidth();
        int oHeight = oBackground.getHeight();

        float screenRatio = (float) screenW / screenH;
        float imageRatio = (float) oWidth / oHeight;

        int cropWidth = oWidth, cropHeight = oHeight;
        int cropX = 0, cropY = 0;

        if (imageRatio > screenRatio) {
            cropWidth = (int) (oHeight * screenRatio);
            cropX = (oWidth - cropWidth) / 2;
        } else if (imageRatio < screenRatio) {
            cropHeight = (int) (oWidth / screenRatio);
            cropY = (oHeight - cropHeight) / 2;
        }

        Bitmap croppedBackground = Bitmap.createBitmap(oBackground, cropX, cropY, cropWidth, cropHeight);
        background = Bitmap.createScaledBitmap(croppedBackground, screenW, screenH, true);
        oBackground.recycle();
        croppedBackground.recycle();
        rectCanvas = new Rect(0, 0, screenW, screenH);
    }

    /**
     * Initializes text and button properties for the Game Over screen.
     */
    private void initUI() {
        // Game Over text
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(ResourcesCompat.getFont(gameView.getContext(), R.font.quicksand_semibold));

        // Restart button
        buttonPaint = new Paint();
        buttonPaint.setColor(Color.rgb(255, 156, 0));  // Orange color
        buttonPaint.setStyle(Paint.Style.FILL);

        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(TEXT_SIZE_BUTTON);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        int centerX = screenW / 2;
        int centerY = screenH / 2 + BUTTON_OFFSET_Y;
        buttonRect = new Rect(
            centerX - BUTTON_WIDTH / 2,
            centerY - BUTTON_HEIGHT / 2,
            centerX + BUTTON_WIDTH / 2,
            centerY + BUTTON_HEIGHT / 2
        );
    }

    /**
     * Draws the Game Over screen.
     *
     * @param canvas The canvas to draw on.
     * @param finalScore The player's final score.
     */
    public void draw(Canvas canvas, int finalScore) {
        canvas.drawBitmap(background, null, rectCanvas, null);

        textPaint.setTextSize(TEXT_SIZE_GAME_OVER);
        canvas.drawText("Game Over", screenW / 2, screenH / 3, textPaint);

        textPaint.setTextSize(TEXT_SIZE_SCORE);
        canvas.drawText("Final Score: " + (finalScore * 10), screenW / 2, screenH * 5 / 12, textPaint);

        canvas.drawRoundRect(new RectF(buttonRect), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS, buttonPaint);
        canvas.drawText("Restart", screenW / 2, buttonRect.centerY() + 30, buttonTextPaint);
    }

    /**
     * Sets the game-over state to true;
     */
    public void setGameOver() {
        this.isGameOver = true;
    }

    /**
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return this.isGameOver;
    }

    /**
     * Handles touch events for restarting the game.
     *
     * @param event The MotionEvent containing touch details.
     * @return True if the restart button was pressed, false otherwise.
     */
    public boolean handleTouchEvent(MotionEvent event) {
        if (!isGameOver) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN && isRestartButtonPressed(event)) {
            restartGame();
            return true;
        }
        return false;
    }

    /**
     * Checks if the restart button was pressed.
     *
     * @param event The MotionEvent with the user's touch coordinates.
     * @return True if the touch was inside the button, false otherwise.
     */
    private boolean isRestartButtonPressed(MotionEvent event) {
        return buttonRect.contains((int) event.getX(), (int) event.getY());
    }

    /**
     * Restarts the game by resetting the game state.
     */
    private void restartGame() {
        isGameOver = false;
        gameView.resetGame();
    }
}
