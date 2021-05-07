package com.myapplication.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.chaquo.python.PyObject;
import java.util.List;

public class AsyncTaskCalc extends android.os.AsyncTask {

    private ProgressDialog nProgress = null;
    private Context nContext = null;
    private boolean changeable = false;
    protected List<PyObject> eye_bags;
    protected List<PyObject> red_eyes;
    protected PyObject blue_lips;
    protected List<PyObject> asymmetric_face;
    protected PyObject smile;
    protected PyObject dry_lips;
    protected PyObject skin_color;
    protected PyObject eyebrows_alopecia;
    protected PyObject redness;

    private int max = 9;

    AsyncTaskCalc(Context context){
        nContext = context;
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
        String image = (String) objects[0];
        eye_bags = Calculations.eyeBagsCalculation(image);
        publishProgress(1);
        blue_lips = Calculations.blueLipsCalculation(image);
        publishProgress(2);
        red_eyes = Calculations.redEyesCalculation(image);
        publishProgress(3);
        asymmetric_face = Calculations.asymmetricFaceCalculation(image);
        publishProgress(4);
        smile = Calculations.smileCalculation(image);
        publishProgress(5);
        dry_lips = Calculations.dryLipsCalculation(image);
        publishProgress(6);
        skin_color = Calculations.skinColorCalculation(image);
        publishProgress(7);
        eyebrows_alopecia = Calculations.eyebrowsAlopeciaCalculation(image);
        publishProgress(8);
        redness = Calculations.rednessCalculation(image);
        publishProgress(9);

        return null;
    }

}
