package com.myapplication.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Initialize extends AppCompatActivity {

    private Button backButton;
    private Button resultsButton;
    protected static TextView textInfo;
    private static Context context;
    protected static AlertDialog dialog;
    private List<String> selectedItems;
    protected static ArrayDeque<String> queue;
    protected static String element;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private ImageView imageSelected;

    String imageString = "";

    public static final String SHARED_PREFS = "sharedPrefs";

    public static final String NUMBER_OF_PHOTOS = "numberOfUploadedPhotos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialize_xml);
        context = this;

        textInfo = (TextView) findViewById(R.id.textInfo);
        int number_of_photos = loadNumberOfPhotosData();
        String info = "Загружено " + number_of_photos + " фото";
        textInfo.setText(info);

        backButton = (Button) findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMain();
            }
        });

        imageSelected = findViewById(R.id.selectedImage);

        findViewById(R.id.buttonSelectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Initialize.this,
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

    public static void saveNumberOfPhotosData(int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(NUMBER_OF_PHOTOS, n);
        editor.apply();
    }

    public static int loadNumberOfPhotosData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(NUMBER_OF_PHOTOS, 0);
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

                        findViewById(R.id.buttonCalculateImage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imageString = getStringImage(bitmap);
                                queue = new ArrayDeque<>();
                                queue.add("EyeBags");
                                queue.add("BlueLips");
                                queue.add("RedEyes");
                                queue.add("AsymmetricEyes");
                                queue.add("AsymmetricEyebrows");
                                queue.add("AsymmetricLips");
                                queue.add("Smile");
                                queue.add("DryLips");
                                queue.add("SkinColor");
                                queue.add("EyebrowsAlopecia");
                                queue.add("Redness");
                                selectedItems = new ArrayList<>();
                                selectedItems.add(imageString);
                                selectedItems.add("EyeBags");
                                selectedItems.add("BlueLips");
                                selectedItems.add("RedEyes");
                                selectedItems.add("AsymmetricFace");
                                selectedItems.add("Smile");
                                selectedItems.add("DryLips");
                                selectedItems.add("SkinColor");
                                selectedItems.add("EyebrowsAlopecia");
                                selectedItems.add("Redness");

                                new AsyncTaskCalc(Initialize.this, 9).execute(selectedItems);
                            }
                        });

                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    resultsButton = (Button)findViewById(R.id.buttonResults);
                    resultsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Detection.detection(queue);
                            selection();
                            int number_of_photos = loadNumberOfPhotosData();
                            saveNumberOfPhotosData(number_of_photos + 1);
                            String info = "Загружено " + (number_of_photos + 1) + " фото";
                            textInfo.setText(info);
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

    public static void selection(){
        element = queue.remove();
        switch (element){
            case "EyeBags":
                if (!Detection.red_okay || !Detection.green_okay || !Detection.blue_okay){
                    createContactDialog("Виявлено мішки під очима. Чи помічали Ви їх більшими, ніж зазвичай?", "EyeBags");
                }
                else
                    selection();
                break;
            case "BlueLips":
                if (!Detection.blue_lips_okay){
                    createContactDialog("Виявлено синюваті губи. Чи помічали Ви їх більш синюватими, ніж зазвичай?", "BlueLips");
                }
                else
                    selection();
                break;
            case "RedEyes":
                if (!Detection.red_eyes_okay){
                    createContactDialog("Виявлено червонуваті очі. Чи помічали Ви їх більш червоними, ніж зазвичай?", "RedEyes");
                }
                else
                    selection();
                break;
            case "AsymmetricEyes":
                if (!Detection.asymmetric_eyes_okay){
                    createContactDialog("Виявлено асиметрію очей. Чи помічали Ви їх більш асиметричними, ніж зазвичай?", "AsymmetricEyes");
                }
                else
                    selection();
                break;
            case "AsymmetricEyebrows":
                if (!Detection.asymmetric_eyebrows_okay){
                    createContactDialog("Виявлено асиметрію брів. Чи помічали Ви їх більш асиметричними, ніж зазвичай?", "AsymmetricEyebrows");
                }
                else
                    selection();
                break;
            case "AsymmetricLips":
                if (!Detection.asymmetric_lips_okay){
                    createContactDialog("Виявлено асиметрію губ. Чи помічали Ви їх більш асиметричними, ніж зазвичай?", "AsymmetricLips");
                }
                else
                    selection();
                break;
            case "Smile":
                if (!Detection.smile_okay){
                    createContactDialog("Посміхайтеся частіше!", "Smile");
                }
                else
                    selection();
                break;
            case "DryLips":
                if (!Detection.dry_lips_okay){
                    createContactDialog("Виявлено сухість та лущення губ. Чи помічали Ви їх більш сухими, ніж зазвичай?", "DryLips");
                }
                else
                    selection();
                break;
            case "SkinColor":
                if (!Detection.skin_color_okay){
                    createContactDialog("Виявлено зміну кольору шкіри порівняно з попередніми фото", "SkinColor");
                }
                else
                    selection();
                break;
            case "EyebrowsAlopecia":
                if (!Detection.eyebrows_alopecia_okay){
                    createContactDialog("Виявлено випадіння брів порівняно з попередніми фото", "EyebrowsAlopecia");
                }
                else
                    selection();
                break;
            case "Redness":
                if (!Detection.redness_okay){
                    createContactDialog("Виявлено почервовіння обличчя порівняно з попередніми фото", "Redness");
                }
                break;
        }

    }

    public static void createContactDialog(String text, final String option){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View popupView = inflater.inflate(R.layout.popup, null);

        Button yesButton = popupView.findViewById(R.id.yesButton);
        Button noButton = popupView.findViewById(R.id.noButton);
        Button okButton = popupView.findViewById(R.id.okButton);
        TextView textView = popupView.findViewById(R.id.textQuestion);
        textView.setText(text);

        if (option.equals("Smile") || option.equals("SkinColor") || option.equals("EyebrowsAlopecia") || option.equals("Redness")){
            okButton.setVisibility(View.VISIBLE);
            yesButton.setVisibility(View.GONE);
            noButton.setVisibility(View.GONE);
        }

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selection();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                notNoticed();
                selection();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!option.equals("Redness"))
                    selection();
            }
        });
    }

    public static void notNoticed(){
        switch (element){
            case "EyeBags":
                Modifications.eyeBagsNotNoticed();
                break;
            case "BlueLips":
                Modifications.blueLipsNotNoticed();
                break;
            case "RedEyes":
                Modifications.redEyesNotNoticed();
                break;
            case "AsymmetricEyes":
                Modifications.asymmetricEyesNotNoticed();
                break;
            case "AsymmetricEyebrows":
                Modifications.asymmetricEyebrowsNotNoticed();
                break;
            case "AsymmetricLips":
                Modifications.asymmetricLipsNotNoticed();
                break;
            case "DryLips":
                Modifications.dryLipsNotNoticed();
                break;
        }
    }

}