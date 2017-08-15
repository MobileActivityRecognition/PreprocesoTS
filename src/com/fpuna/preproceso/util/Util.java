/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.preproceso.util;

import com.fpuna.preproceso.Registro;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 *
 * @author Santirrium
 */
public class Util {

    public static Double calculateShannonEntropy(double valuesD[]) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        List<String> values = new ArrayList<String>();
        for (Double d : valuesD) {
            values.add(d.toString());
        }

        // count the occurrences of each value
        for (String sequence : values) {
            if (!map.containsKey(sequence)) {
                map.put(sequence, 0);
            }
            map.put(sequence, map.get(sequence) + 1);
        }

        // calculate the entropy
        Double result = 0.0;
        for (String sequence : map.keySet()) {
            Double frequency = (double) map.get(sequence) / values.size();
            result -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return result;
    }

    public static double[] transform(double[] input) {
        //double[] tempConversion = new double[input.length];
        double[] tempConversion = new double[2048];

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        try {
            Complex[] complx = transformer.transform(input, TransformType.FORWARD);

            for (int i = 0; i < complx.length; i++) {
                double rr = (complx[i].getReal());
                double ri = (complx[i].getImaginary());

                tempConversion[i] = Math.sqrt((rr * rr) + (ri * ri));
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }

        return tempConversion;
    }

    public static double meanFreq(double[] fft, double[] s) {
        int tamanho = fft.length;
        double result = 0.0;
        double numerador = 0.0;
        double denominador = 0.0;

        for (int i = 0; i < fft.length; i++) {
            numerador = numerador + fft[i] * s[i];
            denominador = denominador + fft[i];

        }
        result = numerador / denominador;

        return result;
    }

    public static double sma(double[] x, double[] y, double[] z) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (Math.abs(x[i]) + Math.abs(y[i]) + Math.abs(z[i]));
        }
        return sum / x.length;
    }

    /**
     * Calculo de Moving Average Filters sobre un vector de registros
     *
     * @param muestras Vector de muestras
     * @param m numero de puntos
     * @return
     */
    public static Registro[] maf(Registro[] muestras, int m) {
        Registro[] mFiltradas = new Registro[muestras.length - m];

        for (int i = 0; i < (muestras.length - m); i++) {;
            mFiltradas[i] = muestras[i];

            mFiltradas[i].setValor_x(muestras[i].getValor_x() / m);
            mFiltradas[i].setValor_y(muestras[i].getValor_y() / m);
            mFiltradas[i].setValor_z(muestras[i].getValor_z() / m);

            for (int j = 1; j <= m-1; j++) {
                mFiltradas[i].setValor_x(mFiltradas[i].getValor_x() + muestras[i + j].getValor_x() / m);
                mFiltradas[i].setValor_y(mFiltradas[i].getValor_y() + muestras[i + j].getValor_y() / m);
                mFiltradas[i].setValor_z(mFiltradas[i].getValor_z() + muestras[i + j].getValor_z() / m);
            }

        }
        return mFiltradas;
    }

    public static List<Registro> calcMagnitud(List<Registro> muestras) {
        
        for (int i = 0; i < muestras.size(); i++) {
            
            muestras.get(i).setM_1(Math.sqrt(Math.pow(muestras.get(i).getValor_x(), 2) + Math.pow(muestras.get(i).getValor_y(), 2) + Math.pow(muestras.get(i).getValor_z(), 2)));
        }
        
        return muestras;
    }
}
