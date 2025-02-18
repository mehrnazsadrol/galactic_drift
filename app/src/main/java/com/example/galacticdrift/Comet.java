package com.example.galacticdrift;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Random;

public class Comet {
    private final int VELOCITY = 5;
    private final int ROTATION_SPEED = 5;
    private final int COLISION_THRESHOHLD = 25;
    private static final int HIT_EFFECT_DURATION = 5000;
    private final int PASSED_COMET_STREAK_REWARD = 10;
    private Handler handler;
    Bitmap comets[] = new Bitmap[4];
    ArrayList<Position> positions;
    ArrayList<Float> rotationSpeed;
    ArrayList<Bitmap> gameComets;
    Random random;
    static int lastColision;
    static boolean hitEffect;
    private GameView gameView;
    private boolean gameIsOver;
    private int screenH, screenW;
    private int passedCometsStreak = 0;


    public Comet (GameView gameView) {
        this.gameView = gameView;
        this.screenH = gameView.getScreenHeight();
        this.screenW = gameView.getScreenWidth();
        Context context = gameView.getContext();
        comets[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock_one);
        comets[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock_two);
        comets[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock_three);
        comets[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock_four);

        lastColision = -1;
        hitEffect = false;
        handler = new Handler();
        random = new Random();
        positions = new ArrayList<>();
        rotationSpeed = new ArrayList<>();
        gameComets = new ArrayList<>();
        gameIsOver = false;
        startGame();
    }
    private void triggerHitEffect() {
        hitEffect = true;
        handler.postDelayed(() -> {
            hitEffect = false;
            gameView.invalidate();
        }, HIT_EFFECT_DURATION);
    }

    private void startGame(){
        int count = random.nextInt(5) + 1;
        for (int i=0; i<count; i++){
            addComet();
            positions.add(getInitialPos());
            rotationSpeed.add(random.nextFloat() * ROTATION_SPEED);
        }
    }
    private void addComet () {

        int idx = random.nextInt(4);
        int scale = random.nextInt(5) + 4;
        int scaledWidth = screenW / scale;
        Bitmap temp = comets[idx];
        if (temp == null || temp.getWidth() <= 0 || temp.getHeight() <= 0) {
            return;
        }
        int scaledHeight = (int) ((float) temp.getHeight() / temp.getWidth() * scaledWidth);
        if (scaledWidth > 0 && scaledHeight > 0) {
            Bitmap comet = Bitmap.createScaledBitmap(temp, scaledWidth, scaledHeight, true);
            gameComets.add(comet);
        }
    }

    private void resetGameComets () {
        int newCount = random.nextInt(2) + 1;
        for (int i=0; i < newCount; i++) {
            addComet();
            positions.add(getInitialPos());
            rotationSpeed.add(random.nextFloat() * ROTATION_SPEED);
        }
    }

    public  int getCometWidth(){
        int idx = gameComets.size() -1;
        return gameComets.get(idx).getWidth();
    }

    private Position getInitialPos() {
        int cX = random.nextInt(screenW - getCometWidth());
        int cY = -random.nextInt(screenH/4) - 50 ;
        int cV = random.nextInt(VELOCITY) + 2;

        Position pos = new Position(cX, cY, cV);
        pos.rotationAngle = random.nextFloat() * 360;
        return pos;
    }

