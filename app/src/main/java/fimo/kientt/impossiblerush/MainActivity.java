package fimo.kientt.impossiblerush;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements Runnable{

    private Button btnLeft, btnRight;
    private ImageView imgSquare, imgBall, btnStart;
    private int degree, colorSquare, rd_color, score, level, TIME_ROUND, count_fall;
    private boolean isRunning;
    private Handler handler;
    private TextView tvScore, tvLevel;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        initView();
    }

    private void initView(){
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnStart = findViewById(R.id.btn_start);
        imgSquare = findViewById(R.id.imgSquare);
        imgBall = findViewById(R.id.imgBall);
        tvScore = findViewById(R.id.tv_score);
        tvLevel = findViewById(R.id.tv_level);
        imgBall.setVisibility(View.GONE);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotateAnimation = new RotateAnimation(degree, degree - 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(100);
                rotateAnimation.setFillAfter(true);
                imgSquare.startAnimation(rotateAnimation);
                degree = degree - 90;
                colorSquare++;
                if(colorSquare > 3) colorSquare = 0;
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotateAnimation = new RotateAnimation(degree, degree + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(100);
                rotateAnimation.setFillAfter(true);
                imgSquare.startAnimation(rotateAnimation);
                degree = degree + 90;
                colorSquare--;
                if(colorSquare < 0) colorSquare = 3;
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGame();
            }
        });
    }

    private void initGame(){
        level = 1;
        count_fall = 1;
        score = 0;
        degree = 0;
        TIME_ROUND = 2000;
        colorSquare = 0;
        isRunning = true;
        tvScore.setText("Score: 0");
        tvLevel.setText("Level: 1");
        imgSquare.clearAnimation();
        btnStart.setVisibility(View.GONE);
        imgBall.setVisibility(View.VISIBLE);
        Thread thread = new Thread(this);
        thread.start();
    }

    private void startBallTranslate(){
        Random random = new Random();
        rd_color = random.nextInt(4);
        imgBall.post(new Runnable() {
            @Override
            public void run() {
                imgBall.setImageResource(R.drawable.ball_0 + rd_color);
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.8f);
                translateAnimation.setDuration(TIME_ROUND);
                imgBall.startAnimation(translateAnimation);
            }
        });
    }

    private void playMedia(int id){
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(MainActivity.this, id);
        mediaPlayer.start();
    }
    @Override
    public void run() {
        while(true){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Score: " + score + " Level: " + level, Toast.LENGTH_SHORT ).show();
                }
            });
            if (count_fall > level * 10 && level < 7){
                level++;
                TIME_ROUND = TIME_ROUND - 200;
            }
            if(isRunning){
                startBallTranslate();
                try {
                    Thread.sleep(TIME_ROUND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(colorSquare == rd_color){
                    score = score + level;
                    count_fall++;
                    playMedia(R.raw.point);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvLevel.setText("Level: " + level);
                            tvScore.setText("Score: " + score);
                            playMedia(R.raw.point);
                        }
                    });
                }
                else{
                    isRunning = false;
                    playMedia(R.raw.gameover);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnStart.setVisibility(View.VISIBLE);
                            imgBall.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    }
}
