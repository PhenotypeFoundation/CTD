/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import ctd.model.GCTFile;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author kerkh010
 */
public class ParserGCTFile {

    private String file;

    public GCTFile startParser() throws FileNotFoundException, IOException {
        GCTFile gctfile = new GCTFile();

        FileInputStream fstream = new FileInputStream(getFile());

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String version = br.readLine();
        String dimension = br.readLine();
        String[] dimensions = dimension.split("\t");
        Integer amount_probes = Integer.valueOf(dimensions[0].trim());
        Integer amount_assays = Integer.valueOf(dimensions[1].trim());

        String header = br.readLine();
        String[] parts = header.split("\t");
        ArrayList<String> assays = new ArrayList<String>();
        for (int i=2;i<parts.length;i++){
            String assay_name = parts[i];
            assays.add(assay_name);
        }
        gctfile.setAssays(assays);


        Double[][] values = new Double[amount_probes][amount_assays];
        String line = "";
        ArrayList<String> probes = new ArrayList<String>();
        ArrayList<String> annotation = new ArrayList<String>();

        int count =0;
        while ((line = br.readLine()) != null)   {
            parts = line.split("\t");
            probes.add(parts[0]);
            annotation.add(parts[1]);
            for (int j=2;j<parts.length;j++){
                Double value = Double.valueOf(parts[j]);
                values[count][j-2]=value;
            }
            count++;
        }
        gctfile.setDescription(annotation);
        gctfile.setValues(values);
        gctfile.setProbes(probes);

        return gctfile;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }
}
