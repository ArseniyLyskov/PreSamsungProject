package com.example.presamsungproject.Activities.General;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.*;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.GameObjects.TankSight;
import com.example.presamsungproject.Models.Resources;
import com.example.presamsungproject.R;

import java.util.HashSet;

public class LoadingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loading, null);

        FrameLayout frameLayout = v.findViewById(R.id.fl_loading_view_container);
        frameLayout.addView(new LoadingView(getContext()));

        return v;
    }

    static class LoadingView extends SurfaceView implements SurfaceHolder.Callback {
        private LoadingThread loadingThread;

        public LoadingView(Context context) {
            super(context);
            setZOrderOnTop(true);
            getHolder().addCallback(this);
            getHolder().setFormat(PixelFormat.TRANSPARENT);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            loadingThread = new LoadingThread(holder);
            loadingThread.setRunning(true);
            loadingThread.start();
            LoadingTimer loadingTimer = new LoadingTimer(Integer.MAX_VALUE, (int) (1000 / 50f));
            loadingTimer.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            loadingThread.setRunning(false);
            boolean retry = true;
            while (retry) {
                try {
                    loadingThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    //
                }
            }
        }

        static class LoadingThread extends Thread {
            private final SurfaceHolder surfaceHolder;
            public volatile boolean isTimeToUpdate = false;
            private volatile boolean running = false;
            private final Paint paint;
            private double x = -1, y = -1, angleH = -1, angleT = -1;

            public LoadingThread(SurfaceHolder surfaceHolder) {
                this.surfaceHolder = surfaceHolder;
                Canvas canvas = surfaceHolder.lockCanvas();
                surfaceHolder.unlockCanvasAndPost(canvas);
                paint = Resources.getInstance().getDefaultPaint();
            }

            public void setRunning(boolean run) {
                running = run;
            }

            @Override
            public void run() {
                while (running) {
                    if (isTimeToUpdate) {
                        Canvas canvas = surfaceHolder.lockCanvas();
                        if (canvas != null) {
                            try {
                                //
                                Bitmap hull = Resources.getInstance().getBmp_greenHp();
                                Bitmap tower = Resources.getInstance().getBmp_greenTp();
                                if (x == -1) {
                                    x = (canvas.getWidth() - hull.getHeight()) / 2f;
                                    y = canvas.getHeight() - hull.getWidth();
                                    angleH = -90;
                                    angleT = 0;
                                }
                                Tank tank = new Tank(1, 1, x, y, angleH, angleT,
                                        "", new TankSight(), new HashSet<>());
                                tank.draw(canvas, paint, hull, tower);
                                x += Math.cos(Math.toRadians(90 - angleH)) * 4;
                                y -= Math.sin(Math.toRadians(90 - angleH)) * 4;
                                angleH += 2;
                                angleT -= 5;
                                //
                            } finally {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                                isTimeToUpdate = false;
                            }
                        }
                    }
                }
            }
        }

        class LoadingTimer extends CountDownTimer {
            public LoadingTimer(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                loadingThread.isTimeToUpdate = true;
            }

            @Override
            public void onFinish() {

            }
        }
    }
}
