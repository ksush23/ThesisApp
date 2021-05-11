package com.myapplication.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.chaquo.python.PyObject;
import java.util.List;

public class AsyncTaskCalc extends android.os.AsyncTask {

    private ProgressDialog nProgress = null;
    private Context nContext = null;
    protected List<PyObject> eye_bags = null;
    protected List<PyObject> red_eyes = null;
    protected PyObject blue_lips = null;
    protected List<PyObject> asymmetric_face = null;
    protected PyObject smile = null;
    protected PyObject dry_lips = null;
    protected PyObject skin_color = null;
    protected PyObject eyebrows_alopecia = null;
    protected PyObject redness = null;

    private int max;

    AsyncTaskCalc(Context context, int max) {
        nContext = context;
        this.max = max;
    }

    @Override
    protected void onPreExecute() {
        nProgress = new ProgressDialog(nContext);

        nProgress.setMessage("Будь ласка, зачекайте проводиться діаностика");
        nProgress.setTitle("Медична діагностика");

        nProgress.setMax(max);
        nProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        nProgress.setIndeterminate(false);

        nProgress.setCancelable(true);
        nProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });

        nProgress.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        Detection.eye_bags = eye_bags;
        Detection.blue_lips = blue_lips;
        Detection.red_eyes = red_eyes;
        Detection.asymmetric_face = asymmetric_face;
        Detection.smile = smile;
        Detection.dry_lips = dry_lips;
        Detection.skin_color = skin_color;
        Detection.eyebrows_alopecia = eyebrows_alopecia;
        Detection.redness = redness;
        nProgress.dismiss();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        nProgress.setProgress((int)values[0]);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        publishProgress(0);
        List<String> input = (List<String>) objects[0];
        String image = input.get(0);
        for (int i = 1; i < input.size(); i++) {
            String element = input.get(i);
            switch (element) {
                case "EyeBags":
                    eye_bags = Calculations.eyeBagsCalculation(image);
                    publishProgress(1);
                    break;
                case "BlueLips":
                    blue_lips = Calculations.blueLipsCalculation(image);
                    publishProgress(2);
                    break;
                case "RedEyes":
                    red_eyes = Calculations.redEyesCalculation(image);
                    publishProgress(3);
                    break;
                case "AsymmetricFace":
                    asymmetric_face = Calculations.asymmetricFaceCalculation(image);
                    publishProgress(4);
                    break;
                case "Smile":
                    smile = Calculations.smileCalculation(image);
                    publishProgress(5);
                    break;
                case "DryLips":
                    dry_lips = Calculations.dryLipsCalculation(image);
                    publishProgress(6);
                    break;
                case "SkinColor":
                    skin_color = Calculations.skinColorCalculation(image);
                    publishProgress(7);
                    break;
                case "EyebrowsAlopecia":
                    eyebrows_alopecia = Calculations.eyebrowsAlopeciaCalculation(image);
                    publishProgress(8);
                    break;
                case "Redness":
                    redness = Calculations.rednessCalculation(image);
                    publishProgress(9);
                    break;
            }
        }
        return null;
    }

}
