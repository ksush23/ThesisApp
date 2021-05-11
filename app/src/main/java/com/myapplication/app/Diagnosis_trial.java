package com.myapplication.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Diagnosis_trial extends AppCompatActivity {

    private Button backButton;
    private ImageView imageSelected;
    private Button calculateButton;
    protected ArrayDeque<String> queue;
    private Button resultsButton;
    private AlertDialog dialog;

    private ListView listView;
    private List<String> selectedItems;
    private String[] items = {"EyeBags", "BlueLips", "RedEyes", "AsymmetricFace", "Smile", "DryLips"};
    private ArrayAdapter<String> adapter;
    private String[] list = {"Мішки під очима", "Синюваті губи", "Червоні очі", "Асиметрія та набряки обличчя", "Опущені кутики губ", "Сухості та лущення губ"};
    private Boolean[] selected = {true, true, true, true, true, true};

    String imageString = "";

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_trial);

        backButton = (Button) findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMain();
            }
        });

        imageSelected = findViewById(R.id.selectedImage);
        calculateButton = (Button)findViewById(R.id.buttonCalculateImage);

        findViewById(R.id.buttonSelectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Diagnosis_trial.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }else{
                    selectImage();
                }
            }
        });
    }

    public void openActivityMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Відмова у доступі", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                final Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageSelected.setImageBitmap(bitmap);

                        if(!Python.isStarted())
                            Python.start(new AndroidPlatform(this));

                        calculateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                        // Set up the alert builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(Diagnosis_trial.this);
                        builder.setTitle("Оберіть категорії діагностики");

                        // Add a checkbox list
                        boolean[] checkedItems = {true, true, true, true, true, true};
                        selectedItems = new ArrayList<>();
                        builder.setMultiChoiceItems(list, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                selected[which] = isChecked;
                            }
                        });

                        // Add OK and Cancel buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                queue = new ArrayDeque<>();
                                imageString = getStringImage(bitmap);
                                selectedItems.add(imageString);
                                for (int i = 0; i < items.length; i++){
                                    if (selected[i]){
                                        selectedItems.add(items[i]);
                                        queue.add(items[i]);
                                    }
                                }

                                new AsyncTaskCalc(Diagnosis_trial.this, queue.size()).execute(selectedItems);
                            }
                        });
                        builder.setNegativeButton("Відмінити", null);

                        // Create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                            }
                        });

                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    resultsButton = (Button)findViewById(R.id.buttonResults);
                    resultsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<Float> eyeBagsList = new ArrayList<>();
                            eyeBagsList.add(0.1277055564508918f);
                            eyeBagsList.add(0.1400120476555604326f);
                            eyeBagsList.add(0.15516625103437830024f);

                            List<Float> blueLipsList = new ArrayList<>();
                            blueLipsList.add(14.04255591482153395f);

                            List<Float> redEyesList = new ArrayList<>();
                            redEyesList.add(6.3575525776411664f);

                            List<Float> asymmetricFaceList = new ArrayList<>();
                            asymmetricFaceList.add(1.9773613733019304764f);
                            asymmetricFaceList.add(6.0218941230021674f);
                            asymmetricFaceList.add(5.888726197575719988f);

                            List<Float> dryLipsList = new ArrayList<>();
                            dryLipsList.add(531.04084358109003f);

                            Detection.trial_detection(queue, eyeBagsList, blueLipsList, redEyesList, asymmetricFaceList, dryLipsList);
                            detection();
                        }
                    });
                }
            }
        }
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    private void detection(){
        boolean eyeBagsDetected = false;
        boolean blueLipsDetected = false;
        boolean redEyesDetected = false;
        boolean asymmetricFaceDetected = false;
        boolean smileDetected = false;
        boolean dryLipsDetected = false;

        do {
            String element = queue.remove();
            switch (element) {
                case "EyeBags":
                    if (!Detection.red_okay || !Detection.green_okay || !Detection.blue_okay) {
                        eyeBagsDetected = true;
                    }
                    break;
                case "BlueLips":
                    if (!Detection.blue_lips_okay) {
                        blueLipsDetected = true;
                    }
                    break;
                case "RedEyes":
                    if (!Detection.red_eyes_okay) {
                        redEyesDetected = true;
                    }
                    break;
                case "AsymmetricFace":
                    if (!Detection.asymmetric_eyes_okay || !Detection.asymmetric_eyebrows_okay || !Detection.asymmetric_lips_okay) {
                        asymmetricFaceDetected = true;
                    }
                    break;
                case "Smile":
                    if (!Detection.smile_okay) {
                        smileDetected = true;
                    }
                    break;
                case "DryLips":
                    if (!Detection.dry_lips_okay) {
                        dryLipsDetected = true;
                    }
                    break;
            }
        }while (!queue.isEmpty());
        createResultsDialog(eyeBagsDetected, blueLipsDetected, redEyesDetected, asymmetricFaceDetected, smileDetected, dryLipsDetected);

    }

    public void createResultsDialog(boolean eyeBagsDetected, boolean blueLipsDetected, boolean redEyesDetected, boolean asymmetricFaceDetected, boolean smileDetected, boolean dryLipsDetected){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View resultsView = getLayoutInflater().inflate(R.layout.results_window, null);

        Button okButton = resultsView.findViewById(R.id.okButton);
        ListView listView = resultsView.findViewById(R.id.listView);

        dialogBuilder.setView(resultsView);
        dialog = dialogBuilder.create();
        dialog.show();

        ArrayList<String> results = new ArrayList<>();
        if (eyeBagsDetected){
            results.add("Мішки під очима: \nХронічна алергія, гіпотериоз, сонне апное, захворювання нирок, ураження щитоподібної залози зі зниженням її функції");
        }
        if (blueLipsDetected){
            results.add("Синюваті губи: \nПереохолодження, цианоз, круп, при вагітності – дефіцит заліза, хвороба серця або легень");
        }
        if (redEyesDetected){
            results.add("Червонуваті очі: \nАлергічний кон’юктивіт, язва роговиці, синдром сухого ока, інфекційний кератит, блефарит, інтоксикація, аутоімунні захворювання, грип, ГРВІ");
        }
        if (asymmetricFaceDetected){
            results.add("Надмірна асиметрія лиця: \nПерша ознака інсульту, лицевий параліч");
        }
        if (smileDetected){
            results.add("Опущені кутики губ: \nДепресія");
        }
        if (dryLipsDetected){
            results.add("Сухі губи, що лущаться: \nЗневоднення, діабет, порушення роботи щитовидної залози, дефіцит вітаміна В12 чи заліза");
        }

        if (results.isEmpty()){
            results.add("Симптоми не були помічені!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, results);

        listView.setAdapter(adapter);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}