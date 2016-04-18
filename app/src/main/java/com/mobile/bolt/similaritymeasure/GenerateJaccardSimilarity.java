package com.mobile.bolt.similaritymeasure;

import android.util.Log;

import java.util.List;

import Jama.Matrix;

/**
 * Created by Neeraj on 4/18/2016.
 */
public class GenerateJaccardSimilarity {
    private static String TAG = "MobileGrading";
    public static double generate(List<Integer> old, List<Integer> New){
        double old_integer[][];
        double new_integer[][];
        old_integer = new double[old.size()][1];
        new_integer = new double[New.size()][1];
        Log.d(TAG, "jaccard: generate: old" + old.toString());
        Log.d(TAG, "jaccard: generate: new"+New.toString());
        for(int i=0 ; i<old.size();i++){
            old_integer[i][0] = (double) old.get(i);
        }
        for(int i=0 ; i<New.size();i++){
            new_integer[i][0] = (double) New.get(i);
        }
        Matrix sourceDoc = new Matrix(old_integer);
        Matrix targetDoc = new Matrix(new_integer);
        return computeSimilarity(sourceDoc,targetDoc);
    }

    private static double computeSimilarity(Matrix source, Matrix target) {
        double intersection = 0.0D;
        for (int i = 0; i < source.getRowDimension();i++) {
            intersection += Math.min(source.get(i, 0), target.get(i, 0));
        }
        if (intersection > 0.0D) {
            double union = source.norm1() + target.norm1() - intersection;
            return intersection / union;
        } else {
            return 0.0D;
        }
    }
}
