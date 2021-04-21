package com.myapplication.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private TextView tv;

    private ImageView imageSelected;

    String imageString = "";

    public static final String SHARED_PREFS = "sharedPrefs";

    public static final String EYE_BAGS_R = "eyeBagsR";
    public static final String EYE_BAGS_G = "eyeBagsG";
    public static final String EYE_BAGS_B = "eyeBagsB";
    public static final String EYE_BAGS_R_N = "eyeBagsRN";
    public static final String EYE_BAGS_G_N = "eyeBagsGN";
    public static final String EYE_BAGS_B_N = "eyeBagsBN";

    public static final String BLUE_LIPS = "blueLips";
    public static final String BLUE_LIPS_N = "blueLipsN";

    public static final String RED_EYES = "redEyes";
    public static final String RED_EYES_N = "redEyesN";

    public static final String ASYMMETRIC_FACE_EYES = "asymmetric_face_eyes";
    public static final String ASYMMETRIC_FACE_EYEBROWS = "asymmetric_face_eyebrows";
    public static final String ASYMMETRIC_FACE_LIPS = "asymmetric_face_lips";
    public static final String ASYMMETRIC_FACE_EYES_N = "asymmetric_face_eyes_n";
    public static final String ASYMMETRIC_FACE_EYEBROWS_N = "asymmetric_face_eyebrows_n";
    public static final String ASYMMETRIC_FACE_LIPS_N = "asymmetric_face_lips_n";

    public static final String SMILE = "smile";

    public static final String DRY_LIPS = "dryLips";
    public static final String DRY_LIPS_N = "dryLipsN";

    public static final String SKIN_COLOR_MEAN = "skin_color_mean";
    public static final String SKIN_COLOR_LAST = "skin_color_last";
    public static final String SKIN_COLOR_N = "skin_color_n";

    public static final String EYEBROWS_ALOPECIA_MEAN = "eyebrows_alopecia_mean";
    public static final String EYEBROWS_ALOPECIA_LAST = "eyebrows_alopecia_last";
    public static final String EYEBROWS_ALOPECIA_N = "eyebrows_alopecia_n";

    public static final String REDNESS_MEAN = "redness_mean";
    public static final String REDNESS_LAST = "redness_last";
    public static final String REDNESS_N = "redness_n";

    private float eye_bags_r;
    private float eye_bags_g;
    private float eye_bags_b;
    private int eye_bags_r_n;
    private int eye_bags_g_n;
    private int eye_bags_b_n;

    private float blue_lips;
    private int blue_lips_n;

    private float red_eyes;
    private int red_eyes_n;

    private float asymmetric_face_eyes;
    private float asymmetric_face_eyebrows;
    private float asymmetric_face_lips;
    private int asymmetric_face_eyes_n;
    private int asymmetric_face_eyebrows_n;
    private int asymmetric_face_lips_n;

    private float smile;

    private float dry_lips;
    private int dry_lips_n;

    private float skin_color_mean;
    private float skin_color_last;
    private int skin_color_n;

    private float eyebrows_alopecia_mean;
    private float eyebrows_alopecia_last;
    private int eyebrows_alopecia_n;

    private float redness_mean;
    private float redness_last;
    private int redness_n;

    private AlertDialog dialog;

    private static final int skin_color = 6;
    private static final int eyebrows_alopecia = 150;
    private static final int redness = 15000;

    private List<PyObject> eyeBagsList;
    private PyObject blueLipsObject;
    private List<PyObject> redEyesList;
    private List<PyObject> asymmetricFaceList;
    private PyObject smileObject;
    private PyObject dryLipsObject;
    private PyObject skinColorObject;
    private PyObject eyebrowsAlopeciaObject;
    private PyObject rednessObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSelected = findViewById(R.id.selectedImage);
        tv = findViewById(R.id.resultText);

        findViewById(R.id.buttonSelectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }else{
                    selectImage();
                }
            }
        });
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
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
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

                                Python py = Python.getInstance();
                                final PyObject pyobj = py.getModule("script");

                                eyeBagsList = pyobj.callAttr("eye_bags_detection", imageString).asList();
                                blueLipsObject = pyobj.callAttr("blue_lips_detection", imageString);
                                redEyesList = pyobj.callAttr("red_eyes_detection", imageString).asList();
                                asymmetricFaceList = pyobj.callAttr("asymmetric_face_detection", imageString).asList();
                                smileObject = pyobj.callAttr("depression_detection", imageString);
                                dryLipsObject = pyobj.callAttr("dry_lips_detection", imageString);
                                skinColorObject = pyobj.callAttr("skin_color_detection", imageString);
                                eyebrowsAlopeciaObject = pyobj.callAttr("eyebrows_alopecia_detection", imageString);
                                rednessObject = pyobj.callAttr("redness_detection", imageString);
                                eyeBagsDetection();
                            }
                        });

                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

    public void saveEyeBagsData(float r, float g, float b, int n_r, int n_g, int n_b){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(EYE_BAGS_R, r);
        editor.putFloat(EYE_BAGS_G, g);
        editor.putFloat(EYE_BAGS_B, b);
        editor.putInt(EYE_BAGS_R_N, n_r);
        editor.putInt(EYE_BAGS_G_N, n_g);
        editor.putInt(EYE_BAGS_B_N, n_b);

        editor.apply();
    }

    public void loadEyeBagsData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        eye_bags_r = sharedPreferences.getFloat(EYE_BAGS_R, 0.1277055564508918f);
        eye_bags_g = sharedPreferences.getFloat(EYE_BAGS_G, 0.1400120476555604326f);
        eye_bags_b = sharedPreferences.getFloat(EYE_BAGS_B, 0.15516625103437830024f);
        eye_bags_r_n = sharedPreferences.getInt(EYE_BAGS_R_N, 1);
        eye_bags_g_n = sharedPreferences.getInt(EYE_BAGS_G_N, 1);
        eye_bags_b_n = sharedPreferences.getInt(EYE_BAGS_B_N, 1);
    }

    public void saveBlueLipsData(float a, int n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(BLUE_LIPS, a);
        editor.putInt(BLUE_LIPS_N, n);

        editor.apply();
    }

    public void loadBlueLipsData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        blue_lips = sharedPreferences.getFloat(BLUE_LIPS, 14.04255591482153395f);
        blue_lips_n = sharedPreferences.getInt(BLUE_LIPS_N, 1);
    }

    public void saveRedEyesData(float a, int n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(RED_EYES, a);
        editor.putInt(RED_EYES_N, n);

        editor.apply();
    }

    public void loadRedEyesData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        red_eyes = sharedPreferences.getFloat(RED_EYES, 6.3575525776411664f);
        red_eyes_n = sharedPreferences.getInt(RED_EYES_N, 1);
    }

    public void saveAsymmetricFaceData(float eyes, float eyebrows, float lips, int eyes_n, int eyebrows_n, int lips_n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(ASYMMETRIC_FACE_EYES, eyes);
        editor.putFloat(ASYMMETRIC_FACE_EYEBROWS, eyebrows);
        editor.putFloat(ASYMMETRIC_FACE_LIPS, lips);
        editor.putInt(ASYMMETRIC_FACE_EYES_N, eyes_n);
        editor.putInt(ASYMMETRIC_FACE_EYEBROWS_N, eyebrows_n);
        editor.putInt(ASYMMETRIC_FACE_LIPS_N, lips_n);

        editor.apply();
    }

    public void loadAsymmetricFaceData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        asymmetric_face_eyes = sharedPreferences.getFloat(ASYMMETRIC_FACE_EYES, 1.9773613733019304764f);
        asymmetric_face_eyebrows = sharedPreferences.getFloat(ASYMMETRIC_FACE_EYEBROWS, 6.0218941230021674f);
        asymmetric_face_lips = sharedPreferences.getFloat(ASYMMETRIC_FACE_LIPS, 5.888726197575719988f);
        asymmetric_face_eyes_n = sharedPreferences.getInt(ASYMMETRIC_FACE_EYES_N, 1);
        asymmetric_face_eyebrows_n = sharedPreferences.getInt(ASYMMETRIC_FACE_EYEBROWS_N, 1);
        asymmetric_face_lips_n = sharedPreferences.getInt(ASYMMETRIC_FACE_LIPS_N, 1);
    }

    public void saveSmileData(float smile){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(SMILE, smile);
        editor.apply();
    }

    public void loadSmileData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        smile = sharedPreferences.getFloat(SMILE, 5.225230623883975f);
    }

    public void saveDryLipsData(float edges, int n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(DRY_LIPS, edges);
        editor.putInt(DRY_LIPS_N, n);
        editor.apply();
    }

    public void loadDryLipsData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        dry_lips = sharedPreferences.getFloat(DRY_LIPS, 531.04084358109003f);
        dry_lips_n = sharedPreferences.getInt(DRY_LIPS_N, 1);
    }

    public void saveSkinColorData(float mean, float last, int n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(SKIN_COLOR_MEAN, mean);
        editor.putFloat(SKIN_COLOR_LAST, last);
        editor.putInt(SKIN_COLOR_N, n);
        editor.apply();
    }

    public void loadSkinColorData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        skin_color_mean = sharedPreferences.getFloat(SKIN_COLOR_MEAN, 0);
        skin_color_last = sharedPreferences.getFloat(SKIN_COLOR_LAST, 0);
        skin_color_n = sharedPreferences.getInt(SKIN_COLOR_N, 0);
    }

    public void saveEyeBrowsAlopeciaData(float mean, float last, int n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(EYEBROWS_ALOPECIA_MEAN, mean);
        editor.putFloat(EYEBROWS_ALOPECIA_LAST, last);
        editor.putInt(EYEBROWS_ALOPECIA_N, n);
        editor.apply();
    }

    public void loadEyeBrowsAlopeciaData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        eyebrows_alopecia_mean = sharedPreferences.getFloat(EYEBROWS_ALOPECIA_MEAN, 0);
        eyebrows_alopecia_last = sharedPreferences.getFloat(EYEBROWS_ALOPECIA_LAST, 0);
        eyebrows_alopecia_n = sharedPreferences.getInt(EYEBROWS_ALOPECIA_N, 0);
    }

    public void saveRednessData(float mean, float last, int n){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(REDNESS_MEAN, mean);
        editor.putFloat(REDNESS_LAST, last);
        editor.putInt(REDNESS_N, n);
        editor.apply();
    }

    public void loadRednessData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        redness_mean = sharedPreferences.getFloat(REDNESS_MEAN, 0);
        redness_last = sharedPreferences.getFloat(REDNESS_LAST, 0);
        redness_n = sharedPreferences.getInt(REDNESS_N, 0);
    }

    public void eyeBagsDetection(){
        float result_r = eyeBagsList.get(0).toFloat();
        float result_g = eyeBagsList.get(1).toFloat();
        float result_b = eyeBagsList.get(2).toFloat();

        loadEyeBagsData();

        boolean red_okay = true;
        boolean green_okay = true;
        boolean blue_okay = true;

        if (result_r > eye_bags_r){
            red_okay = false;
        }
        if (result_g > eye_bags_g){
            green_okay = false;
        }
        if (result_b > eye_bags_g){
            blue_okay = false;
        }

        if (!red_okay || !green_okay || !blue_okay){
            createContactDialog("Eye bags detected. Have you noticed them being bigger than usual", "EyeBags");
        }

    }

    public void eyeBagsNotNoticed(){
        float result_r = eyeBagsList.get(0).toFloat();
        float result_g = eyeBagsList.get(1).toFloat();
        float result_b = eyeBagsList.get(2).toFloat();

        loadEyeBagsData();

        boolean red_okay = true;
        boolean green_okay = true;
        boolean blue_okay = true;

        if (result_r > eye_bags_r){
            red_okay = false;
        }
        if (result_g > eye_bags_g){
            green_okay = false;
        }
        if (result_b > eye_bags_g){
            blue_okay = false;
        }

        float new_red = eye_bags_r;
        float new_green = eye_bags_g;
        float new_blue = eye_bags_b;
        int new_r_n = eye_bags_r_n;
        int new_g_n = eye_bags_g_n;
        int new_b_n = eye_bags_b_n;
        if (!red_okay){
            new_red = (new_red * eye_bags_r_n + result_r) / (eye_bags_r_n + 1);
            new_r_n += 1;
        }
        if (!green_okay){
            new_green = (new_green * eye_bags_g_n + result_g) / (eye_bags_g_n + 1);
            new_g_n += 1;
        }
        if (!blue_okay){
            new_blue = (new_blue * eye_bags_b_n + result_b) / (eye_bags_b_n + 1);
            new_b_n += 1;
        }

        saveEyeBagsData(new_red, new_green, new_blue, new_r_n, new_g_n, new_b_n);
    }

    public void blueLipsDetection(){
        float result = blueLipsObject.toFloat();

        loadBlueLipsData();
        if (result < blue_lips){
            createContactDialog("Blue lips detected have you noticed them being blueish?", "BlueLips");
        }
    }

    public void blueLipsNotNoticed(){
        float result = blueLipsObject.toFloat();
        loadBlueLipsData();
        float new_blue_lips = (blue_lips * blue_lips_n + result) / (blue_lips_n + 1);
        saveBlueLipsData(new_blue_lips, blue_lips_n + 1);
    }

    public void redEyesDetection(){
        float result_left = redEyesList.get(0).toFloat();
        float result_right = redEyesList.get(1).toFloat();

        loadRedEyesData();

        boolean left_okay = true;
        boolean right_okay = true;

        if (result_left > red_eyes){
            left_okay = false;
        }
        if (result_right > red_eyes){
            right_okay = false;
        }

        if (!left_okay || !right_okay){
            createContactDialog("Red eyes detected. Have you noticed them being more red than usual?", "RedEyes");
        }
    }

    public void redEyesNotNoticed(){
        float result_left = redEyesList.get(0).toFloat();
        float result_right = redEyesList.get(1).toFloat();

        loadRedEyesData();

        boolean left_okay = true;

        if (result_left > red_eyes){
            left_okay = false;
        }
        float new_red;
        if (!left_okay){
            new_red = (red_eyes * red_eyes_n + result_left) / (red_eyes_n + 1);
        }
        else{
            new_red = (red_eyes * red_eyes_n + result_right) / (red_eyes_n + 1);
        }

        saveRedEyesData(new_red, red_eyes_n + 1);
    }

    public void asymmetricEyesDetection(){
        float result_eyes = asymmetricFaceList.get(0).toFloat();

        loadAsymmetricFaceData();

        boolean eyes_okay = true;

        if (result_eyes > asymmetric_face_eyes){
            eyes_okay = false;
        }

        if (!eyes_okay){
            createContactDialog("Asymmetric eyes detected have you noticed them being more asymmetrical than usual?", "AsymmetricEyes");
        }
    }

    public void asymmetricEyebrowsDetection(){
        float result_eyebrows = asymmetricFaceList.get(1).toFloat();
        loadAsymmetricFaceData();

        boolean eyebrows_okay = true;

        if (result_eyebrows > asymmetric_face_eyebrows){
            eyebrows_okay = false;
        }

        if (!eyebrows_okay){
            createContactDialog("Asymmetric eyebrows detected have you noticed them being more asymmetrical than usual?", "AsymmetricEyebrows");
        }
    }

    public void asymmetricLipsDetection(){
        float result_mouth = asymmetricFaceList.get(2).toFloat();
        loadAsymmetricFaceData();

        boolean mouth_okay = true;

        if (result_mouth > asymmetric_face_lips){
            mouth_okay = false;
        }

        if (!mouth_okay){
            createContactDialog("Asymmetric mouth detected have you noticed it being more asymmetrical than usual?", "AsymmetricLips");
        }
    }

    public void asymmetricEyesNotNoticed(){
        float result_eyes = asymmetricFaceList.get(0).toFloat();
        loadAsymmetricFaceData();
        float new_eyes = (asymmetric_face_eyes * asymmetric_face_eyes_n + result_eyes) / (asymmetric_face_eyes_n + 1);
        saveAsymmetricFaceData(new_eyes, asymmetric_face_eyebrows, asymmetric_face_lips, asymmetric_face_eyes_n + 1,asymmetric_face_eyebrows_n, asymmetric_face_lips_n);
    }

    public void asymmetricEyebrowsNotNoticed(){
        float result_eyebrows = asymmetricFaceList.get(1).toFloat();
        loadAsymmetricFaceData();
        float new_eyebrows = (asymmetric_face_eyebrows * asymmetric_face_eyebrows_n + result_eyebrows) / (asymmetric_face_eyebrows_n + 1);
        saveAsymmetricFaceData(asymmetric_face_eyes, new_eyebrows, asymmetric_face_lips, asymmetric_face_eyes_n ,asymmetric_face_eyebrows_n + 1, asymmetric_face_lips_n);
    }

    public void asymmetricLipsNotNoticed(){
        float result_lips = asymmetricFaceList.get(2).toFloat();
        loadAsymmetricFaceData();
        float new_lips = (asymmetric_face_lips * asymmetric_face_lips_n + result_lips) / (asymmetric_face_lips_n + 1);
        saveAsymmetricFaceData(asymmetric_face_eyes, asymmetric_face_eyebrows, new_lips, asymmetric_face_eyes_n ,asymmetric_face_eyebrows_n, asymmetric_face_lips_n + 1);
    }

    public void smileDetection(){
        float result = smileObject.toFloat();
        loadSmileData();
        if (result < smile){
            createContactDialog("Smile more often!", "Smile");
        }
    }

    public void dryLipsDetection(){
        float result = dryLipsObject.toFloat();

        loadDryLipsData();
        if (result > dry_lips){
            createContactDialog("Dry lips detected. Have you noticed them being more dry than usual?", "DryLips");
        }
    }

    public void dryLipsNotNoticed(){
        float result = dryLipsObject.toFloat();
        float new_lips = (dry_lips * dry_lips_n + result) / (dry_lips_n + 1);
        saveDryLipsData(new_lips, dry_lips_n + 1);
    }

    public void skinColorDetection(){
        float result = skinColorObject.toFloat();
        loadSkinColorData();

        if (skin_color_n > 0){
            if (result - skin_color_mean > skin_color || result - skin_color_last > skin_color){
                createContactDialog("Skin color changed!", "SkinColor");
            }
            else{
                float new_mean = (skin_color_mean * skin_color_n + result) / (skin_color_n + 1);
                saveSkinColorData(new_mean, result, skin_color_n + 1);
            }
        }else{
            saveSkinColorData(result, result, skin_color_n + 1);
        }
    }

    public void eyebrowsAlopeciaDetection(){
        float result = eyebrowsAlopeciaObject.toFloat();

        loadEyeBrowsAlopeciaData();
        if (eyebrows_alopecia_n > 0){
            if (eyebrows_alopecia_mean - result > eyebrows_alopecia || eyebrows_alopecia_last - result > eyebrows_alopecia){
                createContactDialog("Eyebrows alopecia detected!", "EyebrowsAlopecia");
            }
            else{
                float new_eyebrows_alopecia = (eyebrows_alopecia_mean * eyebrows_alopecia_n + result) / (eyebrows_alopecia_n + 1);
                saveEyeBrowsAlopeciaData(new_eyebrows_alopecia, result, eyebrows_alopecia_n + 1);
            }
        } else{
            saveEyeBrowsAlopeciaData(result, result, eyebrows_alopecia_n + 1);
        }
    }

    public void rednessDetection(){
        float result = rednessObject.toFloat();

        loadRednessData();
        if (redness_n > 0) {
            if (result - redness_mean > redness || result - redness_last > redness) {
                createContactDialog("Redness detected!", "Redness");
            }
            else{
                float new_redness = (redness_mean * redness_n + result) / (redness_n + 1);
                saveRednessData(new_redness, result, redness_n + 1);
            }
        } else {
            saveRednessData(result, result, redness_n + 1);
        }
    }

    public void createContactDialog(String text, final String option){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup, null);

        Button yesButton = popupView.findViewById(R.id.yesButton);
        Button noButton = popupView.findViewById(R.id.noButton);
        TextView textView = popupView.findViewById(R.id.textQuestion);
        textView.setText(text);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                postSelection(option, true);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                postSelection(option, false);
            }
        });
    }

    public void postSelection(String option, boolean noticed){
        switch (option){
            case "EyeBags":
                if (!noticed)
                    eyeBagsNotNoticed();
                blueLipsDetection();
                break;
            case "BlueLips":
                if (!noticed)
                    blueLipsNotNoticed();
                redEyesDetection();
                break;
            case "RedEyes":
                if (!noticed)
                    redEyesNotNoticed();
                asymmetricEyesDetection();
                break;
            case "AsymmetricEyes":
                if (!noticed)
                    asymmetricEyesNotNoticed();
                asymmetricEyebrowsDetection();
                break;
            case "AsymmetricEyebrows":
                if (!noticed)
                    asymmetricEyebrowsNotNoticed();
                asymmetricLipsDetection();
                break;
            case "AsymmetricLips":
                if (!noticed)
                    asymmetricLipsNotNoticed();
                smileDetection();
                break;
            case "Smile":
                dryLipsDetection();
                break;
            case "DryLips":
                if (!noticed)
                    dryLipsNotNoticed();
                skinColorDetection();
                break;
            case "SkinColor":
                eyebrowsAlopeciaDetection();
                break;
            case "EyebrowsAlopecia":
                rednessDetection();
                break;
        }
    }

}