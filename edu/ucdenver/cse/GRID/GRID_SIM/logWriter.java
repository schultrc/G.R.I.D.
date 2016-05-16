package edu.ucdenver.cse.GRID.GRID_SIM;

import javax.swing.JFileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class logWriter {
    private static File selectedFile;
    private static FileWriter fw;
    private static BufferedWriter bw;
    private static PrintWriter out;

    public logWriter(){
        //Create a file chooser
        final JFileChooser fc = new JFileChooser("data/");

        //In response to a button click:
        int returnVal = fc.showOpenDialog(null);
        selectedFile = fc.getSelectedFile();
    }

    public static void main(String args[])
    {
        writeOutput("test000");
        writeOutput("test001");
        writeOutput("test002");
        writeOutput("test003");
        writeOutput("test004");
    }

    public static void writeOutput(String outputString) {
        try (FileWriter fw = new FileWriter(selectedFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(outputString);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}