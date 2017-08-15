/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.preproceso;

import com.fpuna.preproceso.entities.TrainingSetFeature;
import com.fpuna.preproceso.util.AutoRegression;
import com.fpuna.preproceso.util.Util;
import com.fpuna.preproceso.util.FFTMixedRadix;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author Santirrium
 */
public class PreprocesoTS {

    private static final String path = "src\\com\\fpuna\\preproceso\\data\\initialTS\\";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //String data = path + "20170510_AlbertoG_Nexus5X_W_R";           //BMI160 accelerometer
        //String data = path + "20170510_SimonG_MotoG_XT1540_W_R";      //3-axis Accelerometer
        //String data = path + "20170610_AlbertoG_Nexus5x_W_R_B";       //BMI160 accelerometer
        //String data = path + "20170610_SebasF_LGG2_D625_W_R_B";       //BOSCH BMC150 Acceleration Sensor
        //String data = path + "20170622_log_guido_acosta_samsung_S6";  //MPU6500 Acceleration Sensor
        //String data = path + "20170624_AlbertoG_Nexus5X_W_S_C";       //BMI160 accelerometer
        //String data = path + "20170624_BrendaV_HuaweiM8_W_R_S";       //LSM330 3-axis Accelerometer
        //String data = path + "20170624_PaolaV_SamsungA5_W_R_S_C";     //BOSCH Accelerometer Sensor
        //String data = path + "20170624-2_Marisa_Dominguez_S6edge";    //MPU6500 Acceleration Sensor
        //String data = path + "20170624-2_SantiagoYegros_Mate9";       //accelerometer-lsm6dsm
        //String data = path + "20170624-2_SantiagoYegros_Mate9_2";     //accelerometer-lsm6dsm
        //String data = path + "20170624-2_SantiagoYegros_Mate9_3";     //accelerometer-lsm6dsm
        //String data = path + "20170624-2_SantiagoYegros_Mate9_4";     //accelerometer-lsm6dsm
        //String data = path + "20170627_Guido_Acosta_S6_correr";       //MPU6500 Acceleration Sensor
        //String data = path + "20170809_AlbertoG_Nexus5X_T";           //BMI160 accelerometer
        String data = path + "20170809_SantiagoYegros_Mate9_tilting"; //accelerometer-lsm6dsm
        
        //String db = path + "sensor.db";
        String sensorName = "accelerometer-lsm6dsm";
        //String sensorName = "LIS3DH Accelerometer";
        HashMap<String, SessionTS> sessiones = new HashMap<String, SessionTS>();

