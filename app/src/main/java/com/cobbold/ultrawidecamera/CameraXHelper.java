package com.cobbold.ultrawidecamera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraXHelper {
    private static final String TAG = "CameraXHelper";
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public final int REQUEST_CODE_PERMISSIONS = 1001;
    PreviewView mPreviewView;
    Button captureImage;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Context context;
    private final Activity activity;
    private Camera camera;
    ProcessCameraProvider cameraProvider;

    public CameraXHelper(PreviewView mPreviewView,Button captureImage ,Context context, Activity activity) {
        this.mPreviewView = mPreviewView;
        this.context = context;
        this.activity = activity;
        this.captureImage = captureImage;
        if (!allPermissionsGranted()) {
//            startCameraX();
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }



    public void startCameraX() {
        Log.d(TAG, "is camera x open");
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {

                cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                bindPreview(cameraProvider);

            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(context));
        Log.d(TAG, "startCameraX");
    }

    void bindPreview(ProcessCameraProvider cameraProvider) {
        if (camera != null && cameraProvider != null) {
            cameraProvider.unbindAll();
            camera = null;
        }
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        assert cameraProvider != null;
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, preview);
    }


//    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        // Create a Preview use case
//        Preview preview = new Preview.Builder()
//                .build();
//
//        // Create an ImageCapture use case
//        ImageCapture imageCapture = new ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                .build();
//
//        // Bind the preview and imageCapture to the camera
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
//        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, preview, imageCapture);
//
//        captureImage.setOnClickListener(v -> {
//            // Capture an image
//            File file = createImageFile();
//            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//
//            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
//                @Override
//                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        Toast.makeText(activity, "Image Saved successfully", Toast.LENGTH_SHORT).show();
//                    });
//                }
//
//                @Override
//                public void onError(@NonNull ImageCaptureException error) {
//                    error.printStackTrace();
//                }
//            });
//        });
//
//        // Bind the preview to the PreviewView
//        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
//    }

    private File createImageFile() {
        // Create a unique file for each image captured
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/images");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return new File(storageDir, imageFileName);
    }


    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void closeCamera() {
        if (camera != null) {
            camera = null;
        }
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }
    }
}
