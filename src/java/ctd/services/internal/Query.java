/* Copyright 2010 Wageningen University, Division of Human Nutrition.
 * Drs. R. Kerkhoven, robert.kerkhoven@wur.nl, R.Kerkhoven@atoom.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ctd.services.internal;

import com.skaringa.javaxml.DeserializerException;
import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Ticket;
import ctd.services.getExpressionByProbeSetId;
import ctd.statistics.Statistics;
import ctd.ws.model.ProbeSetExpressionInfo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class Query {

    private String probesetId = "";
    private String amountValues;
    private ArrayList<ProbeSetExpressionInfo> expressionInfo;
    private String svg = "";
    private String svgGroups = "";
    private String zvalue = "";
    private String downloadData = "";
    private String graphType = "";

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
        String amount = "";

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");


        getExpressionByProbeSetId aa = new getExpressionByProbeSetId();
        aa.setProbeSetId(getProbesetId());
        aa.setWsPassword(webservice_password);


        ArrayList<ProbeSetExpressionInfo> psei = aa.getExpressionByProbeSetIdInternal();
        setExpressionInfo(psei);
        if (getGraphType().equals("ungrouped")) {
            amount = String.valueOf((psei.size() * 15) + 60 + 10);
        }
        if (getGraphType().equals("grouped")) {
            ArrayList<String> groups = new ArrayList<String>();
            for (int i = 0; i < psei.size(); i++) {
                ProbeSetExpressionInfo ei = psei.get(i);
                String groupname = ei.getGroupName();
                if (groups.contains(groupname) == false) {
                    groups.add(groupname);
                }
            }
            amount = String.valueOf((groups.size() * 15) + 120);
        }


        return amount;
    }

    /**
     * @param expressionTable the expressionTable to set
     */
    public void setAmountValues(String amountValues) {
        this.amountValues = amountValues;
    }

    /**
     * @return the svgGroups
     */
    public String getSvgGroups() {

        String image = "";

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        getExpressionByProbeSetId aa = new getExpressionByProbeSetId();
        aa.setProbeSetId(getProbesetId());
        aa.setWsPassword(webservice_password);
        ArrayList<ProbeSetExpressionInfo> psei = null;
        try {
            psei = aa.getExpressionByProbeSetIdInternal();
        } catch (NoImplementationException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SerializerException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }

        Iterator it1 = psei.iterator();
        Iterator it_title = psei.iterator();
        Iterator it_groups = psei.iterator();


        //Get groups
        HashMap<String, HashMap<String, ArrayList<ProbeSetExpressionInfo>>> groups = new HashMap<String, HashMap<String, ArrayList<ProbeSetExpressionInfo>>>();
        int counter = 0;
        int counter_groups = 0;
        Integer counter_title = 0;
        while (it_groups.hasNext()) {
            ProbeSetExpressionInfo info = (ProbeSetExpressionInfo) it_groups.next();
            String groupname = info.getGroupName();
            String title = info.getTitle();

            if (groupname != null) {
                if (groups.containsKey(title)) {

                    HashMap<String, ArrayList<ProbeSetExpressionInfo>> ps = groups.get(title);

                    if (ps.containsKey(groupname)) {
                        ArrayList<ProbeSetExpressionInfo> psa = ps.get(groupname);
                        psa.add(info);
                        ps.put(groupname, psa);
                        groups.put(title, ps);

                    }
                    if (ps.containsKey(groupname) == false) {
                        ArrayList<ProbeSetExpressionInfo> psa = new ArrayList<ProbeSetExpressionInfo>();
                        psa.add(info);
                        ps.put(groupname, psa);
                        groups.put(title, ps);
                        counter_groups++;
                    }

                }
                if (groups.containsKey(title) == false) {
                    ArrayList<Integer> gr = new ArrayList<Integer>();

                    HashMap<String, ArrayList<ProbeSetExpressionInfo>> ps = new HashMap<String, ArrayList<ProbeSetExpressionInfo>>();
                    ArrayList<ProbeSetExpressionInfo> psa = new ArrayList<ProbeSetExpressionInfo>();
                    psa.add(info);
                    ps.put(groupname, psa);
                    groups.put(title, ps);
                    counter_groups++;
                    counter_title++;
                }
            }
            counter++;
        }


        Integer amount_rows = counter_title + counter_groups;

        int ori_x = 30;
        int ori_y = 60;
        String total = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 700 " + String.valueOf((amount_rows * 15) + ori_y + 10) + "\" width=\"700\" height=\"" + String.valueOf((amount_rows * 15) + ori_y + 10) + "\">";
        int count = 0;

        //Style
        String style = "<style type=\"text/css\" ><![CDATA[ ]]></style>";//circle {fill: #2222FF;}
        total = total + style;

        //Draw X-bar
        String xline = "<line x1=\"30\" y1=\"50\" x2=\"530\" y2=\"50\" stroke-width=\"4\" stroke=\"black\" />";
        total = total + xline;
        //Draw legenda X-bar
        String legenda = "<text x=\"200\" y=\"18\" fill = \"black\" font-size = \"16\">RMA (Log2)</text>";
        if (getGraphType().equals("grouped")) {
            legenda = "<text x=\"170\" y=\"18\" fill = \"black\" font-size = \"16\">RMA (Log2, extremes and SD)</text>";
        }

        total = total + legenda;
        //Draw scales
        if (getZvalue().equals("zvalue") == false) {
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
        Iterator ti1 = groups.keySet().iterator();
        Integer counter_row = 0;
        Integer counter_value = 0;
        while (ti1.hasNext()) {
            String title = (String) ti1.next();
            counter_row++;
            //draw background
            String bgline = "<rect x=\"30\" y=\"" + String.valueOf((counter_row * 15) + ori_y - 7.5D) + "\" width=\"500\" height=\"15\" style=\"fill:grey;stroke:grey;stroke-width:1;fill-opacity:0.1;stroke-opacity:0.1\" />";
            if (counter_row % 2 == 0) {
                image = image + " " + bgline;
            }
            //draw title
            String title_svg = "<text x=\"30\" y=\"" + String.valueOf((counter_row * 15) + ori_y + 5) + "\" fill=\"black\" font-size = \"10\">" + title + "</text>";
            image = image + " " + title_svg;


            HashMap<String, ArrayList<ProbeSetExpressionInfo>> gr = groups.get(title);
            Iterator ti2 = gr.keySet().iterator();

            Iterator ti4 = gr.keySet().iterator();
            ArrayList<String> group_names_list = new ArrayList<String>();
            while (ti4.hasNext()) {
                String groupname = (String) ti4.next();
                group_names_list.add(groupname);
            }
            Collections.sort(group_names_list);

            for (int j = 0; j < group_names_list.size(); j++) {
                String groupname = group_names_list.get(j);

                ArrayList<ProbeSetExpressionInfo> gr1 = gr.get(groupname);
                ///statistics on values in group
                Double high = 0.0D;
                Double low = 0.0D;
                Double average = 0.0D;
                Double std = 0.0D;
                Statistics st = new Statistics();
                ArrayList<Double> values = new ArrayList<Double>();

                for (int i = 0; i < gr1.size(); i++) {
                    ProbeSetExpressionInfo pse = gr1.get(i);
                    String local_accession = pse.getLocalAccession();

                    Double value = pse.getLog2Value();
                    values.add(value);
                }
                st.setData(values);
                average = st.getAverage();
                std = st.getSTD();
                high = st.getHigh();
                low = st.getLow();

                counter_value++;
                counter_row++;

                //Draw group data on one line.
                //numbering
                String number = "<text x=\"4\" y=\"" + String.valueOf((counter_row * 15) + ori_y + 5) + "\" fill = \"black\" font-size = \"11\">" + String.valueOf(counter_value) + "</text>";
                image = image + " " + number;
                //name
                String name = "<text x=\"530\" y=\"" + String.valueOf((counter_row * 15) + ori_y + 5) + "\" fill = \"black\" font-size = \"11\">" + groupname + "</text>";
                image = image + " " + name;
                //background
                bgline = "<rect x=\"30\" y=\"" + String.valueOf((counter_row * 15) + ori_y - 7.5D) + "\" width=\"500\" height=\"15\" style=\"fill:grey;stroke:grey;stroke-width:1;fill-opacity:0.1;stroke-opacity:0.1\" />";
                if (counter_row % 2 == 0) {
                    image = image + " " + bgline;
                }

                //values
                String std_line = "<line x1=\"" + String.valueOf((average * 31.25D) - (15.7D * std) + ori_x) + "\" y1=\"" + String.valueOf((counter_row * 15) + ori_y) + "\" x2=\"" + String.valueOf((average * 31.25D) + (15.7D * std) + ori_x) + "\" y2=\"" + String.valueOf((counter_row * 15) + ori_y) + "\" stroke-width=\"2\"  stroke=\"black\" />";
                image = image + " " + std_line;

                String shape = "<circle cx=\"" + String.valueOf((average * 31.25D) + ori_x) + "\" cy=\"" + String.valueOf((counter_row * 15) + ori_y) + "\" r=\"3\" stroke=\"black\" fill=\"red\" stroke-width=\"1\"/>";
                image = image + " " + shape;

                shape = "<circle cx=\"" + String.valueOf((low * 31.25D) + ori_x) + "\" cy=\"" + String.valueOf((counter_row * 15) + ori_y) + "\" r=\"3\" stroke=\"black\" fill=\"blue\" stroke-width=\"1\"/>";
                image = image + " " + shape;

                shape = "<circle cx=\"" + String.valueOf((high * 31.25D) + ori_x) + "\" cy=\"" + String.valueOf((counter_row * 15) + ori_y) + "\" r=\"3\" stroke=\"black\" fill=\"blue\" stroke-width=\"1\"/>";
                image = image + " " + shape;
            }
        }

        total = total + image + "</svg>";

        

        return total;
    }

    /**
     * @param svgGroups the svgGroups to set
     */
    public void setSvgGroups(String svgGroups) {
        this.svgGroups = svgGroups;
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
        aa.setWsPassword(webservice_password);
        ArrayList<ProbeSetExpressionInfo> psei = aa.getExpressionByProbeSetIdInternal();

        Iterator it1 = psei.iterator();
        Iterator it_title = psei.iterator();
        Iterator it_groups = psei.iterator();

        int ori_x = 30;
        int ori_y = 60;

        String total = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 700 " + String.valueOf((psei.size() * 15) + ori_y + 10) + "\" width=\"700\" height=\"" + String.valueOf((psei.size() * 15) + ori_y + 10) + "\">";
        int count = 0;

        //Style
        String style = "<style type=\"text/css\" ><![CDATA[ circle {fill: #2222FF;}]]></style>";
        total =
                total + style;

        //Draw X-bar
        String xline = "<line x1=\"30\" y1=\"50\" x2=\"530\" y2=\"50\" stroke-width=\"4\" stroke=\"black\" />";
        total =total + xline;
        //Draw legenda X-bar
        String legenda = "<text x=\"200\" y=\"18\" fill = \"black\" font-size = \"16\">RMA (Log2)</text>";
        if (getGraphType().equals("grouped")) {
            legenda = "<text x=\"170\" y=\"18\" fill = \"black\" font-size = \"16\">RMA (Log2) extremes, average and SD.)</text>";
        }

        total = total + legenda;



        //Draw scales
        if (getZvalue().equals("zvalue") == false) {
            for (int i = 0; i <
                    17; i++) {
                String xscale = "<line x1=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y1=\"50\" x2=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y2=\"40\" stroke-width=\"3\"  stroke=\"black\" />";
                total =
                        total + xscale;
                String number = "<text x=\"" + String.valueOf(ori_x + (i * 31.25D) - 5) + "\" y=\"36\" fill = \"black\" font-size = \"15\">" + String.valueOf(i) + "</text>";
                total =
                        total + number;
            }

        }
        if (getZvalue().equals("zvalue")) {
            for (int i = 0; i <
                    17; i++) {
                String xscale = "<line x1=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y1=\"50\" x2=\"" + String.valueOf(ori_x + (i * 31.25D)) + "\" y2=\"40\" stroke-width=\"3\"  stroke=\"black\" />";
                total =
                        total + xscale;
                String number = "<text x=\"" + String.valueOf(ori_x + (i * 31.25D) - 5) + "\" y=\"36\" fill = \"black\" font-size = \"15\">" + String.valueOf(i - 8) + "</text>";
                total =
                        total + number;
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


            Double zscore = (value - avg) / std;
            if (getZvalue().equals("zvalue")) {
                value = 8.0D + zscore;
            }

//numbering
            String number = "<text x=\"4\" y=\"" + String.valueOf((count * 15) + ori_y + 5) + "\" fill = \"black\" font-size = \"11\">" + String.valueOf(count) + "</text>";
            image =
                    image + " " + number;
            //name
            String name = "<text x=\"530\" y=\"" + String.valueOf((count * 15) + ori_y + 5) + "\" fill = \"black\" font-size = \"11\">" + local_accession + "</text>";
            image =
                    image + " " + name;
            //background
            String bgline = "<rect x=\"30\" y=\"" + String.valueOf((count * 15) + ori_y - 7.5D) + "\" width=\"500\" height=\"15\" style=\"fill:grey;stroke:grey;stroke-width:1;fill-opacity:0.1;stroke-opacity:0.1\" />";
            if (count % 2 == 1) {
                image = image + " " + bgline;
            }
//values

            String shape = "<circle cx=\"" + String.valueOf((value * 31.25D) + ori_x) + "\" cy=\"" + String.valueOf((count * 15) + ori_y) + "\" r=\"5\" stroke=\"black\" stroke-width=\"1\"/>";
            image =
                    image + " " + shape;
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

    /**
     * @return the downloadData
     */
    public String getDownloadData() {
        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String ftp_username = res.getString("ws.ftp_username");
        String ftp_folder = res.getString("ws.ftp_folder");
        String hostname = res.getString("ws.hostname");


        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Ticket ticket = null;

        SQLQuery q1 = session.createSQLQuery("Select ctd_REF,title,folder,id FROM ticket Order By id ASC");
        Iterator it1 = q1.list().iterator();

        String table = "";

        while (it1.hasNext()) {
            Object[] data = (Object[]) it1.next();
            String ctd_REF = (String) data[0];
            String title = (String) data[1];
            String folder = (String) data[2];

            if (title != null && title.equals("") == false) {
                String link2 = "sftp://" + ftp_username + "@" + hostname + ":" + ftp_folder + folder + "/";
                String link1 = "<a href=\"" + link2 + "\">link</a>";
                table =
                        table + "<tr><td>" + ctd_REF + "</td><td>" + title + "</td><td>" + link1 + "</td></tr>";
            }

        }
        session.close();
        sessionFactory.close();

        return table;
    }

    /**
     * @param downloadData the downloadData to set
     */
    public void setDownloadData(String downloadData) {
        this.downloadData = downloadData;
    }

   

    /**
     * @return the graphType
     */
    public String getGraphType() {
        return graphType;
    }

    /**
     * @param graphType the graphType to set
     */
    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }
}
