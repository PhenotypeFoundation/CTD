/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.ws.model;

/**
 *
 * @author kerkh010
 */
public class CombatInfo {
    private Integer amountProbes;
    private Integer amountAssays;
    private String locationFtpFile;

    /**
     * @return the amountProbes
     */
    public Integer getAmountProbes() {
        return amountProbes;
    }

    /**
     * @param amountProbes the amountProbes to set
     */
    public void setAmountProbes(Integer amountProbes) {
        this.amountProbes = amountProbes;
    }

    /**
     * @return the amountAssays
     */
    public Integer getAmountAssays() {
        return amountAssays;
    }

    /**
     * @param amountAssays the amountAssays to set
     */
    public void setAmountAssays(Integer amountAssays) {
        this.amountAssays = amountAssays;
    }

    /**
     * @return the locationFtpFile
     */
    public String getLocationFtpFile() {
        return locationFtpFile;
    }

    /**
     * @param locationFtpFile the locationFtpFile to set
     */
    public void setLocationFtpFile(String locationFtpFile) {
        this.locationFtpFile = locationFtpFile;
    }
}
