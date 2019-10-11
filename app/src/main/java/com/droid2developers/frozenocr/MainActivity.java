package com.droid2developers.frozenocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.droid2developers.frozenocr.activities.LiveActivity;
import com.droid2developers.frozenocr.activities.CropActivity;
import com.droid2developers.frozenocr.activities.HistoryActivity;
import com.droid2developers.frozenocr.controller.SQLiteHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 5;
    private ImageView optionsImage;
    private String imageFilePath;
    private SQLiteHandler sqLiteHandler;
    private static final int CAPTURE_IMAGE_REQUEST = 10;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

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
        sqLiteHandler = new SQLiteHandler(this);


        if (!checkPermissions()){

        }


        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupMenu();
            }
        });


        liveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, LiveActivity.class);
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



        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionsList, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {

                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        finish();
                    }
                }

            }
            return;
        }
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


    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }



    private void openPopupMenu(){


        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MainActivity.this, optionsImage);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.main_menu,popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.clear_menuID:
                        ShowDeleteDialog();
                        break;
                    case R.id.feedback_menuID:
                        /* Create the Intent */
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                        /* Fill it with Data */
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"rahulkumarshah5000@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback - Frozen OCR");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Write your feedback here...");

                        /* Send it off to the Activity-Chooser */
                        startActivity(Intent.createChooser(emailIntent, "Open gmail..."));
                        break;
                    case R.id.about_menuID:
                        String url = "https://github.com/rahulshah456";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        break;
                    case R.id.exit_menuID:
                            finish();
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }


    private void ShowDeleteDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Delete History?");
        alertDialog.setMessage("Are you sure  you want to delete all your recognition data...");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (sqLiteHandler.getAllRecognitions().size()>0){
                    sqLiteHandler.deleteRecognitionData();
                    Toast.makeText(MainActivity.this, "History Cleaned!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Nothing to Delete!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: Cancelled Delete!");
            }
        });
        alertDialog.show();

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
