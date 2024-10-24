package com.example.lifeguard.handlers;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.example.lifeguard.R;

public class FlashAndMediaHandler {
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;
    private MediaPlayer sirenPlayer;
    private Handler flashHandler = new Handler();
    private Runnable flashRunnable;

    public FlashAndMediaHandler(Context context) {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        sirenPlayer = MediaPlayer.create(context, R.raw.siren);
    }

    public void startFlashAndSiren() {
        startSiren();
        startFlashing();
    }

    public void stopFlashAndSiren() {
        stopSiren();
        stopFlashing();
    }

    private void startFlashing() {
        flashRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    isFlashlightOn = !isFlashlightOn;
                    cameraManager.setTorchMode(cameraId, isFlashlightOn);
                    flashHandler.postDelayed(this, 500); // Toggle flashlight every 500ms
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };
        flashHandler.post(flashRunnable);
    }

    private void stopFlashing() {
        flashHandler.removeCallbacks(flashRunnable);
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        isFlashlightOn = false;
    }

    private void startSiren() {
        if (!sirenPlayer.isPlaying()) {
            sirenPlayer.setLooping(true);
            sirenPlayer.start();
        }
    }

    private void stopSiren() {
        if (sirenPlayer.isPlaying()) {
            sirenPlayer.stop();
            sirenPlayer.prepareAsync();
        }
    }
}