    public void updatePositions(float spaceshipX, float spaceshipY, int spaceshipWidth, int spaceshipHeight) {
        if (gameIsOver) return;
        for (int i = 0; i < positions.size(); i++) {
            Position pos = positions.get(i);
            pos.y += pos.v;
            pos.rotationAngle += rotationSpeed.get(i);

            if (pos.rotationAngle >= 360) {
                pos.rotationAngle -= 360;
            }

            Bitmap cometBitmap = gameComets.get(i);
            int cometWidth = cometBitmap.getWidth();
            int cometHeight = cometBitmap.getHeight();
            float cometX = pos.x;
            float cometY = pos.y;

            if (isCollision(
                spaceshipX, spaceshipY, spaceshipWidth, spaceshipHeight,
                cometX, cometY, cometWidth, cometHeight
            )) {
                if ( i != lastColision) {
                    float collisionArea = getCollisionArea(spaceshipX, spaceshipY, spaceshipWidth, spaceshipHeight,
                        cometX, cometY, cometWidth, cometHeight);
                    float spaceshipArea = spaceshipWidth * spaceshipHeight;

                    if (collisionArea > 0) {
                        float hitPercentage = (collisionArea / spaceshipArea) * 100;
                        if (hitPercentage > COLISION_THRESHOHLD) {
                            lastColision = i;
                            triggerHitEffect();
                            if (!gameView.handleCollision()){
                                gameIsOver = true;
                            }

                        }
                    }
                }

            }

            if (pos.y > screenH) {
                passedCometsStreak++;
                if (i == lastColision) {
                    lastColision = -1;
                } else {
                    gameView.increaseScore();
                }
                gameComets.remove(i);
                positions.remove(i);
                rotationSpeed.remove(i);
                if(gameComets.size() < 8) {
                    resetGameComets();
                }
            }
            if (passedCometsStreak >= PASSED_COMET_STREAK_REWARD) {
                gameView.addLifeToHUD();
                passedCometsStreak = 0;
            }
        }

    }
    private float getCollisionArea(
        float x1, float y1, int width1, int height1,
        float x2, float y2, int width2, int height2
    ) {
        float left = Math.max(x1, x2);
        float right = Math.min(x1 + width1, x2 + width2);
        float top = Math.max(y1, y2);
        float bottom = Math.min(y1 + height1, y2 + height2);

        float intersectionWidth = Math.max(0, right - left);
        float intersectionHeight = Math.max(0, bottom - top);

        return intersectionWidth * intersectionHeight;
    }


    private boolean isCollision( float x1, float y1, int width1, int height1,
                                 float x2, float y2, int width2, int height2
    ) {
        return x1 < x2 + width2 &&
            x1 + width1 > x2 &&
            y1 < y2 + height2 &&
            y1 + height1 > y2;
    }

    private void drawHitEffect (Canvas canvas) {
        Paint borderPaint = new Paint();
        //top
        borderPaint.setShader(new LinearGradient(0, 0, 0,
            50, 0xFFFF0000, 0x00FF0000, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, screenW, 50, borderPaint);
        //bottom
        borderPaint.setShader(new LinearGradient(0,
            screenH - 50, 0, screenH, 0x00FF0000, 0xFFFF0000, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawRect(0, screenH - 50, screenW, screenH, borderPaint);
        //lef
        borderPaint.setShader(new LinearGradient(0, 0, 50, 0,0xFFFF0000, 0x00FF0000, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, 50, screenH, borderPaint);
        //right
        borderPaint.setShader(new LinearGradient(screenW - 50,
            0, screenW, 0, 0x00FF0000, 0xFFFF0000, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawRect(screenW - 50, 0,
            screenW, screenH, borderPaint);
    }

    public void draw(Canvas canvas) {
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(0xFFFF0000);
        borderPaint.setStrokeWidth(5);

        for (int i = 0; i < gameComets.size(); i++) {
            Position pos = positions.get(i);
            Bitmap cometBitmap = gameComets.get(i);
            Matrix matrix = new Matrix();
            matrix.postRotate(pos.rotationAngle, gameComets.get(i).getWidth() / 2, gameComets.get(i).getHeight() / 2);
            matrix.postTranslate(pos.x, pos.y);
            canvas.drawBitmap(cometBitmap, matrix, null);
        }
        if (hitEffect) drawHitEffect(canvas);
    }

    public void reset() {
        gameComets.clear();
        positions.clear();
        rotationSpeed.clear();
        lastColision = -1;
        gameIsOver = false;
    }

}
