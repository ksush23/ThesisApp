package com.myapplication.app;

import com.chaquo.python.PyObject;

import java.util.ArrayDeque;
import java.util.List;

public class Detection {

    protected static List<PyObject> eye_bags;
    protected static List<PyObject> red_eyes;
    protected static PyObject blue_lips;
    protected static List<PyObject> asymmetric_face;
    protected static PyObject smile;
    protected static PyObject dry_lips;
    protected static PyObject skin_color;
    protected static PyObject eyebrows_alopecia;
    protected static PyObject redness;

    protected static boolean red_okay;
    protected static boolean green_okay;
    protected static boolean blue_okay;
    protected static boolean blue_lips_okay;
    protected static boolean red_eyes_okay;
    protected static boolean asymmetric_eyes_okay;
    protected static boolean asymmetric_eyebrows_okay;
    protected static boolean asymmetric_lips_okay;
    protected static boolean smile_okay;
    protected static boolean dry_lips_okay;
    protected static boolean skin_color_okay;
    protected static boolean eyebrows_alopecia_okay;
    protected static boolean redness_okay;

    public static void detection (ArrayDeque<String> arrayDeque){
        ArrayDeque<String> deque = new ArrayDeque<>(arrayDeque);
        while (!deque.isEmpty()){
            String element = deque.remove();
            switch (element){
                case "EyeBags":
                    if (eye_bags != null)
                        eyeBagsDetection(null);
                    break;
                case "BlueLips":
                    if (blue_lips != null)
                        blueLipsDetection(null);
                    break;
                case "RedEyes":
                    if (red_eyes != null)
                        redEyesDetection(null);
                    break;
                case "AsymmetricEyes":
                    if (asymmetric_face != null)
                        asymmetricEyesDetection(null);
                    break;
                case "AsymmetricEyebrows":
                    if (asymmetric_face != null)
                        asymmetricEyebrowsDetection(null);
                    break;
                case "AsymmetricLips":
                    if (asymmetric_face != null)
                        asymmetricLipsDetection(null);
                    break;
                case "Smile":
                    if (smile != null)
                        smileDetection();
                    break;
                case "DryLips":
                    if (dry_lips != null)
                        dryLipsDetection();
                    break;
                case "SkinColor":
                    if (skin_color != null)
                        skinColorDetection();
                    break;
                case "EyebrowsAlopecia":
                    if (eyebrows_alopecia != null)
                        eyebrowsAlopeciaDetection();
                    break;
                case "Redness":
                    if (redness != null)
                        rednessDetection();
                    break;
            }
        }
    }

    public static void trial_detection(ArrayDeque<String> arrayDeque, List<Float> eyeBags, List<Float> blueLips, List<Float> redEyes, List<Float> asymmetricFace, List<Float> dryLips) {
        ArrayDeque<String> deque = new ArrayDeque<>(arrayDeque);
        while (!deque.isEmpty()) {
            String element = deque.remove();
            switch (element) {
                case "EyeBags":
                    if (eye_bags != null && eyeBags != null)
                        eyeBagsDetection(eyeBags);
                    break;
                case "BlueLips":
                    if (blue_lips != null && blueLips != null)
                        blueLipsDetection(blueLips);
                    break;
                case "RedEyes":
                    if (red_eyes != null && redEyes != null)
                        redEyesDetection(redEyes);
                    break;
                case "AsymmetricFace":
                    if (asymmetric_face != null && asymmetricFace != null) {
                        asymmetricEyesDetection(asymmetricFace);
                        asymmetricEyebrowsDetection(asymmetricFace);
                        asymmetricLipsDetection(asymmetricFace);
                    }
                    break;
                case "Smile":
                    if (smile != null)
                        smileDetection();
                    break;
            }
        }
    }

    public static void eyeBagsDetection(List<Float> data) {
        float result_r = eye_bags.get(0).toFloat();
        float result_g = eye_bags.get(1).toFloat();
        float result_b = eye_bags.get(2).toFloat();

        if (data == null)
            data = MainActivity.loadEyeBagsData();
        float eye_bags_r = data.get(0);
        float eye_bags_g = data.get(1);
        float eye_bags_b = data.get(2);

        red_okay = !(result_r > eye_bags_r);
        green_okay = !(result_g > eye_bags_g);
        blue_okay = !(result_b > eye_bags_b);
    }

    public static void blueLipsDetection(List<Float> data){
        float result = blue_lips.toFloat();
        if (data == null) {
            data = MainActivity.loadBlueLipsData();
        }
        float blue_lips = data.get(0);
        blue_lips_okay = !(result < blue_lips);
    }

