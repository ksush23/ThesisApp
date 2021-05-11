package com.myapplication.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button initButton;
    private Button diagnosisButton;
    private Button trialDiagnosisButton;
    private Button clearButton;
    private AlertDialog dialog;

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

    private static Context context;

    protected static final int skin_color = 6;
    protected static final int eyebrows_alopecia = 150;
    protected static final int redness = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initButton = (Button) findViewById(R.id.buttonInitialize);
        diagnosisButton = (Button) findViewById(R.id.buttonDiagnosis);
        trialDiagnosisButton = (Button) findViewById(R.id.buttonPriorDiagnosis);
        clearButton = (Button) findViewById(R.id.buttonClear);

        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityInitialize();
            }
        });
        diagnosisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityDiagnosis();
            }
        });
        trialDiagnosisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityTrialDiagnosis();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View popupView = inflater.inflate(R.layout.clean_data, null);

                Button yesButton = popupView.findViewById(R.id.yesButton);
                Button noButton = popupView.findViewById(R.id.noButton);
                TextView textView = popupView.findViewById(R.id.textQuestion);
                String text = "Ви точно хочете видалити всі дані?";
                textView.setText(text);

                dialogBuilder.setView(popupView);
                dialog = dialogBuilder.create();
                dialog.show();

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        SharedPreferences settings = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void openActivityInitialize(){
        Intent intent = new Intent(this, Initialize.class);
        startActivity(intent);
    }

    public void openActivityDiagnosis(){
        Intent intent = new Intent(this, Diagnosis.class);
        startActivity(intent);
    }


    public void openActivityTrialDiagnosis(){
        Intent intent = new Intent(this, Diagnosis_trial.class);
        startActivity(intent);
    }

    public static void saveEyeBagsData(float r, float g, float b, int n_r, int n_g, int n_b){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(EYE_BAGS_R, r);
        editor.putFloat(EYE_BAGS_G, g);
        editor.putFloat(EYE_BAGS_B, b);
        editor.putInt(EYE_BAGS_R_N, n_r);
        editor.putInt(EYE_BAGS_G_N, n_g);
        editor.putInt(EYE_BAGS_B_N, n_b);

        editor.apply();
    }

    public static List<Float> loadEyeBagsData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float eye_bags_r = sharedPreferences.getFloat(EYE_BAGS_R, 0.1277055564508918f);
        float eye_bags_g = sharedPreferences.getFloat(EYE_BAGS_G, 0.1400120476555604326f);
        float eye_bags_b = sharedPreferences.getFloat(EYE_BAGS_B, 0.15516625103437830024f);
        float eye_bags_r_n = sharedPreferences.getInt(EYE_BAGS_R_N, 1);
        float eye_bags_g_n = sharedPreferences.getInt(EYE_BAGS_G_N, 1);
        float eye_bags_b_n = sharedPreferences.getInt(EYE_BAGS_B_N, 1);
        List<Float> result = new ArrayList<>();
        result.add(eye_bags_r);
        result.add(eye_bags_g);
        result.add(eye_bags_b);
        result.add(eye_bags_r_n);
        result.add(eye_bags_g_n);
        result.add(eye_bags_b_n);
        return result;
    }

    public static void saveBlueLipsData(float a, int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(BLUE_LIPS, a);
        editor.putInt(BLUE_LIPS_N, n);

        editor.apply();
    }

    public static List<Float> loadBlueLipsData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float blue_lips = sharedPreferences.getFloat(BLUE_LIPS, 14.04255591482153395f);
        float blue_lips_n = sharedPreferences.getInt(BLUE_LIPS_N, 1);
        List<Float> result = new ArrayList<>();
        result.add(blue_lips);
        result.add(blue_lips_n);
        return result;
    }

    public static void saveRedEyesData(float a, int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(RED_EYES, a);
        editor.putInt(RED_EYES_N, n);

        editor.apply();
    }

    public static List<Float> loadRedEyesData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float red_eyes = sharedPreferences.getFloat(RED_EYES, 6.3575525776411664f);
        float red_eyes_n = sharedPreferences.getInt(RED_EYES_N, 1);
        List<Float> results = new ArrayList<>();
        results.add(red_eyes);
        results.add(red_eyes_n);
        return results;
    }

    public static void saveAsymmetricFaceData(float eyes, float eyebrows, float lips, int eyes_n, int eyebrows_n, int lips_n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(ASYMMETRIC_FACE_EYES, eyes);
        editor.putFloat(ASYMMETRIC_FACE_EYEBROWS, eyebrows);
        editor.putFloat(ASYMMETRIC_FACE_LIPS, lips);
        editor.putInt(ASYMMETRIC_FACE_EYES_N, eyes_n);
        editor.putInt(ASYMMETRIC_FACE_EYEBROWS_N, eyebrows_n);
        editor.putInt(ASYMMETRIC_FACE_LIPS_N, lips_n);

        editor.apply();
    }

    public static List<Float> loadAsymmetricFaceData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float asymmetric_face_eyes = sharedPreferences.getFloat(ASYMMETRIC_FACE_EYES, 1.9773613733019304764f);
        float asymmetric_face_eyebrows = sharedPreferences.getFloat(ASYMMETRIC_FACE_EYEBROWS, 6.0218941230021674f);
        float asymmetric_face_lips = sharedPreferences.getFloat(ASYMMETRIC_FACE_LIPS, 5.888726197575719988f);
        float asymmetric_face_eyes_n = sharedPreferences.getInt(ASYMMETRIC_FACE_EYES_N, 1);
        float asymmetric_face_eyebrows_n = sharedPreferences.getInt(ASYMMETRIC_FACE_EYEBROWS_N, 1);
        float asymmetric_face_lips_n = sharedPreferences.getInt(ASYMMETRIC_FACE_LIPS_N, 1);

        List<Float> results = new ArrayList<>();
        results.add(asymmetric_face_eyes);
        results.add(asymmetric_face_eyebrows);
        results.add(asymmetric_face_lips);
        results.add(asymmetric_face_eyes_n);
        results.add(asymmetric_face_eyebrows_n);
        results.add(asymmetric_face_lips_n);
        return results;
    }

    public static void saveSmileData(float smile){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(SMILE, smile);
        editor.apply();
    }

    public static float loadSmileData () {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getFloat(SMILE, 5.225230623883975f);
    }

    public static void saveDryLipsData(float edges, int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(DRY_LIPS, edges);
        editor.putInt(DRY_LIPS_N, n);
        editor.apply();
    }

    public static List<Float> loadDryLipsData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float dry_lips = sharedPreferences.getFloat(DRY_LIPS, 531.04084358109003f);
        float dry_lips_n = sharedPreferences.getInt(DRY_LIPS_N, 1);
        List<Float> results = new ArrayList<>();
        results.add(dry_lips);
        results.add(dry_lips_n);
        return results;
    }

    public static void saveSkinColorData(float mean, float last, int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(SKIN_COLOR_MEAN, mean);
        editor.putFloat(SKIN_COLOR_LAST, last);
        editor.putInt(SKIN_COLOR_N, n);
        editor.apply();
    }

    public static List<Float> loadSkinColorData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float skin_color_mean = sharedPreferences.getFloat(SKIN_COLOR_MEAN, 0);
        float skin_color_last = sharedPreferences.getFloat(SKIN_COLOR_LAST, 0);
        float skin_color_n = sharedPreferences.getInt(SKIN_COLOR_N, 0);
        List<Float> results = new ArrayList<>();
        results.add(skin_color_mean);
        results.add(skin_color_last);
        results.add(skin_color_n);
        return results;
    }

    public static void saveEyeBrowsAlopeciaData(float mean, float last, int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(EYEBROWS_ALOPECIA_MEAN, mean);
        editor.putFloat(EYEBROWS_ALOPECIA_LAST, last);
        editor.putInt(EYEBROWS_ALOPECIA_N, n);
        editor.apply();
    }

    public static List<Float> loadEyeBrowsAlopeciaData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float eyebrows_alopecia_mean = sharedPreferences.getFloat(EYEBROWS_ALOPECIA_MEAN, 0);
        float eyebrows_alopecia_last = sharedPreferences.getFloat(EYEBROWS_ALOPECIA_LAST, 0);
        float eyebrows_alopecia_n = sharedPreferences.getInt(EYEBROWS_ALOPECIA_N, 0);

        List<Float> results = new ArrayList<>();
        results.add(eyebrows_alopecia_mean);
        results.add(eyebrows_alopecia_last);
        results.add(eyebrows_alopecia_n);
        return results;
    }

    public static void saveRednessData(float mean, float last, int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(REDNESS_MEAN, mean);
        editor.putFloat(REDNESS_LAST, last);
        editor.putInt(REDNESS_N, n);
        editor.apply();
    }

    public static List<Float> loadRednessData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        float redness_mean = sharedPreferences.getFloat(REDNESS_MEAN, 0);
        float redness_last = sharedPreferences.getFloat(REDNESS_LAST, 0);
        float redness_n = sharedPreferences.getInt(REDNESS_N, 0);
        List<Float> results = new ArrayList<>();
        results.add(redness_mean);
        results.add(redness_last);
        results.add(redness_n);
        return results;
    }

}