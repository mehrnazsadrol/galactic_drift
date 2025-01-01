package com.example.galacticdrift;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Button blinkingButton = findViewById(R.id.blinking_button);
            Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation);
            blinkingButton.startAnimation(blinkAnimation);
        }
        public void startGame(View view){
            GameView gameView = new GameView(this);
            setContentView(gameView);
        }
}