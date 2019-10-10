package com.droid2developers.frozenocr.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.droid2developers.frozenocr.R;
import com.droid2developers.frozenocr.custom.RotateTransformation;
import com.droid2developers.frozenocr.model.OCRModel;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lyft.android.scissors.CropView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.droid2developers.frozenocr.utils.Constants.RATIO_FULLSCREEN;
import static com.droid2developers.frozenocr.utils.Constants.RATIO_SQUARE;

public class CropActivity extends AppCompatActivity {

    private final String TAG = CropActivity.class.getSimpleName();
    private CropView imageView;
    private String imageUrl;
    private ProgressDialog mProgressDialog;
    private float currentRotation = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop);

        Bundle extras = getIntent().getExtras();
        imageView = findViewById(R.id.crop_view);
        mProgressDialog = new ProgressDialog(this);
        CardView cropButton = findViewById(R.id.cropButtonId);
        FloatingActionButton rotateButton = findViewById(R.id.rotateButtonId);
        FloatingActionButton ratioButton = findViewById(R.id.ratioButtonId);



        if (extras !=null){
            imageUrl = extras.getString("imageURL");
        }

        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                .into(imageView);


        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDialog("Processing Image...");
                Callback(Objects.requireNonNull(imageView.crop()));

            }
        });



        ratioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageView.getViewportRatio() == RATIO_SQUARE){
                    imageView.setViewportRatio(RATIO_FULLSCREEN);
                }else {
                    imageView.setViewportRatio(RATIO_SQUARE);
                }
            }
        });


        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentRotation == 0f){
                    RotateImage(90f);
                }else if (currentRotation == 90f){
                    RotateImage(180f);
                }else if (currentRotation == 180f){
                    RotateImage(270f);
                }else {
                    RotateImage(0f);
                }

            }
        });

    }

    public void RotateImage(float rotationAngle){
        currentRotation = rotationAngle;
        Glide.with(CropActivity.this)
                .load(imageUrl)
                .transform(new RotateTransformation(rotationAngle))
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                .into(imageView);
    }


    public void Callback(final Bitmap cropped){

        new RecognizeImage().execute(cropped);
    }



    private void CreateDialog(String message){

        mProgressDialog.setIndeterminate(true);
        // Progress dialog horizontal style
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Progress dialog title
        mProgressDialog.setTitle("Progress");
        // Progress dialog message
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void CloseDialog(){
        mProgressDialog.cancel();
    }

    private Uri saveImage(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getApplicationInfo().dataDir, "files");
        Uri uri = null;
        try {
            boolean wasSuccessful = imagesFolder.mkdirs();
            if (!wasSuccessful) {
                File file = new File(imagesFolder, System.currentTimeMillis() + "_thumb_image.jpg");
                FileOutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
                uri = FileProvider.getUriForFile(this, getPackageName()+ ".provider", file);
            }

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        Log.d(TAG, "saveImage: " + uri);
        return uri;
    }


    @SuppressLint("StaticFieldLeak")
    private class RecognizeImage extends AsyncTask<Bitmap,Integer, OCRModel>{


        @Override
        protected OCRModel doInBackground(Bitmap... bitmaps) {

            TextRecognizer textRecognizer = new TextRecognizer.Builder(CropActivity.this).build();
            Uri savedUri = saveImage(bitmaps[0]);
            String extraText = null;

            if (!textRecognizer.isOperational()) {
                // Note: The first time that an app using a Vision API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any text,
                // bar-codes, or faces.
                //
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
                Log.w(TAG, "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(CropActivity.this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                    Log.w(TAG, getString(R.string.low_storage_error));
                }
            }else {

                Frame frame = new Frame.Builder().setBitmap(bitmaps[0]).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);

                StringBuilder builder = new StringBuilder();
                for (int i=0; i<items.size(); i++){
                    TextBlock textBlock = items.valueAt(i);

                    builder.append(textBlock.getValue());
                    builder.append("\n");
                }
                extraText = builder.toString();
                Log.d(TAG, "onDetect: Text = " + builder.toString());

            }

            return  ((extraText==null && savedUri==null) ? null :
                    new OCRModel(System.currentTimeMillis(), String.valueOf(savedUri),extraText));
        }


        @Override
        protected void onPostExecute(OCRModel ocrModel) {
            super.onPostExecute(ocrModel);

            Log.d(TAG, "onPostExecute: "+ ocrModel.toString());
            Intent intent = new Intent(CropActivity.this,PreviewActivity.class);
            intent.putExtra("timeStamp",ocrModel.getTimeStamp());
            intent.putExtra("savedURL",ocrModel.getImageUri());
            intent.putExtra("extraText",ocrModel.getExtaText());
            startActivity(intent);
            finish();
            CloseDialog();
        }
    }

}
