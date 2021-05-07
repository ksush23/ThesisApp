package com.myapplication.app;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.List;

public class Calculations {
    private static Python py = Python.getInstance();
    private static PyObject pyobj = py.getModule("script");

    public static List<PyObject> eyeBagsCalculation(String imageString){
        return pyobj.callAttr("eye_bags_detection", imageString).asList();
    }
    public static PyObject blueLipsCalculation(String imageString){
        return pyobj.callAttr("blue_lips_detection", imageString);
    }
    public static List<PyObject> redEyesCalculation(String imageString){
        return pyobj.callAttr("red_eyes_detection", imageString).asList();
    }
    public static List<PyObject> asymmetricFaceCalculation(String imageString){
        return pyobj.callAttr("asymmetric_face_detection", imageString).asList();
    }
    public static PyObject smileCalculation(String imageString){
        return pyobj.callAttr("depression_detection", imageString);
    }
    public static PyObject dryLipsCalculation(String imageString){
        return pyobj.callAttr("dry_lips_detection", imageString);
    }
    public static PyObject skinColorCalculation(String imageString){
        return pyobj.callAttr("skin_color_detection", imageString);
    }
    public static PyObject eyebrowsAlopeciaCalculation(String imageString){
        return pyobj.callAttr("eyebrows_alopecia_detection", imageString);
    }
    public static PyObject rednessCalculation(String imageString){
        return pyobj.callAttr("redness_detection", imageString);
    }
}
