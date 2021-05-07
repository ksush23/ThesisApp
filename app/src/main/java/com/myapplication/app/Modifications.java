package com.myapplication.app;

import com.chaquo.python.PyObject;

import java.util.List;

public class Modifications {

    public static void eyeBagsNotNoticed(){
        float result_r = Detection.eye_bags.get(0).toFloat();
        float result_g = Detection.eye_bags.get(1).toFloat();
        float result_b = Detection.eye_bags.get(2).toFloat();

        List<Float> data = MainActivity.loadEyeBagsData();
        float eye_bags_r = data.get(0);
        float eye_bags_g = data.get(1);
        float eye_bags_b = data.get(2);

        int eye_bags_r_n = Math.round(data.get(3));
        int eye_bags_g_n = Math.round(data.get(4));
        int eye_bags_b_n = Math.round(data.get(5));

        float new_red = eye_bags_r;
        float new_green = eye_bags_g;
        float new_blue = eye_bags_b;
        int new_r_n = eye_bags_r_n;
        int new_g_n = eye_bags_g_n;
        int new_b_n = eye_bags_b_n;
        if (!Detection.red_okay) {
            new_red = (new_red * eye_bags_r_n + result_r) / (eye_bags_r_n + 1);
            new_r_n += 1;
        }
        if (!Detection.green_okay) {
            new_green = (new_green * eye_bags_g_n + result_g) / (eye_bags_g_n + 1);
            new_g_n += 1;
        }
        if (!Detection.blue_okay) {
            new_blue = (new_blue * eye_bags_b_n + result_b) / (eye_bags_b_n + 1);
            new_b_n += 1;
        }

        MainActivity.saveEyeBagsData(new_red, new_green, new_blue, new_r_n, new_g_n, new_b_n);

    }

    public static void blueLipsNotNoticed(){
        float result = Detection.blue_lips.toFloat();
        List<Float> data = MainActivity.loadBlueLipsData();
        float blue_lips = data.get(0);
        int blue_lips_n = Math.round(data.get(0));
        float new_blue_lips = (blue_lips * blue_lips_n + result) / (blue_lips_n + 1);
        MainActivity.saveBlueLipsData(new_blue_lips, blue_lips_n + 1);
    }

    public static void redEyesNotNoticed(){
        float result_left = Detection.red_eyes.get(0).toFloat();
        float result_right = Detection.red_eyes.get(1).toFloat();

        List<Float> data = MainActivity.loadRedEyesData();
        float red_eyes = data.get(0);
        int red_eyes_n = Math.round(data.get(1));

        float new_red;
        if (result_left > result_right){
            new_red = (red_eyes * red_eyes_n + result_left) / (red_eyes_n + 1);
        }
        else{
            new_red = (red_eyes * red_eyes_n + result_right) / (red_eyes_n + 1);
        }

        MainActivity.saveRedEyesData(new_red, red_eyes_n + 1);
    }

    public static void asymmetricEyesNotNoticed(){
        float result_eyes = Detection.asymmetric_face.get(0).toFloat();
        List<Float> data = MainActivity.loadAsymmetricFaceData();
        float asymmetric_face_eyes = data.get(0);
        float asymmetric_face_eyebrows = data.get(1);
        float asymmetric_face_lips = data.get(2);
        int asymmetric_face_eyes_n = Math.round(data.get(3));
        int asymmetric_face_eyebrows_n = Math.round(data.get(4));
        int asymmetric_face_lips_n = Math.round(data.remove(5));
        float new_eyes = (asymmetric_face_eyes * asymmetric_face_eyes_n + result_eyes) / (asymmetric_face_eyes_n + 1);
        MainActivity.saveAsymmetricFaceData(new_eyes, asymmetric_face_eyebrows, asymmetric_face_lips, asymmetric_face_eyes_n + 1,asymmetric_face_eyebrows_n, asymmetric_face_lips_n);
    }

    public static void asymmetricEyebrowsNotNoticed(){
        float result_eyebrows = Detection.asymmetric_face.get(1).toFloat();
        List<Float> data = MainActivity.loadAsymmetricFaceData();
        float asymmetric_face_eyes = data.get(0);
        float asymmetric_face_eyebrows = data.get(1);
        float asymmetric_face_lips = data.get(2);
        int asymmetric_face_eyes_n = Math.round(data.get(3));
        int asymmetric_face_eyebrows_n = Math.round(data.get(4));
        int asymmetric_face_lips_n = Math.round(data.remove(5));
        float new_eyebrows = (asymmetric_face_eyebrows * asymmetric_face_eyebrows_n + result_eyebrows) / (asymmetric_face_eyebrows_n + 1);
        MainActivity.saveAsymmetricFaceData(asymmetric_face_eyes, new_eyebrows, asymmetric_face_lips, asymmetric_face_eyes_n ,asymmetric_face_eyebrows_n + 1, asymmetric_face_lips_n);
    }

    public static void asymmetricLipsNotNoticed(){
        float result_lips = Detection.asymmetric_face.get(2).toFloat();
        List<Float> data = MainActivity.loadAsymmetricFaceData();
        float asymmetric_face_eyes = data.get(0);
        float asymmetric_face_eyebrows = data.get(1);
        float asymmetric_face_lips = data.get(2);
        int asymmetric_face_eyes_n = Math.round(data.get(3));
        int asymmetric_face_eyebrows_n = Math.round(data.get(4));
        int asymmetric_face_lips_n = Math.round(data.remove(5));
        float new_lips = (asymmetric_face_lips * asymmetric_face_lips_n + result_lips) / (asymmetric_face_lips_n + 1);
        MainActivity.saveAsymmetricFaceData(asymmetric_face_eyes, asymmetric_face_eyebrows, new_lips, asymmetric_face_eyes_n ,asymmetric_face_eyebrows_n, asymmetric_face_lips_n + 1);
    }

    public static void dryLipsNotNoticed(){
        float result = Detection.dry_lips.toFloat();
        List<Float> data = MainActivity.loadDryLipsData();
        float dry_lips = data.get(0);
        int dry_lips_n = Math.round(data.get(1));
        float new_lips = (dry_lips * dry_lips_n + result) / (dry_lips_n + 1);
        MainActivity.saveDryLipsData(new_lips, dry_lips_n + 1);
    }


}
