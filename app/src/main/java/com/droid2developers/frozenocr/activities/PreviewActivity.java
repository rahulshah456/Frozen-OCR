package com.droid2developers.frozenocr.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.droid2developers.frozenocr.R;
import com.droid2developers.frozenocr.model.OCRModel;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private OCRModel ocrModel;
    private ImageView imageView,optionsImage;
    private ProgressDialog mProgressDialog;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Bundle extras = getIntent().getExtras();
        mProgressDialog = new ProgressDialog(this);
        imageView = findViewById(R.id.imageViewId);
        editText = findViewById(R.id.exitTextId);
        optionsImage = findViewById(R.id.optionsImageId);
        CardView backButton = findViewById(R.id.backButtonId);
        CardView optionsButton = findViewById(R.id.optionsButtonId);

        if (extras !=null){
            ocrModel = new OCRModel(extras.getLong("timeStamp"),extras.getString("savedURL"),
                    extras.getString("extraText"));
            saveToDatabase(ocrModel);
        }

        init();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPopupMenu();

            }
        });

    }


    private void init(){
        Glide.with(this)
                .load(ocrModel.getImageUri())
                .into(imageView);
        editText.setText(ocrModel.getExtaText());
        editText.setEnabled(false);
    }


    @SuppressLint("StaticFieldLeak")
    private class ExportTextData extends AsyncTask<OCRModel,Integer, Uri>{


        @Override
        protected Uri doInBackground(OCRModel... ocrModels) {

            File imagesFolder = new File(getApplicationInfo().dataDir, "files");
            File pdfFile = new File(imagesFolder, System.currentTimeMillis() + "_document.pdf");
            // Destination Folder and File name
            //String FileUri = Environment.DIRECTORY_DOCUMENTS + System.currentTimeMillis() + ".pdf";

            // Create New Blank Document
            Document document = new Document(PageSize.A4);

            // Create Pdf Writer for Writing into New Created Document
            try {
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }

            // Open Document for Writing into document
            document.open();
            document.newPage();

            // User Define Method
            addMetaData(document);
            addTitlePage(document,ocrModels[0].getExtaText());
            // Close Document after writing all content
            document.close();

            return FileProvider.getUriForFile(PreviewActivity.this, getPackageName()+ ".provider", pdfFile);
        }


        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            CloseDialog();
            Log.d(TAG, "onPostExecute: " + uri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

        }
    }


    // Set PDF document Properties
    public void addMetaData(Document document) {
        document.addTitle("Text Recognition");
        document.addSubject("Text Info");
        document.addKeywords("Personal,Education, Skills");
                document.addAuthor(getResources().getString(R.string.app_name));
        document.addCreator(getResources().getString(R.string.app_name));
    }


    public void addTitlePage(Document document, String extraText) {

        Log.d(TAG, "addTitlePage: pdfText" + extraText);
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph header = new Paragraph();
        // Set Font in this Paragraph
        header.setFont(titleFont);
        // Add item into Paragraph
        header.add("Recognized Text – From Frozen OCR\n");

        // Now Start another New Paragraph
        Paragraph textInformation = new Paragraph();
        textInformation.setFont(smallBold);
        textInformation.add(extraText);

        textInformation.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(header);
            document.add(textInformation);
            // Create new Page in PDF
            document.newPage();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }


    private void openPopupMenu(){


        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(PreviewActivity.this, optionsImage);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.preview_menu,popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.export_menuID:
                        CreateDialog("Processing Export!");
                        new ExportTextData().execute(ocrModel);
                        break;
                    case R.id.edit_menuID:
                        editText.setEnabled(true);
                        break;
                    case R.id.share_menuID:
                        shareTextIntent(ocrModel.getExtaText());
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }


    private void shareTextIntent(String extraText){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, extraText);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

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


    //TODO
    public void saveToDatabase(OCRModel saveModel){



    }



}