        //System.out.println("***** Feature *****");
        //System.out.print("mean_x, std_x, max_x, min_x, skewness_x, kurtosis_x, energy_x, entropy_x, iqr_x, ar_x_1, ar_x_2, ar_x_3, ar_x_4, meanFreq_x, ");
        //System.out.print("mean_y, std_y, max_y, min_y, skewness_y, kurtosis_y, energy_y, entropy_y, iqr_y, ar_y_1, ar_y_2, ar_y_3, ar_y_4, meanFreq_y, ");
        //System.out.print("mean_z, std_z, max_z, min_z, skewness_z, kurtosis_z, energy_z, entropy_z, iqr_z, ar_z_1, ar_z_2, ar_z_3, ar_z_4, meanFreq_z, ");
        //System.out.print("sma_xyz, correlation_xy, correlation_zy, correlation_yz, ");
        //System.out.print("activity");
        //System.out.print("\n");
        //Leo el archivo
        sessiones = leerArchivo(data, sensorName);
        //sessiones = leerArchivos(path, "LIS3DH Accelerometer");
        //sessiones = leerBDtrainingSet(db, "LIS3DH Accelerometer");
        //Preproceso(feature) los datos del archivo para un sensor 
        preProceso(sessiones, sensorName, (float) 2.56);

    }

    /**
     * Metodo estatico que lee el archivo y lo carga en una estructura de hash
     *
     * @param Archivo path del archivo
     * @return Hash con las sessiones leida del archivo de TrainigSet
     */
    public static HashMap<String, SessionTS> leerArchivo(String Archivo, String sensor) {

        HashMap<String, SessionTS> SessionsTotal = new HashMap<String, SessionTS>();
        HashMap<String, String> actividades = new HashMap<String, String>();
        Path file = Paths.get(Archivo);

        if (Files.exists(file) && Files.isReadable(file)) {

            try {
                BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset());

                String line;
                int cabecera = 0;

                while ((line = reader.readLine()) != null) {
                    if (line.contentEquals("statusId | label")) {

                        //Leo todos las actividades  
                        while ((line = reader.readLine()) != null && !line.contentEquals("statusId|sensorName|value|timestamp")) {
                            String part[] = line.split("\\|");
                            actividades.put(part[0], part[1]);
                            SessionTS s = new SessionTS();
                            s.setActividad(part[1]);
                            SessionsTotal.put(part[0], s);
                        }
                        line = reader.readLine();
                    }

                    String lecturas[] = line.split("\\|");

                    if (lecturas[1].contentEquals(sensor)) {
                        Registro reg = new Registro();
                        reg.setSensor(lecturas[1]);
                        String[] values = lecturas[2].split("\\,");

                        if (values.length == 3) {
                            reg.setValor_x(Double.parseDouble(values[0].substring(1)));
                            reg.setValor_y(Double.parseDouble(values[1]));
                            reg.setValor_z(Double.parseDouble(values[2].substring(0, values[2].length() - 1)));
                        } else if (values.length == 5) {
                            reg.setValor_x(Double.parseDouble(values[0].substring(1)));
                            reg.setValor_y(Double.parseDouble(values[1]));
                            reg.setValor_z(Double.parseDouble(values[2]));
                            reg.setM_1(Double.parseDouble(values[3]));
                            reg.setM_2(Double.parseDouble(values[4].substring(0, values[4].length() - 1)));
                        }

                        reg.setTiempo(new Timestamp(Long.parseLong(lecturas[3])));

                        SessionTS s = SessionsTotal.get(lecturas[0]);
                        s.addRegistro(reg);
                        SessionsTotal.replace(lecturas[0], s);
                    }
                }
            } catch (IOException ex) {
                System.err.println("Okapu");
                Logger.getLogger(PreprocesoTS.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return SessionsTotal;
    }

    /**
     * Metodo estatico que lee el archivo y lo carga en una estructura de hash
     *
     * @param Archivo path del archivo
     * @return Hash con las sessiones leida del archivo de TrainigSet
     */
    public static HashMap<String, SessionTS> leerArchivos(String Archivo, String sensor) {

        HashMap<String, SessionTS> SessionsTotal = new HashMap<String, SessionTS>();
        HashMap<String, String> actividades = new HashMap<String, String>();

        String sDirectorio = path;
        File dirList = new File(sDirectorio);

        if (dirList.exists()) { // Directorio existe 
            File[] ficheros = dirList.listFiles();
            for (int x = 0; x < ficheros.length; x++) {
                Path file = Paths.get(path + ficheros[x].getName());

                if (Files.exists(file) && Files.isReadable(file)) {

                    try {
                        BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset());

                        String line;
                        int cabecera = 0;

                        while ((line = reader.readLine()) != null) {
                            if (line.contentEquals("statusId | label")) {

                                //Leo todos las actividades  
                                while ((line = reader.readLine()) != null && !line.contentEquals("statusId|sensorName|value|timestamp")) {
                                    String part[] = line.split("\\|");
                                    actividades.put(part[0], part[1]);
                                    SessionTS s = new SessionTS();
                                    s.setActividad(part[1]);
                                    SessionsTotal.put(part[0], s);
                                }
                                line = reader.readLine();
                            }

                            String lecturas[] = line.split("\\|");

                            if (lecturas[1].contentEquals(sensor)) {
                                Registro reg = new Registro();
                                reg.setSensor(lecturas[1]);
                                String[] values = lecturas[2].split("\\,");

                                if (values.length == 3) {
                                    reg.setValor_x(Double.parseDouble(values[0].substring(1)));
                                    reg.setValor_y(Double.parseDouble(values[1]));
                                    reg.setValor_z(Double.parseDouble(values[2].substring(0, values[2].length() - 1)));
                                } else if (values.length == 5) {
                                    reg.setValor_x(Double.parseDouble(values[0].substring(1)));
                                    reg.setValor_y(Double.parseDouble(values[1]));
                                    reg.setValor_z(Double.parseDouble(values[2]));
                                    reg.setM_1(Double.parseDouble(values[3]));
                                    reg.setM_2(Double.parseDouble(values[4].substring(0, values[4].length() - 1)));
                                }

                                reg.setTiempo(new Timestamp(Long.parseLong(lecturas[3])));

                                SessionTS s = SessionsTotal.get(lecturas[0]);
                                s.addRegistro(reg);
                                SessionsTotal.replace(lecturas[0], s);
                            }
                        }
                    } catch (IOException ex) {
                        System.err.println("Okapu");
                        Logger.getLogger(PreprocesoTS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        } else { //Directorio no existe 

        }

        return SessionsTotal;
    }

    /**
     * Metodo estatico que calcula los features
     *
     * @param sensor nombre del sensor a tener en cuenta
     * @param CantMuestras cantidad de muestras a tomar.
     */
    public static void preProceso(HashMap<String, SessionTS> sessiones, String sensor, float ventanaTiempo) {

        // Anteriormente  ventanaTiempo = cantMuestras 
        List<Registro> muestras = new ArrayList <Registro>();
        int cantR, cantTomados;
        int i;
        long TimeInicio, TimeFin;
        List<TrainingSetFeature> featureList = new ArrayList<TrainingSetFeature>();

        //Extraer la cantidad de muestras del sensor
        for (SessionTS session : sessiones.values()) {

            Registro registros[] = Util.maf(session.getRegistros(), 5);
            cantTomados = 0;
            i = 0;
            TimeInicio = registros[0].getTiempo().getTime(); //Extraemos el primer timestamp para calcular luego las ventanas de tiempo
            System.out.println("*-*-*-*-*-*-*-* " + session.getActividad() + " *-*-*-*-*-*-*-*");

            while (i < registros.length) {

                if (registros[i].getSensor().contentEquals(sensor)) {
                    muestras.add(registros[i]); //Agregamos un registro
                    TimeFin = registros[i].getTiempo().getTime();
                    cantTomados++;
                    //System.out.println("Tomamos: " + cantTomados + " muestras");


                    if (ventanaTiempo <= ((float)(TimeFin - TimeInicio)/1000.0) ) {
                        //calculoFeatures(muestras, session.getActividad());
                        featureList.add(calculoFeaturesMagnitud(muestras, session.getActividad()));
                        TimeInicio = registros[i].getTiempo().getTime();
                        cantTomados = 0;
                        muestras.clear();
                    }
                }
                i++;
            }

        }

        //Guarda los feature
        GuardarFeature(featureList);
    }

    private static void calculoFeatures(Registro[] muestras, String activity) {

        DescriptiveStatistics stats_x = new DescriptiveStatistics();
        DescriptiveStatistics stats_y = new DescriptiveStatistics();
        DescriptiveStatistics stats_z = new DescriptiveStatistics();
        //DescriptiveStatistics stats_m1 = new DescriptiveStatistics();
        //DescriptiveStatistics stats_m2 = new DescriptiveStatistics();
        double[] fft_x;
        double[] fft_y;
        double[] fft_z;
        double[] AR_4;

        for (int i = 0; i < muestras.length; i++) {
            stats_x.addValue(muestras[i].getValor_x());
            stats_y.addValue(muestras[i].getValor_y());
            stats_z.addValue(muestras[i].getValor_z());
        }

        //********* FFT *********
        fft_x = Util.transform(stats_x.getValues());
        fft_y = Util.transform(stats_y.getValues());
        fft_z = Util.transform(stats_z.getValues());

        //******************* Eje X *******************//
        //mean(s) - Arithmetic mean
        System.out.print(stats_x.getMean() + ",");
        //std(s) - Standard deviation
        System.out.print(stats_x.getStandardDeviation() + ",");
        //mad(s) - Median absolute deviation
        //
        //max(s) - Largest values in array
        System.out.print(stats_x.getMax() + ",");
        //min(s) - Smallest value in array
        System.out.print(stats_x.getMin() + ",");
        //skewness(s) - Frequency signal Skewness
        System.out.print(stats_x.getSkewness() + ",");
        //kurtosis(s) - Frequency signal Kurtosis
        System.out.print(stats_x.getKurtosis() + ",");
        //energy(s) - Average sum of the squares
        System.out.print(stats_x.getSumsq() / stats_x.getN() + ",");
        //entropy(s) - Signal Entropy
        System.out.print(Util.calculateShannonEntropy(fft_x) + ",");
        //iqr (s) Interquartile range
        System.out.print(stats_x.getPercentile(75) - stats_x.getPercentile(25) + ",");
        try {
            //autoregression (s) -4th order Burg Autoregression coefficients
            AR_4 = AutoRegression.calculateARCoefficients(stats_x.getValues(), 4, true);
            System.out.print(AR_4[0] + ",");
            System.out.print(AR_4[1] + ",");
            System.out.print(AR_4[2] + ",");
            System.out.print(AR_4[3] + ",");
        } catch (Exception ex) {
            Logger.getLogger(PreprocesoTS.class.getName()).log(Level.SEVERE, null, ex);
        }
        //meanFreq(s) - Frequency signal weighted average
        System.out.print(Util.meanFreq(fft_x, stats_x.getValues()) + ",");

        //******************* Eje Y *******************//
        //mean(s) - Arithmetic mean
        System.out.print(stats_y.getMean() + ",");
        //std(s) - Standard deviation
        System.out.print(stats_y.getStandardDeviation() + ",");
        //mad(s) - Median absolute deviation
        //
        //max(s) - Largest values in array
        System.out.print(stats_y.getMax() + ",");
        //min(s) - Smallest value in array
        System.out.print(stats_y.getMin() + ",");
        //skewness(s) - Frequency signal Skewness
        System.out.print(stats_y.getSkewness() + ",");
        //kurtosis(s) - Frequency signal Kurtosis
        System.out.print(stats_y.getKurtosis() + ",");
        //energy(s) - Average sum of the squares
        System.out.print(stats_y.getSumsq() / stats_y.getN() + ",");
        //entropy(s) - Signal Entropy
        System.out.print(Util.calculateShannonEntropy(fft_y) + ",");
        //iqr (s) Interquartile range
        System.out.print(stats_y.getPercentile(75) - stats_y.getPercentile(25) + ",");
        try {
            //autoregression (s) -4th order Burg Autoregression coefficients
            AR_4 = AutoRegression.calculateARCoefficients(stats_y.getValues(), 4, true);
            System.out.print(AR_4[0] + ",");
            System.out.print(AR_4[1] + ",");
            System.out.print(AR_4[2] + ",");
            System.out.print(AR_4[3] + ",");
        } catch (Exception ex) {
            Logger.getLogger(PreprocesoTS.class.getName()).log(Level.SEVERE, null, ex);
        }
        //meanFreq(s) - Frequency signal weighted average
        System.out.print(Util.meanFreq(fft_y, stats_y.getValues()) + ",");

        //******************* Eje Z *******************//
        //mean(s) - Arithmetic mean
        System.out.print(stats_z.getMean() + ",");
        //std(s) - Standard deviation
        System.out.print(stats_z.getStandardDeviation() + ",");
        //mad(s) - Median absolute deviation
        //
        //max(s) - Largest values in array
        System.out.print(stats_z.getMax() + ",");
        //min(s) - Smallest value in array
        System.out.print(stats_z.getMin() + ",");
        //skewness(s) - Frequency signal Skewness
        System.out.print(stats_z.getSkewness() + ",");
        //kurtosis(s) - Frequency signal Kurtosis
        System.out.print(stats_z.getKurtosis() + ",");
        //energy(s) - Average sum of the squares
        System.out.print(stats_z.getSumsq() / stats_z.getN() + ",");
        //entropy(s) - Signal Entropy
        System.out.print(Util.calculateShannonEntropy(fft_z) + ",");
        //iqr (s) Interquartile range
        System.out.print(stats_z.getPercentile(75) - stats_z.getPercentile(25) + ",");
        try {
            //autoregression (s) -4th order Burg Autoregression coefficients
            AR_4 = AutoRegression.calculateARCoefficients(stats_z.getValues(), 4, true);
            System.out.print(AR_4[0] + ",");
            System.out.print(AR_4[1] + ",");
            System.out.print(AR_4[2] + ",");
            System.out.print(AR_4[3] + ",");
        } catch (Exception ex) {
            Logger.getLogger(PreprocesoTS.class.getName()).log(Level.SEVERE, null, ex);
        }
        //meanFreq(s) - Frequency signal weighted average
        System.out.print(Util.meanFreq(fft_z, stats_z.getValues()) + ",");

        //******************* Feature combinados *******************/
        //sma(s1; s2; s3) - Signal magnitude area
        System.out.print(Util.sma(stats_x.getValues(), stats_y.getValues(), stats_z.getValues()) + ",");
        //correlation(s1; s2) - Pearson Correlation coefficient
        System.out.print(new PearsonsCorrelation().correlation(stats_x.getValues(), stats_y.getValues()) + ",");
        System.out.print(new PearsonsCorrelation().correlation(stats_x.getValues(), stats_z.getValues()) + ",");
        System.out.print(new PearsonsCorrelation().correlation(stats_y.getValues(), stats_z.getValues()) + ",");

        //******************* Actividad *******************/
        System.out.print(activity);
        System.out.print("\n");
    }

    private static TrainingSetFeature calculoFeaturesMagnitud(List<Registro> muestras, String activity) {

        TrainingSetFeature Feature = new TrainingSetFeature();
        DescriptiveStatistics stats_m = new DescriptiveStatistics();

        double[] fft_m;
        double[] AR_4;

        muestras = Util.calcMagnitud(muestras);

        for (int i = 0; i < muestras.size(); i++) {
            stats_m.addValue(muestras.get(i).getM_1());
        }

        //********* FFT *********
        //fft_m = Util.transform(stats_m.getValues());
        fft_m = FFTMixedRadix.fftPowerSpectrum(stats_m.getValues());

        //******************* Calculos Magnitud *******************//
        //mean(s) - Arithmetic mean
        System.out.print(stats_m.getMean() + ",");
        Feature.setMeanX((float) stats_m.getMean());

        //std(s) - Standard deviation
        System.out.print(stats_m.getStandardDeviation() + ",");
        Feature.setStdX((float) stats_m.getStandardDeviation());

        //mad(s) - Median absolute deviation
        //
        //max(s) - Largest values in array
        System.out.print(stats_m.getMax() + ",");
        Feature.setMaxX((float) stats_m.getMax());

        //min(s) - Smallest value in array
        System.out.print(stats_m.getMin() + ",");
        Feature.setMinX((float) stats_m.getMin());

        //skewness(s) - Frequency signal Skewness
        System.out.print(stats_m.getSkewness() + ",");
        Feature.setSkewnessX((float) stats_m.getSkewness());

        //kurtosis(s) - Frequency signal Kurtosis
        System.out.print(stats_m.getKurtosis() + ",");
        Feature.setKurtosisX((float) stats_m.getKurtosis());

        //energy(s) - Average sum of the squares
        System.out.print(stats_m.getSumsq() / stats_m.getN() + ",");
        Feature.setEnergyX((float) (stats_m.getSumsq() / stats_m.getN()));

        //entropy(s) - Signal Entropy
        System.out.print(Util.calculateShannonEntropy(fft_m) + ",");
        Feature.setEntropyX(Util.calculateShannonEntropy(fft_m).floatValue());

        //iqr (s) Interquartile range
        System.out.print(stats_m.getPercentile(75) - stats_m.getPercentile(25) + ",");
        Feature.setIqrX((float) (stats_m.getPercentile(75) - stats_m.getPercentile(25)));

        try {
            //autoregression (s) -4th order Burg Autoregression coefficients
            AR_4 = AutoRegression.calculateARCoefficients(stats_m.getValues(), 4, true);
            System.out.print(AR_4[0] + ",");
            System.out.print(AR_4[1] + ",");
            System.out.print(AR_4[2] + ",");
            System.out.print(AR_4[3] + ",");
            Feature.setArX1((float) AR_4[0]);
            Feature.setArX2((float) AR_4[1]);
            Feature.setArX3((float) AR_4[2]);
            Feature.setArX4((float) AR_4[3]);
        } catch (Exception ex) {
            Logger.getLogger(PreprocesoTS.class.getName()).log(Level.SEVERE, null, ex);
        }
        //meanFreq(s) - Frequency signal weighted average
        System.out.print(Util.meanFreq(fft_m, stats_m.getValues()) + ",");
        Feature.setMeanFreqx((float) Util.meanFreq(fft_m, stats_m.getValues()));

        //******************* Actividad *******************/
        System.out.print(activity);
        System.out.print("\n");
        Feature.setEtiqueta(activity);

        return Feature;
    }

    /**
     *
     * @param trainingSetFeatureList
     */
    public static void GuardarFeature(List<TrainingSetFeature> trainingSetFeatureList) {
        EntityManager entityManager = Persistence.createEntityManagerFactory("PreprocesoTSPU").createEntityManager();

        entityManager.getTransaction().begin();

        Iterator<TrainingSetFeature> Iterator = trainingSetFeatureList.iterator();
        while (Iterator.hasNext()) {
            entityManager.persist(Iterator.next());
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public static HashMap<String, SessionTS> leerBDtrainingSet(String BD, String sensor) {
        HashMap<String, SessionTS> SessionsTotal = new HashMap<String, SessionTS>();
        String Consulta;
        Connection c = null;
        Statement stmt = null;
        Registro reg = new Registro();

        Consulta = "SELECT dat.statusId, dat.sensorName, dat.\"value\", dat.\"timestamp\", lb.\"name\"\n"
                + "FROM sensor_data AS dat, status AS st, label AS lb\n"
                + "WHERE dat.statusId = st.\"_id\" AND st.labelId = lb.\"_id\" ORDER BY dat.\"timestamp\"";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + path + "sensor.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(Consulta);

            while (rs.next()) {
                reg = new Registro();
                reg.setSensor(rs.getString("sensorName"));
                reg.setTiempo(rs.getTimestamp("timestamp"));

                String[] values = (rs.getString("value")).split("\\,");

                if (values.length == 3) {
                    reg.setValor_x(Double.parseDouble(values[0].substring(1)));
                    reg.setValor_y(Double.parseDouble(values[1]));
                    reg.setValor_z(Double.parseDouble(values[2].substring(0, values[2].length() - 1)));
                } else if (values.length == 5) {
                    reg.setValor_x(Double.parseDouble(values[0].substring(1)));
                    reg.setValor_y(Double.parseDouble(values[1]));
                    reg.setValor_z(Double.parseDouble(values[2]));
                    reg.setM_1(Double.parseDouble(values[3]));
                    reg.setM_2(Double.parseDouble(values[4].substring(0, values[4].length() - 1)));
                }

                if (SessionsTotal.containsKey(rs.getString("statusId"))) {
                    SessionTS s = SessionsTotal.get(rs.getString("statusId"));
                    s.addRegistro(reg);
                    SessionsTotal.replace(rs.getString("statusId"), s);
                } else {
                    SessionTS s = new SessionTS();
                    s.setActividad(rs.getString("name"));
                    s.addRegistro(reg);
                    SessionsTotal.put(rs.getString("statusId"), s);
                }
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            System.err.println("Okapu:" + e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return SessionsTotal;
    }
}
