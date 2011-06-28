package ctd.services;

import ctd.model.StudySampleAssay;
import ctd.services.exceptions.Exception307TemporaryRedirect;
import ctd.services.exceptions.Exception400BadRequest;
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
import java.util.ResourceBundle;
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
     * This function generates the table that is shown in step 4 of the upload process
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

        // Init a GSCF-service and load the settings
        GscfService objGSCFService = new GscfService();
        ResourceBundle res = ResourceBundle.getBundle("settings");

        // Check if the provided sessionToken is valid
        if(!objGSCFService.isUser(getSessionToken())) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken invalid: "+getSessionToken());
            throw new Exception403Forbidden();
        }

        // Get all samples that are linked to this assay
        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("assayToken", getAssayToken());
        LinkedList lstGetSamples = objGSCFService.callGSCF2(getSessionToken(),"getSamples",restParams);
        
        // Sort the samples by name
        Collections.sort(lstGetSamples, new responseComparator("name"));

        // Create the table and headers
        strReturn.append("<table style='width:100%'>");
        strReturn.append("<tr class='fs_th'><th>Filenames</th><th>Samplenames <span class='fs_fontsize'>[<a href='#' onClick='resetall(); return false;'>clear all</a>]</span></th></tr>");

        // Get a map of assayTokens and corresponding files that are already present in the database
        HashMap<String, String> mapFiles = getFilesFromDatabase(getAssayToken());

        // init a linked list for files
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
                    // Seperate the filenames the ugly way
                    if(!strUglyHack.equals("")) strUglyHack += "!!SEP!!";
                    // Add a filename
                    strUglyHack += zipEntryName;
                }
            }
            // Split the filenames to an array
            String[] arrFiles = strUglyHack.split("!!SEP!!");
            // Sort the array (this is why we needed an ugly hack
            Arrays.sort(arrFiles);
            // Add the sorted array to the linked list
            lstFilenames.addAll(Arrays.asList(arrFiles));

        } catch (IOException e) {
            Logger.getLogger(getSamples.class.getName()).log(Level.SEVERE, "ERROR getSamples: "+e.getMessage());
        }

        // If we have less samples available then we have files uploaded
        // we return an error message and set a boolean (for the GUI to know that there was an error)
        if(lstGetSamples.size()<lstFilenames.size()) {
            blnError = true;
            return "<b>There are more files in the submitted .zip than there are available samples.</b><br />Go to the study in GSCF (<a href='"+res.getString("gscf.baseURL")+"/assay/showByToken/"+getAssayToken()+"'>link</a>) and add more samples.<br />";
        }
        // If the uploaded zip was empty
        // we return an error message and set a boolean (for the GUI to know that there was an error)
        if(lstFilenames.size()==0) {
            blnError = true;
            return "<b>There are either no files in the submitted .zip, or the .zip is corrupted.</b><br/>No data has been processed!</br>Please make sure your .zip contains cel-files and is readable before you upload it.<br />";
        }

        // Create the options for the SELECTS
        String strOptions = "<option value='none'>Select a sample for this file...</option>";
        while(lstGetSamples.size()>0) {
            // Get the first sample (samples are ordered)
            HashMap<String, String> mapSamples = (HashMap<String, String>) lstGetSamples.removeFirst();
            if(!mapFiles.containsKey(mapSamples.get("sampleToken"))) {
                // If there is no file already linked to this sample
                strOptions += "<option value='"+mapSamples.get("sampleToken")+"'>"+mapSamples.get("name")+" - "+mapSamples.get("event")+" - "+mapSamples.get("Text on vial")+"</option>";
            } else {
                // Generate a table line with the filename, the possibility to remove the matching and the samplename
                strReturn.append("<tr><td class='fs_fontsize' style='width:50%; background-color:#CCC;'>")
                        .append(mapFiles.get(mapSamples.get("sampleToken")))
                        .append(" <a href='#' onClick='delSampleUpload(\"")
                        .append(mapSamples.get("sampleToken"))
                        .append("\",\"")
                        .append(getAssayToken())
                        .append("\");return false;'><img src='./images/icon_delete.png' alt='delete sample' /></a>")
                    .append("</td><td class='fs_fontsize' style='width:50%; background-color:#CCC;'>")
                        .append(mapSamples.get("name"))
                        .append(" - ")
                        .append(mapSamples.get("event"))
                        .append(" - ")
                        .append(mapSamples.get("Text on vial"))
                    .append("</td></tr>");
                // Remove the file from the list of uploaded files to prevent double matchings
                lstFilenames.removeFirstOccurrence(mapFiles.get(mapSamples.get("sampleToken")));
            }
        }

        // Loop through all remaining files
        for(int i=0; i<lstFilenames.size(); i++) {

            // Alternate the background color of the rows
            String strColor = "#DDEFFF";
            if(i%2==0) {
                strColor = "#FFFFFF";
            }

            // Get the filename
            String strFilename = lstFilenames.get(i);

            // Generate a table line with the filename and a SELECT containing the sampleoptions
            // Also add the lines for autofill, autoclear and error
            strReturn.append("<tr>" +
                    "<td class='fs_fontsize' style='width:50%; background-color:").append(strColor).append(";'>").append(strFilename).append("</td>" +
                    "<td class='fs_fontsize' style='width:50%; background-color:").append(strColor).append(";'>" + 
                        "<select id='").append(strFilename).append("' class='select_file' style='width:250px' onChange=\"updateOptions('").append(strFilename).append("');\">").append(strOptions).append("</select>" +
                        "<a href='#' id='link_autofill_").append(strFilename).append("' style='visibility: hidden' onClick=\"autofill('").append(strFilename).append("'); return false;\"><img src='./images/lightningBolt.png' style='border: 0px;' alt='autofill' /></a>" +
                        "<a href='#' id='link_autoclear_").append(strFilename).append("' style='visibility: hidden' onClick=\"autoclear('").append(strFilename).append("'); return false;\"><img src='./images/icon_bin.png' style='border: 0px;' alt='autoclear' /></a>" +
                        " <span id='errorspan_").append(strFilename).append("' style='visibility: hidden; color:red; font-weight:bold;'>!!!</span>" +
                    "</td></tr>");
        }

        // Close the table
        strReturn.append("</table>");

        return strReturn.toString();
    }

    /**
     * This function enables the blnDelete option of getSamplesOverview to be empty
     * @return a String containing the table
     * @throws Exception307TemporaryRedirect if the user isn't logged in
     */
    public String getSamplesOverview() throws Exception307TemporaryRedirect {
        // Use blnDelete=true as default
        return getSamplesOverview(true);
    }

    /**
     * This function generated the table that is shown in de details pannel in
     * the overview (my studies) screen.
     *
     * @param blnDelete a boolean that indicates if delete buttons need to be printed
     * @return a String containing the table
     * @throws Exception307TemporaryRedirect if the user isn't logged in
     */
    public String getSamplesOverview(boolean blnDelete) throws Exception307TemporaryRedirect {
        
        StringBuilder strRet = new StringBuilder();

        // Add the table and the table header
        strRet.append("<table class='overviewdet'>");
        strRet.append("<tr>" +
                        "<th>Event</th>"+
                        "<th>Start time</th>"+
                        "<th>Subject</th>"+
                        "<th>File</th>"+
                        "</tr>\n");

        // If the the assayToken and sessionToken are provided
        if(getAssayToken()!=null && getSessionToken()!=null) {

            // Init a GSCF service
            GscfService objGSCFService = new GscfService();

            // Check id the user is authenticated
            if(!objGSCFService.isUser(getSessionToken())) {
                ResourceBundle res = ResourceBundle.getBundle("settings");
                String urlAuthRemote = objGSCFService.urlAuthRemote(getSessionToken(), res.getString("ctd.moduleURL")+"/assay/showByToken/"+getAssayToken());
                //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "REDIRECT in getSamples: "+urlAuthRemote);
                throw new Exception307TemporaryRedirect(urlAuthRemote);
            }

            // Get all samples that are linked to this assay
            HashMap<String, String> restParams = new HashMap<String, String>();
            restParams.put("assayToken", getAssayToken());
            LinkedList lstGetSamples = objGSCFService.callGSCF2(getSessionToken(),"getSamples",restParams);

            // If the call for information didn't yield results return the empty string
            if(lstGetSamples==null) {
                return "";
            }

            // If the list of samples enables sorting
            if(lstGetSamples.size()>1) {
                Collections.sort(lstGetSamples, new responseComparator("name"));
                Collections.sort(lstGetSamples, new responseComparator("subject"));
                Collections.sort(lstGetSamples, new responseComparator("startTime"));
                Collections.sort(lstGetSamples, new responseComparator("event"));
            }

            // init a linked list for files
            HashMap<String, String> mapFiles = getFilesFromDatabase(getAssayToken());

            // As long as there are samples available
            while(lstGetSamples.size()>0) {

                // Get a sample
                HashMap<String, String> mapSamples = (HashMap<String, String>) lstGetSamples.removeFirst();

                // Check if there is a file for this sample. If so, enable deletion
                String strFile = "<i>no file</i>";
                if(mapFiles.containsKey(mapSamples.get("sampleToken"))) {
                    strFile = mapFiles.get(mapSamples.get("sampleToken"));
                    if(blnDelete) {
                        // If it is requested a Delete button is shown
                        strFile += " <a href='#' onClick='delSampleOverview(\""+mapSamples.get("sampleToken")+"\",\""+getAssayToken()+"\");return false;'>"
                                + "<img src='./images/icon_delete.png' alt='delete sample' /></a>";
                    }
                }

                // Lines should have alternating colour
                String strStyle = "";
                if(lstGetSamples.size()%2>0) {
                    strStyle = "background-color: #DDD;";
                } 

                // Generate a line in the table
                strRet.append("<tr style='").append(strStyle).append("'>" +
                        "<td colspan='5'>Samplename: <span class='name'>").append(mapSamples.get("name")).append("</span></td>"+
                        "</tr>");
                strRet.append("<tr style='").append(strStyle).append("'>" +
                        "<td>").append(mapSamples.get("event")).append("</td>" +
                        "<td>").append(mapSamples.get("startTime")).append("</td>" +
                        "<td>").append(mapSamples.get("subject")).append("</td>" +
                        "<td>").append(strFile).append("</td>"+
                        "</tr>\n");
            }

        }
        // Close the table
        strRet.append("</table>");
        
        return strRet.toString();
    }

    /**
     *  This function creates a hashmap that linkes all available files of an assay
     *  to sampletokens
     *
     * @param strAssToken The assayToken that we want to check
     * @return The hashmap containing files <-> samples
     */

    public HashMap<String, String> getFilesFromDatabase(String strAssToken) {

        // Create a sessionfactory
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        // Init returnvariable
        HashMap<String, String> mapFiles = new HashMap<String, String>();

        // Get all available assayinformation from the database and iterate
        Query q1 = session.createQuery("from StudySampleAssay where X_REF='"+strAssToken+"'");
        Iterator it1 = q1.iterate();
        while (it1.hasNext()){
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            // Put the sampleTokens and filenames in the list
            mapFiles.put(ssa.getSampleToken(), ssa.getNameRawfile());
        }

        // Close the session
        session.close();
        sessionFactory.close();

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
