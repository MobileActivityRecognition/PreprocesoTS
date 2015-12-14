/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.procesoWeka;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Debug;
 
import weka.core.Instances;
import static weka.core.SerializationHelper.write;
import weka.experiment.InstanceQuery;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
 
public class ProcesoWeka {
 
    private static final String path = "src\\com\\fpuna\\procesoWeka\\";
    
    public static void main(String[] args) throws ClassNotFoundException,
            SQLException, Exception {
         
        /***************************
         * Instances from Database
         ****************************/
        InstanceQuery query = new InstanceQuery();
        query.setUsername("ARUSER");
        query.setPassword("ARUSER");
        query.setQuery("SELECT \"TrainingSetFeature\".mean_x, \"TrainingSetFeature\".std_x, \"TrainingSetFeature\".max_x, \"TrainingSetFeature\".min_x, \"TrainingSetFeature\".skewness_x, \n" +
                        "  \"TrainingSetFeature\".kurtosis_x, \"TrainingSetFeature\".energy_x, \"TrainingSetFeature\".entropy_x, \"TrainingSetFeature\".iqr_x, \"TrainingSetFeature\".ar_x_1, \n" +
                        "  \"TrainingSetFeature\".ar_x_2, \"TrainingSetFeature\".ar_x_3, \"TrainingSetFeature\".ar_x_4, \"TrainingSetFeature\".\"meanFreq_x\", \"TrainingSetFeature\".\"Etiqueta\" FROM \"TrainingSetFeature\"");
 
        Instances dataTrain = query.retrieveInstances();
        //System.out.println(data);
         
        crearArbolDecision(dataTrain, path + "tree.txt");
        
    }
    
    public static void crearArbolDecision(Instances train, String FileOutput) throws Exception {

        J48 cls = new J48();
       
        //Se selecciona el ultimo atributo como index class
        train.setClassIndex(train.numAttributes() - 1);

        //Se contruye el arbol
        cls.buildClassifier(train);

        //Guardo la clase generada
        PrintWriter out = new PrintWriter(path + "WekaWrapper.java");
        out.print(cls.toSource("WekaWrapper"));
        out.close();
        //System.out.println("Grafo2: " + cls.toSource("WekaWrapper"));
        
        Evaluation eval = new Evaluation(train);
        Debug.Random rand = new Debug.Random(1);  // using seed = 1
        int folds = 10;
        eval.crossValidateModel(cls, train, folds, rand);
        
        //Mostramos las estadisticas del arbol
        System.out.println(eval.toSummaryString());
        
        //Graficamos el arbol
        final javax.swing.JFrame jf = new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
        jf.setSize(1920, 3081);
        jf.getContentPane().setLayout(new BorderLayout());
        TreeVisualizer tv = new TreeVisualizer(null,cls.graph(),new PlaceNode2());
        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });
        jf.setVisible(true);
        tv.fitToScreen();
        


        //Graba el modelo
        write(path + "j48.model", cls);

        //Graba el archivo de salida
        escribirArchivo(FileOutput, cls.toString());

    }

    private static void escribirArchivo(String PathArchivo, String content) {

        FileWriter fw = null;
        String line;
        int l = 0;

        try {
            fw = new FileWriter(PathArchivo);
            BufferedWriter bw = new BufferedWriter(fw);
            

            String[] lines = content.split("\n");
            for (String tmpLine : lines) {
                if(l > 2 && l < (lines.length - 4)){
                    bw.write(tmpLine);
                    bw.write("\n");
                }
                l++;
            }
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(ProcesoWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}