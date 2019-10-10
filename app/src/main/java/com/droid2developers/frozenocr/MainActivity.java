package com.droid2developers.frozenocr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.droid2developers.frozenocr.activities.CameraActivity;
import com.droid2developers.frozenocr.activities.CropActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 5;
    private ImageView optionsImage;
    private String imageFilePath;
    private static final int CAPTURE_IMAGE_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optionsImage = findViewById(R.id.optionsImageId);
        CardView optionsButton = findViewById(R.id.optionsButtonId);
        CardView liveButton = findViewById(R.id.liveCardId);
        CardView cameraButton = findViewById(R.id.cameraCardId);
        CardView storageButton = findViewById(R.id.storageCardId);
        CardView historyButton = findViewById(R.id.historyCardId);


        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenOptionsMenu();
            }
        });


        liveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraIntent();
            }
        });


        storageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openStorageIntent();

            }
        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST){
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(MainActivity.this, CropActivity.class);
                intent.putExtra("imageURL",imageFilePath);
                startActivity(intent);
            }
        }else if(resultCode == RESULT_CANCELED) {
            // User Cancelled the action
            Log.d(TAG, "onActivityResult: Action Cancelled!");
        }


        if (requestCode == PICK_IMAGE_REQUEST){

            if (data == null) {
                Toast.makeText(MainActivity.this,"Failed to load picture!",Toast.LENGTH_LONG).show();
                Log.d("Upload","Failed to load picture!");
                return;
            }

            final Uri imageUri = data.getData();
            if (imageUri!=null){
                Intent intent = new Intent(MainActivity.this, CropActivity.class);
                intent.putExtra("imageURL",String.valueOf(imageUri));
                startActivity(intent);
            }else {
                Toast.makeText(this, "Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
            }


        }

    }





    // TODO menu main
    public void OpenOptionsMenu(){


    }


    private void openStorageIntent(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    private void openCameraIntent(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            //Create a file to store the image
            File actualImage = null;
            try {
                actualImage = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                return;
            }
            if (actualImage != null) {
                Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", actualImage);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(pictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
}
