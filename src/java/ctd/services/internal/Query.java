/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import com.skaringa.javaxml.DeserializerException;
import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.SerializerException;
import ctd.services.getExpressionByProbeSetId;
import ctd.ws.model.ProbeSetExpressionInfo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 *
 * @author kerkh010
 */
public class Query {

    private String probesetId = "";
    private String amountValues;
    private ArrayList<ProbeSetExpressionInfo> expressionInfo;
    private String svg = "";
    private String zvalue = "";

    /**
     * @return the probesetId
     */
    public String getProbesetId() {
        return probesetId;
    }

    /**
     * @param probesetId the probesetId to set
     */
    public void setProbesetId(String probesetId) {
        this.probesetId = probesetId;
    }

    /**
     * @return the expressionTable
     */
    public String getAmountValues() throws MalformedURLException, IOException, NoImplementationException, DeserializerException, SerializerException {
        String text = "";

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        getExpressionByProbeSetId aa = new getExpressionByProbeSetId();
        aa.setProbeSetId(getProbesetId());
        aa.setPassword(webservice_password);
        ArrayList<ProbeSetExpressionInfo> psei = aa.getExpressionByProbeSetIdInternal();
        setExpressionInfo(psei);

        text = String.valueOf((psei.size() * 15) + 60 + 10);
        return text;
    }

    /**
     * @param expressionTable the expressionTable to set
     */
    public void setAmountValues(String amountValues) {
        this.amountValues = amountValues;
    }

    /**
     * @return the svg
     */
    public String getSvg() throws MalformedURLException, NoImplementationException, IOException, DeserializerException, SerializerException {

        String image = "";

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        getExpressionByProbeSetId aa = new getExpressionByProbeSetId();
        aa.setProbeSetId(getProbesetId());
        aa.setPassword(webservice_password);
        ArrayList<ProbeSetExpressionInfo> psei = aa.getExpressionByProbeSetIdInternal();

        Iterator it1 = psei.iterator();
        //String total = "<svg viewBox=\"0 0 100% 100%\" width=\""+String.valueOf(psei.size()*30)+"\" height=\"100pt\">";

        int ori_x = 30;
        int ori_y = 60;

        String total = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 700 " + String.valueOf((psei.size() * 15) + ori_y + 10) + "\" width=\"700\" height=\"" + String.valueOf((psei.size() * 15) + ori_y + 10) + "\">";
        int count = 0;

        //Style
        String style = "<style type=\"text/css\" ><![CDATA[ circle {fill: #2222FF;}]]></style>";
        total = total + style;

        //Draw X-bar
        String xline = "<line x1=\"30\" y1=\"50\" x2=\"530\" y2=\"50\" stroke-width=\"4\" stroke=\"black\" />";
        total = total + xline;
        //Draw legenda X-bar
        String legenda = "<text x=\"200\" y=\"18\" fill = \"black\" font-size = \"16\">RMA + GRSN (Log2)</text>";
        if (getZvalue().equals("zvalue")) {
            legenda = "<text x=\"200\" y=\"18\" fill = \"black\" font-size = \"16\">RMA + GRSN (Log2, z-value)</text>";
        }
        total = total + legenda;

        //Draw scales
        if (getZvalue().equals("zvalue")==false) {
            for (int i = 0; i < 17; i++) {
                String xscale = "<line x1=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y1=\"50\" x2=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y2=\"40\" stroke-width=\"3\"  stroke=\"black\" />";
                total = total + xscale;
                String number = "<text x=\"" + String.valueOf(ori_x + (i * 31.25D) - 5) + "\" y=\"36\" fill = \"black\" font-size = \"15\">" + String.valueOf(i) + "</text>";
                total = total + number;
            }
        }
        if (getZvalue().equals("zvalue")) {
            for (int i = 0; i < 17; i++) {
                String xscale = "<line x1=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y1=\"50\" x2=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y2=\"40\" stroke-width=\"3\"  stroke=\"black\" />";
                total = total + xscale;
                String number = "<text x=\"" + String.valueOf(ori_x + (i * 31.25D) - 5) + "\" y=\"36\" fill = \"black\" font-size = \"15\">" + String.valueOf(i - 8) + "</text>";
                total = total + number;
            }
        }

        //Draw dots and shadings and numbering and cel-file names.
        while (it1.hasNext()) {

            count++;
            ProbeSetExpressionInfo pse = (ProbeSetExpressionInfo) it1.next();
            Double value = pse.getLog2Value();
            String local_accession = pse.getLocalAccession();
            Double std = pse.getSTD();
            Double avg = pse.getAverage();

            Double zscore = (value-avg)/std;
            if (getZvalue().equals("zvalue")) {
                value = 8.0D+zscore;
            }

            //numbering
            String number = "<text x=\"4\" y=\"" + String.valueOf((count * 15) + ori_y + 5) + "\" fill = \"black\" font-size = \"11\">" + String.valueOf(count) + "</text>";
            image = image + " " + number;
            //name
            String name = "<text x=\"530\" y=\"" + String.valueOf((count * 15) + ori_y + 5) + "\" fill = \"black\" font-size = \"11\">" + local_accession + "</text>";
            image = image + " " + name;

            //background
            String bgline = "<rect x=\"30\" y=\"" + String.valueOf((count * 15) + ori_y - 7.5D) + "\" width=\"500\" height=\"15\" style=\"fill:grey;stroke:grey;stroke-width:1;fill-opacity:0.1;stroke-opacity:0.1\" />";
            if (count % 2 == 1) {
                image = image + " " + bgline;
            }
            //values
            String shape = "<circle cx=\"" + String.valueOf((value * 31.25D) + ori_x) + "\" cy=\"" + String.valueOf((count * 15) + ori_y) + "\" r=\"5\" stroke=\"black\" stroke-width=\"1\"/>";
            image = image + " " + shape;
        }
        total = total + image + "</svg>";
        return total;
    }

    /**
     * @param svg the svg to set
     */
    public void setSvg(String svg) {
        this.svg = svg;
    }

    /**
     * @return the expressionInfo
     */
    public ArrayList<ProbeSetExpressionInfo> getExpressionInfo() {
        return expressionInfo;
    }

    /**
     * @param expressionInfo the expressionInfo to set
     */
    public void setExpressionInfo(ArrayList<ProbeSetExpressionInfo> expressionInfo) {
        this.expressionInfo = expressionInfo;
    }

    /**
     * @return the zvalue
     */
    public String getZvalue() {
        return zvalue;
    }

    /**
     * @param zvalue the zvalue to set
     */
    public void setZvalue(String zvalue) {
        this.zvalue = zvalue;
    }
}
