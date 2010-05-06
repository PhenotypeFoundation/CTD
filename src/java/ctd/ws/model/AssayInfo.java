/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.ws.model;

/**
 *
 * @author kerkh010
 */
public class AssayInfo {
    private String XREF;
    private String nameRawfile;
    private Double average;
    private Double std;

    /**
     * @return the XREF
     */
    public String getXREF() {
        return XREF;
    }

    /**
     * @param XREF the XREF to set
     */
    public void setXREF(String XREF) {
        this.XREF = XREF;
    }

    /**
     * @return the nameRawfile
     */
    public String getNameRawfile() {
        return nameRawfile;
    }

    /**
     * @param nameRawfile the nameRawfile to set
     */
    public void setNameRawfile(String nameRawfile) {
        this.nameRawfile = nameRawfile;
    }

    /**
     * @return the average
     */
    public Double getAverage() {
        return average;
    }

    /**
     * @param average the average to set
     */
    public void setAverage(Double average) {
        this.average = average;
    }

    /**
     * @return the std
     */
    public Double getStd() {
        return std;
    }

    /**
     * @param std the std to set
     */
    public void setStd(Double std) {
        this.std = std;
    }
}
