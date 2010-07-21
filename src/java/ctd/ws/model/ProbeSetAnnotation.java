/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.ws.model;

/**
 *
 * @author kerkh010
 */
public class ProbeSetAnnotation {
    private String probeSet;
    private String geneAccession;
    private String geneSymbol;
    private String geneDescription;

    /**
     * @return the probeSet
     */
    public String getProbeSet() {
        return probeSet;
    }

    /**
     * @param probeSet the probeSet to set
     */
    public void setProbeSet(String probeSet) {
        this.probeSet = probeSet;
    }

    /**
     * @return the geneAccession
     */
    public String getGeneAccession() {
        return geneAccession;
    }

    /**
     * @param geneAccession the geneAccession to set
     */
    public void setGeneAccession(String geneAccession) {
        this.geneAccession = geneAccession;
    }

    /**
     * @return the geneSymbol
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @param geneSymbol the geneSymbol to set
     */
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
     * @return the geneDescription
     */
    public String getGeneDescription() {
        return geneDescription;
    }

    /**
     * @param geneDescription the geneDescription to set
     */
    public void setGeneDescription(String geneDescription) {
        this.geneDescription = geneDescription;
    }

}
