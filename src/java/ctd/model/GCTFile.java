/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.model;

import java.util.ArrayList;

/**
 *
 * @author kerkh010
 */
public class GCTFile {
    private ArrayList<String> probes;
    private ArrayList<String> assays;
    private ArrayList<String> description;
    private Double[][] values;
    private String version;


    /**
     * @return the probes
     */
    public ArrayList<String> getProbes() {
        return probes;
    }

    /**
     * @param probes the probes to set
     */
    public void setProbes(ArrayList<String> probes) {
        this.probes = probes;
    }

    /**
     * @return the assays
     */
    public ArrayList<String> getAssays() {
        return assays;
    }

    /**
     * @param assays the assays to set
     */
    public void setAssays(ArrayList<String> assays) {
        this.assays = assays;
    }

    /**
     * @return the values
     */
    public Double[][] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Double[][] values) {
        this.values = values;
    }

    /**
     * @return the description
     */
    public ArrayList<String> getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
