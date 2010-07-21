/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.ws.model;

import java.util.ArrayList;

/**
 *
 * @author kerkh010
 */
public class ChipAnnotation {
        private String chipName = "";
        private ArrayList<ProbeSetAnnotation> probeSetAnnotation;

    /**
     * @return the chipName
     */
    public String getChipName() {
        return chipName;
    }

    /**
     * @param chipName the chipName to set
     */
    public void setChipName(String chipName) {
        this.chipName = chipName;
    }

    /**
     * @return the probeSetAnnotation
     */
    public ArrayList<ProbeSetAnnotation> getProbeSetAnnotation() {
        return probeSetAnnotation;
    }

    /**
     * @param probeSetAnnotation the probeSetAnnotation to set
     */
    public void setProbeSetAnnotation(ArrayList<ProbeSetAnnotation> probeSetAnnotation) {
        this.probeSetAnnotation = probeSetAnnotation;
    }
}
