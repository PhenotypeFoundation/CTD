package ctd.services;

import com.skaringa.javaxml.DeserializerException;
import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.model.StudySampleAssay;
import ctd.services.exceptions.Exception400BadRequest;
import ctd.services.exceptions.Exception401Unauthorized;
import ctd.services.exceptions.Exception403Forbidden;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.internal.GscfService;
import ctd.services.internal.responseComparator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.cfg.Configuration;

/**
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getSamples {
    private String strAssayToken;
    private String strSessionToken;
    private String strFilename;
    private boolean blnError = false;

    /***
     * This function gets all available Assaytokens from GSCF that can be linked
     * to a specific assay. It also gets a filename of a zip containing .cel files
     *
     * @return the table with filenames and sampletokens
     * @throws Exception400BadRequest this exception is thrown if no sessionToken or assayToken is set
     * @throws Exception403Forbidden this exception is thrown if the sessionToken is invalide
     * @throws Exception500InternalServerError this exception is thrown if there is some kind of unknown error
     */

    public String getSamples() throws Exception400BadRequest, Exception403Forbidden, Exception500InternalServerError {
        StringBuilder strReturn = new StringBuilder();

        // Check if the minimal parameters are set
        if(getSessionToken()==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken==null");
            throw new Exception400BadRequest();
        }
        if(getAssayToken()==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strAssayToken==null");
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        ResourceBundle res = ResourceBundle.getBundle("settings");
        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("assayToken", getAssayToken());

        if(!objGSCFService.isUser(getSessionToken())) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken invalid: "+getSessionToken());
            throw new Exception403Forbidden();
        }

        LinkedList lstGetSamples = objGSCFService.callGSCF2(getSessionToken(),"getSamples",restParams);
        Collections.sort(lstGetSamples, new responseComparator("name"));

        strReturn.append("<table style='width:100%'>");
        strReturn.append("<tr class='fs_th'><th>Filenames</th><th>Samplenames <span class='fs_fontsize'>[<a href='#' onClick='resetall(); return false;'>clear all</a>]</span></th></tr>");

        HashMap<String, String> mapFiles = getFilesFromDatabase(getAssayToken());

        LinkedList<String> lstFilenames = new LinkedList<String>();
        try {
            // Open the ZIP file
            ZipFile zf = new ZipFile(res.getString("ws.upload_folder")+strFilename);

            // Enumerate each entry
            String strUglyHack = "";
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                // Get the entry name
                String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
                if(!zipEntryName.equals(".") && !zipEntryName.equals("..")){
                    if(!strUglyHack.equals("")) strUglyHack += "!!SEP!!";
                    strUglyHack += zipEntryName;
                }
            }
            String[] arrFiles = strUglyHack.split("!!SEP!!");
            Arrays.sort(arrFiles);
            lstFilenames.addAll(Arrays.asList(arrFiles));

        } catch (IOException e) {
            Logger.getLogger(getSamples.class.getName()).log(Level.SEVERE, "ERROR getSamples: "+e.getMessage());
        }

        if(lstGetSamples.size()<lstFilenames.size()) {
            blnError = true;
            return "<b>There are more files in the submitted .zip than there are available samples.</b><br />Go to the study in GSCF (<a href='"+res.getString("gscf.baseURL")+"/assay/showByToken/"+getAssayToken()+"'>link</a>) and add more samples.<br />";
        }
        if(lstFilenames.size()==0) {
            blnError = true;
            return "<b>There are either no files in the submitted .zip, or the .zip is corrupted.</b><br/>No data has been processed!</br>Please make sure your .zip contains cel-files and is readable before you upload it.<br />";
        }

        String strOptions = "<option value='none'>Select a sample for this file...</option>";
        while(lstGetSamples.size()>0) {
            HashMap<String, String> mapSamples = (HashMap<String, String>) lstGetSamples.removeFirst();
            if(!mapFiles.containsKey(mapSamples.get("sampleToken"))) {
                strOptions += "<option value='"+mapSamples.get("sampleToken")+"'>"+mapSamples.get("name")+" - "+mapSamples.get("event")+" - "+mapSamples.get("Text on vial")+"</option>";
            } else {
                strReturn.append("<tr>" +
                    "<td class='fs_fontsize' style='width:50%; background-color:#CCC;'>").append(mapFiles.get(mapSamples.get("sampleToken"))).append("</td>" +
                    "<td class='fs_fontsize' style='width:50%; background-color:#CCC;'>").append(mapSamples.get("name")).append(" - ").append(mapSamples.get("event")).append(" - ").append(mapSamples.get("Text on vial")).append("</td>" +
                    " <a href='#' onClick='delSampleUpload(\""+mapSamples.get("sampleToken")+"\",\""+getAssayToken()+"\");return false;'><img src='./images/icon_delete.png' alt='delete sample' /></a>" +
                     "</tr>");
            }
        }

        for(int i=0; i<lstFilenames.size(); i++) {
            String strColor = "#DDEFFF";
            if(i%2==0) {
                strColor = "#FFFFFF";
            }

            String fn = lstFilenames.get(i);

            strReturn.append("<tr>" +
                    "<td class='fs_fontsize' style='width:50%; background-color:").append(strColor).append(";'>").append(fn).append("</td>" + 
                    "<td class='fs_fontsize' style='width:50%; background-color:").append(strColor).append(";'>" + 
                        "<select id='").append(fn).append("' class='select_file' style='width:250px' onChange=\"updateOptions('").append(fn).append("');\">").append(strOptions).append("</select>" + 
                        "<a href='#' id='link_autofill_").append(fn).append("' style='visibility: hidden' onClick=\"autofill('").append(fn).append("'); return false;\"><img src='./images/lightningBolt.png' style='border: 0px;' alt='autofill' /></a>" +
                        "<a href='#' id='link_autoclear_").append(fn).append("' style='visibility: hidden' onClick=\"autoclear('").append(fn).append("'); return false;\"><img src='./images/icon_bin.png' style='border: 0px;' alt='autoclear' /></a>" +
                        " <span id='errorspan_").append(fn).append("' style='visibility: hidden; color:red; font-weight:bold;'>!!!</span>" +
                    "</td></tr>");
        }

        strReturn.append("</table>");

        return strReturn.toString();
    }

    public String getSamplesOverview() {
        
        StringBuilder strRet = new StringBuilder();
        strRet.append("<table class='overviewdet'>");
        
        strRet.append("<tr>" +
                        "<th>Event</th>"+
                        "<th>Start time</th>"+
                        "<th>Subject</th>"+
                        "<th>File</th>"+
                        "</tr>\n");
        if(getAssayToken()!=null && getSessionToken()!=null) {

            GscfService objGSCFService = new GscfService();
            HashMap<String, String> restParams = new HashMap<String, String>();
            restParams.put("assayToken", getAssayToken());
            LinkedList lstGetSamples = objGSCFService.callGSCF2(getSessionToken(),"getSamples",restParams);
            if(lstGetSamples.size()>1) {
                Collections.sort(lstGetSamples, new responseComparator("name"));
                Collections.sort(lstGetSamples, new responseComparator("subject"));
                Collections.sort(lstGetSamples, new responseComparator("startTime"));
                Collections.sort(lstGetSamples, new responseComparator("event"));
            }

            HashMap<String, String> mapFiles = getFilesFromDatabase(getAssayToken());

            while(lstGetSamples.size()>0) {
                HashMap<String, String> mapSamples = (HashMap<String, String>) lstGetSamples.removeFirst();

                String strFile = "<i>no file</i>";
                if(mapFiles.containsKey(mapSamples.get("sampleToken"))) {
                    strFile = mapFiles.get(mapSamples.get("sampleToken"));
                    strFile += " <a href='#' onClick='delSampleOverview(\""+mapSamples.get("sampleToken")+"\",\""+getAssayToken()+"\");return false;'>"
                            + "<img src='./images/icon_delete.png' alt='delete sample' /></a>";
                }
                
                String strStyle = "";
                if(lstGetSamples.size()%2>0) {
                    strStyle = "background-color: #DDD;";
                } 
                
                strRet.append("<tr style='").append(strStyle).append(";'>" +
                        "<td colspan='5'>Samplename: <span class='name'>").append(mapSamples.get("name")).append("</span></td>"+
                        "</tr>");
                strRet.append("<tr style='").append(strStyle).append(";'>" + "<td>").append(mapSamples.get("event")).append("</td>" + "<td>").append(mapSamples.get("startTime")).append("</td>" +
                        "<td>").append(mapSamples.get("subject")).append("</td>" +
                        "<td>").append(strFile).append("</td>"+
                        "</tr>\n");
            }

        }
        strRet.append("</table>");

        return strRet.toString();
    }

    public HashMap<String, String> getFilesFromDatabase(String strAssToken) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        HashMap<String, String> mapFiles = new HashMap<String, String>();
        Query q1 = session.createQuery("from StudySampleAssay where X_REF='"+strAssToken+"'");
        Iterator it1 = q1.iterate();
        while (it1.hasNext()){
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            mapFiles.put(ssa.getSampleToken(), ssa.getNameRawfile());
        }
        session.close();
        sessionFactory.close();

        //Logger.getLogger(getSamples.class.getName()).log(Level.SEVERE, "getFiles: "+mapFiles.size()+" "+strAssToken);

        return mapFiles;
    }

    /**
     * @return the strSessionToken
     */
    public String getSessionToken() {
        return strSessionToken;
    }

    /**
     * @param strSessionToken the strSessionToken to set
     */
    public void setSessionToken(String strSessionToken) {
        this.strSessionToken = strSessionToken;
    }

    /**
     * @return the strAssayToken
     */
    public String getAssayToken() {
        return strAssayToken;
    }

    /**
     * @param strAssayToken the strAssayToken to set
     */
    public void setAssayToken(String assayToken) {
        this.strAssayToken = assayToken;
    }

    /**
     * @return the strFilename
     */
    public String getFilename() {
        return strFilename;
    }

    /**
     * @param strFilename the strFilename to set
     */
    public void setFilename(String strFilename) {
        this.strFilename = strFilename;
    }

    /**
     * @return the blnError
     */
    public boolean getError() {
        return blnError;
    }

}
