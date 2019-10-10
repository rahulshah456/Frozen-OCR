package com.droid2developers.frozenocr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.droid2developers.frozenocr.activities.CameraActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 5;
    private ImageView optionsImage;

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



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST){

            if (data == null) {
                //Toast.makeText(UserDeviceActivity.this,"Failed to load picture!",Toast.LENGTH_LONG).show();
                Log.d(TAG,"Failed to load picture!");
                return;
            }

            if (data.getData()!=null){
                //Toast.makeText(getApplicationContext(),String.valueOf(data.getData()),Toast.LENGTH_SHORT).show();
                String imageUri;
                imageUri = String.valueOf(data.getData());

            }else {
                Log.d(TAG, "onActivityResult: Select only one image");
            }



        }

    }



    public void OpenOptionsMenu(){



    }
}
