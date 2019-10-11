package com.droid2developers.frozenocr.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.droid2developers.frozenocr.R;
import com.droid2developers.frozenocr.controller.HistoryAdapter;
import com.droid2developers.frozenocr.controller.SQLiteHandler;
import com.droid2developers.frozenocr.model.OCRModel;

import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    public static final String TAG = HistoryActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private SQLiteHandler sqLiteHandler;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        CardView backButton = findViewById(R.id.backButtonId);
        sqLiteHandler = new SQLiteHandler(this);
        recyclerView = findViewById(R.id.recyclerViewId);
        RelativeLayout relativeLayout = findViewById(R.id.relativeId);

        List<OCRModel> list = sqLiteHandler.getAllRecognitions();
        if (list.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            GenerateRecyclerView(list);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void GenerateRecyclerView(List<OCRModel> ocrModelList){

        // Set up RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL,false);
        historyAdapter = new HistoryAdapter(this,ocrModelList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

                OCRModel ocrModel = historyAdapter.getItemAt(position);
                Intent intent = new Intent(HistoryActivity.this, PreviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("timeStamp",ocrModel.getTimeStamp());
                intent.putExtra("savedURL",ocrModel.getImageUri());
                intent.putExtra("extraText",ocrModel.getExtaText());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(HistoryActivity.this, Objects.requireNonNull(recyclerView
                                        .findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.imageViewId)
                                , Objects.requireNonNull(ViewCompat.getTransitionName(Objects.requireNonNull(recyclerView
                                        .findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.imageViewId))));
                startActivity(intent,options.toBundle());

            }

            @Override
            public void OnItemOptionsClick(int position) {
                openPopupMenu(position);
            }
        });

    }


    private void openPopupMenu(final int position){


        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(HistoryActivity.this, Objects.requireNonNull(recyclerView
                .findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.optionsButtonId));
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.delete_menu,popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.delete_menuID) {
                    sqLiteHandler.deleteRecognition(historyAdapter.getItemAt(position));
                    historyAdapter.removeItem(position);
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }
}
