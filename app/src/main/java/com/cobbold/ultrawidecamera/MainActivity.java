package com.cobbold.ultrawidecamera;

import android.content.pm.PackageManager;

import android.os.Bundle;

import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton, switchButton;
    private TextureView textureView;
    private PreviewView previewView;
    private Camera2Helper camera2Helper;
    private CameraXHelper cameraXHelper;
    boolean isUltraWideCamera = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            takePictureButton = findViewById(R.id.btn_takepicture);
            switchButton = findViewById(R.id.camera_switch);
            textureView = findViewById(R.id.camera2_texture);
            camera2Helper = new Camera2Helper(this, this, textureView);
            previewView = findViewById(R.id.cameraX_preview);
//            cameraXHelper = new CameraXHelper(previewView, takePictureButton, this, this);
        if (isUltraWideCamera) {
            textureView.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.GONE);
        } else {
            textureView.setVisibility(View.GONE);
            previewView.setVisibility(View.VISIBLE);
        }
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(v -> {
            if (isUltraWideCamera) {
                camera2Helper.takePicture();
            }
        });
        switchButton.setOnClickListener(view -> {
            isUltraWideCamera = !isUltraWideCamera;
            if (isUltraWideCamera) {
                textureView.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.GONE);
                cameraXHelper.closeCamera();
                camera2Helper = new Camera2Helper(this, this, textureView);
            }else {
                textureView.setVisibility(View.GONE);
                previewView.setVisibility(View.VISIBLE);
                camera2Helper.onPause();
                camera2Helper.closeCamera();
                cameraXHelper = new CameraXHelper(previewView, takePictureButton, this, this);
                cameraXHelper.startCameraX();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Camera2Helper.REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUltraWideCamera && camera2Helper != null) {
            camera2Helper.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (isUltraWideCamera  && camera2Helper != null) {
            camera2Helper.onPause();
        }
        super.onPause();
    }
}