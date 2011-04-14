package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Ticket;
import java.util.HashMap;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class setData {

    private String ctdRef;
    private String folder;
    private String password;
    private String studytoken;
    private String sampletokens;

    /***
     * This function tries to collect all data submitted in the upload process,
     * process it and save it to the database. It therefore creates a ticket and
     * converts the filename-sampletoken matchings into a hashmap. Then it creates
     * an getCleanData2 object that handles all data.
     *
     * @return the result of the getCleanData2 object
     */

    public String setData() {

        //Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData INPUT: "+getPassword()+" "+getFolder()+" "+getCdRef()+" "+getStudytoken()+" "+getSampletokens());

        // TODO: add checks if all needed information is set and correct

        String strRet = "";

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration() {}.configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();

        // Create a ticket and store some information
        Ticket objTicket = new Ticket(getCdRef(), getFolder(), getPassword());
        objTicket.setClosed("no");
        session.saveOrUpdate(objTicket);
        session.persist(objTicket);
        tr.commit();
        session.close();

        // Convert the filename-sampletoken matches into a hashmap
        HashMap<String, String> objMatches = new HashMap();
        String[] arrMatches = getSampletokens().split(",");
        for(int i=0; i<arrMatches.length; i=i+2) {
            objMatches.put(arrMatches[i], arrMatches[i+1]);
        }

        // Create an getCleanData2 object and store some information
        getCleanData2 objCommitData = new getCleanData2();
        objCommitData.setCTD_REF(getCdRef());
        objCommitData.setPassword(getPassword());
        objCommitData.setStudytoken(getStudytoken());
        objCommitData.setSampletokens(objMatches);

        try {
            strRet = objCommitData.cleanData();
        } catch (Exception e) {
            strRet = "Saving Failed: "+e.toString()+" \n"+e.getMessage();
            Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData ERROR: Internal Service Error: "+e.toString());
        }

        Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData is er helemaal klaar mee: "+strRet);
        return strRet;
    }

    /**
    * @return the ctdRef
    */
    public String getCdRef() {
        return ctdRef;
    }

    /**
     * @param strCtdRef the ctdRef to set
     */
    public void setCtdRef(String strCtdRef) {
        this.ctdRef = strCtdRef;
    }

    /**
    * @return the folder
    */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
    * @return the password
    */
    public String getPassword() {
        return password;
    }

    /**
     * @param stpasswordrCtdRef the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
    * @return the studytoken
    */
    public String getStudytoken() {
        return studytoken;
    }

    /**
     * @param studytoken the studytoken to set
     */
    public void setStudytoken(String studytoken) {
        this.studytoken = studytoken;
    }

    /**
    * @return the sampletokens
    */
    public String getSampletokens() {
        return sampletokens;
    }

    /**
     * @param sampletokens the sampletokens to set
     */
    public void setSampletokens(String sampletokens) {
        this.sampletokens = sampletokens;
    }
}
