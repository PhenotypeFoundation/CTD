package ctd.ws.model;

/**
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class ExpressionProbesetSample {

    private Double value;
    private String measurementToken;
    private String sampleToken;

    /**
     * @return the dblValue
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param dblValue the dblValue to set
     */
    public void setValue(Double dblValue) {
        this.value = dblValue;
    }

    /**
     * @return the strMeasurementToken
     */
    public String getMeasurementToken() {
        return measurementToken;
    }

    /**
     * @param strMeasurementToken the strMeasurementToken to set
     */
    public void setMeasurementToken(String strMeasurementToken) {
        this.measurementToken = strMeasurementToken;
    }

    /**
     * @return the strSampleToken
     */
    public String getSampleToken() {
        return sampleToken;
    }

    /**
     * @param strSampleToken the strSampleToken to set
     */
    public void setSampleToken(String strSampleToken) {
        this.sampleToken = strSampleToken;
    }
}