    public static void redEyesDetection(List<Float> data){
        float result_left = red_eyes.get(0).toFloat();
        float result_right = red_eyes.get(1).toFloat();

        if (data == null) {
            data = MainActivity.loadRedEyesData();
        }
        float red_eyes = data.get(0);

        red_eyes_okay = !(result_left > red_eyes || result_right > red_eyes);
    }

    public static void asymmetricEyesDetection(List<Float> data){
        float result_eyes = asymmetric_face.get(0).toFloat();

        if (data == null) {
            data = MainActivity.loadAsymmetricFaceData();
        }
        float asymmetric_face_eyes = data.get(0);

        asymmetric_eyes_okay = !(result_eyes > asymmetric_face_eyes);
    }

    public static void asymmetricEyebrowsDetection(List<Float> data){
        float result_eyebrows = asymmetric_face.get(1).toFloat();

        if (data == null) {
            data = MainActivity.loadAsymmetricFaceData();
        }
        float asymmetric_face_eyebrows = data.get(1);

        asymmetric_eyebrows_okay = !(result_eyebrows > asymmetric_face_eyebrows);
    }

    public static void asymmetricLipsDetection(List<Float> data){
        float result_mouth = asymmetric_face.get(2).toFloat();

        if (data == null) {
            data = MainActivity.loadAsymmetricFaceData();
        }
        float asymmetric_face_lips = data.get(2);

        asymmetric_lips_okay = !(result_mouth > asymmetric_face_lips);
    }

    public static void smileDetection(){
        float result = smile.toFloat();
        float smile = MainActivity.loadSmileData();
        smile_okay = !(smile > result);
    }

    public static void dryLipsDetection(){
        float result = dry_lips.toFloat();

        List<Float> data = MainActivity.loadDryLipsData();
        float dry_lips = data.get(0);

        dry_lips_okay = !(result > dry_lips);
    }

    public static void skinColorDetection(){
        float result = skin_color.toFloat();
        List<Float> data = MainActivity.loadSkinColorData();
        float skin_color_mean = data.get(0);
        float skin_color_last = data.get(1);
        int skin_color_n = Math.round(data.get(2));

        if (skin_color_n > 0){
            if (result - skin_color_mean > MainActivity.skin_color || result - skin_color_last > MainActivity.skin_color){
                skin_color_okay = false;
            }
            else{
                float new_mean = (skin_color_mean * skin_color_n + result) / (skin_color_n + 1);
                skin_color_okay = true;
                MainActivity.saveSkinColorData(new_mean, result, skin_color_n + 1);
            }
        }else{
            skin_color_okay = true;
            MainActivity.saveSkinColorData(result, result, 1);
        }
    }

    public static void eyebrowsAlopeciaDetection(){
        float result = eyebrows_alopecia.toFloat();
        List<Float> data = MainActivity.loadEyeBrowsAlopeciaData();
        float eyebrows_alopecia_mean = data.get(0);
        float eyebrows_alopecia_last = data.get(1);
        int eyebrows_alopecia_n = Math.round(data.get(2));

        if (eyebrows_alopecia_n > 0){
            if (eyebrows_alopecia_mean - result > MainActivity.eyebrows_alopecia || eyebrows_alopecia_last - result > MainActivity.eyebrows_alopecia){
                eyebrows_alopecia_okay = false;
            }
            else{
                float new_eyebrows_alopecia = (eyebrows_alopecia_mean * eyebrows_alopecia_n + result) / (eyebrows_alopecia_n + 1);
                eyebrows_alopecia_okay = true;
                MainActivity.saveEyeBrowsAlopeciaData(new_eyebrows_alopecia, result, eyebrows_alopecia_n + 1);
            }
        } else{
            eyebrows_alopecia_okay = true;
            MainActivity.saveEyeBrowsAlopeciaData(result, result, 1);
        }
    }

    public static void rednessDetection(){
        float result = redness.toFloat();
        List<Float> data = MainActivity.loadRednessData();
        float redness_mean = data.get(0);
        float redness_last = data.get(1);
        int redness_n = Math.round(data.get(2));

        if (redness_n > 0) {
            if (result - redness_mean > MainActivity.redness || result - redness_last > MainActivity.redness) {
                redness_okay = false;
            }
            else{
                float new_redness = (redness_mean * redness_n + result) / (redness_n + 1);
                redness_okay = true;
                MainActivity.saveRednessData(new_redness, result, redness_n + 1);
            }
        } else {
            redness_okay = true;
            MainActivity.saveRednessData(result, result, 1);
        }
    }
}
