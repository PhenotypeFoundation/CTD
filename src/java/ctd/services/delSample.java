package ctd.services;

import ctd.model.ChipAnnotation;
import ctd.model.Expression;
import ctd.model.StudySampleAssay;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.internal.GscfService;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Tjeerd van Dijk
 */

public class delSample {
    private String strSampleToken;
    private String strAssayToken;
    private String strSessionToken;

    /**
     * This function deletes a sample from the database. If this is the last
     * sample of an assay also all files on disk are deleted.
     *
     * @return either "1" (succes) or "0" (failure)
     */

    public String delSample() {

        // check if the sessiontoken, the sampletoken and the assaytoken are set
        if(getSessionToken()==null){
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): strSessionToken==null");
            return "0";
        }
        if(getSampleToken()==null){
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): strSampleToken==null");
            return "0";
        }
        if(getAssayToken()==null){
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): strAssayToken==null");
            return "0";
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        if(!objGSCFService.isUser(getSessionToken())) {
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): strSessionToken invalid: "+getSessionToken());
            return "0";
        }

        // Check if this user is allowed to delete stuff
        HashMap<String,String> objParam = new HashMap();
        objParam.put("assayToken", getAssayToken());
        try {
            String[] strGSCFRespons = objGSCFService.callGSCF(getSessionToken(), "getAuthorizationLevel", objParam);
            if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canWrite"))) {
                return "0";
            }
        } catch (Exception500InternalServerError ex) {
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): getAuthorizationLevel: "+ex.getError());
        }

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tr2 = session.beginTransaction();

        // init some vars
        int intSampleId = 0;
        int intCount = 0;
        String strCelFile = "";
        ResourceBundle res = ResourceBundle.getBundle("settings");

        // Get all samples with the same assayToken
        // In this loop we want to know:
        //      - What is the id of the record of the sample we want to delete (intSampleId)
        //      - Is this the last sample of an assay (intCount==1)
        Query q = session.createQuery("from StudySampleAssay where XREF='" + getAssayToken() + "'");
        Iterator it1 = q.iterate();
        while (it1.hasNext()){
            StudySampleAssay objSSA = (StudySampleAssay) it1.next();
            if(objSSA.getSampleToken().equals(getSampleToken())) {
                // we found the sample we want to delete
                intSampleId = objSSA.getId();
                strCelFile = objSSA.getNameRawfile();
            }
            intCount++;
            if(intCount>1 && intSampleId>0) {
                // If we found the sample and established that this is not the last
                // sample we can break from the while
                break;
            }
        }

        int iChipsDeleted = 0;
        if(intCount==1) {
            // Delete all chip annotations from the database if this is the last sample
            iChipsDeleted = session.createSQLQuery("DELETE FROM chip_annotation WHERE id IN(SELECT chip_annotation_id FROM expression WHERE study_sample_assay_id=" + intSampleId+")").executeUpdate();
        }
        //Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): Chips deleted: "+iChipsDeleted);

        // Delete all expressions of this sample
        int iExprDeleted = session.createQuery("delete from Expression Expression where studySampleAssayId=" + intSampleId).executeUpdate();
        //Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): Expressions deleted: "+iExprDeleted);

        // Delete this sample
        int iSampleDeleted = session.createQuery("delete from StudySampleAssay StudySampleAssay where id=" + intSampleId).executeUpdate();
        //Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): Samples deleted: "+iSampleDeleted);

        // Delete the .CEL file
        String strPath = res.getString("ws.upload_folder")+getAssayToken()+"/"+strCelFile;
        File celfile = new File(strPath);
        boolean blnCelDeleted = celfile.delete();
        Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): Celfile deleted: "+blnCelDeleted+"<br />"+strPath);

        // close hibernate connection
        tr2.commit();
        session.disconnect();
        sessionFactory.close();

        if(intCount==1) {
            // if this is the last sample of an assay, delete the folder of this
            // assay and all files in it
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "delSample(): Delete files and folder:" + getAssayToken());

            String dirName = res.getString("ws.upload_folder")+getAssayToken();
            File objFolder=new File(dirName);
            File[] arrFiles = objFolder.listFiles();
            for(int i=0; i<arrFiles.length; i++) {
                if(!arrFiles[i].delete()) {
                    Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "delSample(): File deletion failed: "+arrFiles[i].getName());
                }
            }
            if(!objFolder.delete()) {
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "delSample(): Folder deletion failed: "+objFolder.getName());
            }
        }
        
        return "1";
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
     * @param assayToken the strAssayToken to set
     */
    public void setAssayToken(String assayToken) {
        this.strAssayToken = assayToken;
    }

    /**
     * @return the strSampleToken
     */
    public String getSampleToken() {
        return strSampleToken;
    }

    /**
     * @param sampleToken the strSampleToken to set
     */
    public void setSampleToken(String sampleToken) {
        this.strSampleToken = sampleToken;
    }
}
