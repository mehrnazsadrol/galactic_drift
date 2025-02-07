package com.example.galacticdrift;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class HealthBar {
    private Paint healthPaint, borderPaint;

    public HealthBar() {
        healthPaint = new Paint();
        healthPaint.setColor(Color.GREEN);

        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
    }

    public void drawHealthBar(Canvas canvas, int life) {
        int barWidth = GameView.screenWidth/3;
        int barHeight = 50;
        int right = GameView.screenWidth - GameView.screenWidth/17;
        int top = 150;
        int parallelogramCount = 7;
        int segmentWidth = barWidth / parallelogramCount;
        int skewOffset = 20;

        healthPaint.setColor(life > 3 ? Color.GREEN : (life > 1 ? Color.rgb(255, 165, 0) : Color.RED));

        for (int i = 0; i < parallelogramCount; i++) {
            if (i < life) {
                canvas.drawPath(createParallelogramPath(right - i * segmentWidth, top, segmentWidth, barHeight, skewOffset), healthPaint);
            }
            canvas.drawPath(createParallelogramPath(right - i * segmentWidth, top, segmentWidth, barHeight, skewOffset), borderPaint);
        }
    }

    private Path createParallelogramPath(int right, int top, int width, int height, int skewOffset) {
        Path path = new Path();
        path.moveTo(right, top);
        path.lineTo(right - skewOffset, top - height);
        path.lineTo(right - width - skewOffset, top - height);
        path.lineTo(right - width, top);
        path.close();
        return path;
    }
}
