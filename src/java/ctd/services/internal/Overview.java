package ctd.services.internal;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.Exception307TemporaryRedirect;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.getTicket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * This class is used to generate the HTML formatted rows of the overview page
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class Overview {

    private String strOffset;

    String strSessionToken = "";

    public String Overview() throws Exception307TemporaryRedirect {
        String strRet = "";

        //Check if the user is logged in
        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = new String[2];
        ResourceBundle res = ResourceBundle.getBundle("settings");
        HashMap<String, String> restParams = new HashMap<String, String>();
        String strConsumerVal = res.getString("ctd.consumerID");
        try {
            restParams.put("consumer", strConsumerVal);
            strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"isUser",restParams);
        } catch (Exception500InternalServerError e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "OVERVIEW ERROR (isUser): "+e.getError());
        }
        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "isUser Response: "+strGSCFRespons[1]+" "+objGSCFService.isUser(strGSCFRespons[1]));
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            String urlAuthRemote = objGSCFService.urlAuthRemote(getSessionToken(), res.getString("ctd.moduleURL")+"index.jsp?p=overview");
            throw new Exception307TemporaryRedirect(urlAuthRemote);
        }

        // offset is not yet implemented in overview.jsp, but it can be used to
        // split the overview into multiple pages.
        // Now only the first 20 results are given
        if(getOffset()!=null) {
            strOffset = " LIMIT "+strOffset +",20";
        } else {
            strOffset = "";
        }
        strOffset = "";

        // Get all studies a user has access to
        restParams.clear();
        restParams.put("consumer", strConsumerVal);
        try {
            strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"getStudies",restParams);
        } catch (Exception500InternalServerError e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "OVERVIEW ERROR (getStudies): "+e.getError());
        }
        ObjectTransformer trans = null;
        LinkedList objJSON = null;
        String strStudyQuery = "";
        Map mapStudyNames = new HashMap();
        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "OVERVIEW RESPONSE "+strGSCFRespons[1]);
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            objJSON = (LinkedList) trans.deserializeFromJsonString(strGSCFRespons[1]);
        } catch (Exception e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "OVERVIEW ERROR (JSON): "+e.getLocalizedMessage());
        }
        
        for(int i=0; i<objJSON.size(); i++) {
            HashMap<String, String> objMap = (HashMap) objJSON.get(i);
            if(!strStudyQuery.equals("")) strStudyQuery += ",";
            strStudyQuery += "'"+objMap.get("studyToken")+"'";
            //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "EntrySET: "+objMap.entrySet().toString());
            mapStudyNames.put(objMap.get("studyToken"), objMap.get("title"));
            //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "ADDED TO MAP: "+objMap.get("studyToken")+" "+objMap.get("title"));
        }
  
        if(!strStudyQuery.equals("")) strStudyQuery = " WHERE a.study_token IN(" + strStudyQuery + ") ";

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        // Select study_tokens, assay_tokens, the number of rows in study_sample_assay
        // with these 2 keys and the number of rows in expression with these keys
        String strQuery = "SELECT a.X_REF, a.study_token, COUNT(a.id),"
                                    +" (SELECT 1 FROM expression b "
                                    +" WHERE b.study_sample_assay_id=a.id "
                                    +" GROUP BY b.study_sample_assay_id) AS totaal "
                            +" FROM study_sample_assay a"
                            +" "+strStudyQuery
                            +" GROUP BY a.X_REF"
                            +" ORDER BY X_REF "+strOffset+"";
        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "OVERVIEW QUERY: "+strQuery);
        SQLQuery sql = session.createSQLQuery(strQuery);
        Iterator it2 = sql.list().iterator();
        int iRownr = 1;

        // the gscf url in order to be able to refer to study and assay details
        String strGscfHome = res.getString("gscf.baseURL");

        while (it2.hasNext()) {
            Object[] data = (Object[]) it2.next();

            // strClass is used to give even and odd rows a different background color
            String strClass = "odd";
            if(iRownr%2==0) strClass = "even";

            String strExprCount = "0";
            if(data[3]!=null) {
                strExprCount = data[3].toString();
            }

            iRownr++;

            strRet += "<tr class=\""+strClass+"\">\n";
            strRet += "\t<td class=\"tdoverview\"><a href='"+strGscfHome+"/assay/showByToken/"+(String)data[0]+"'>"+objGSCFService.getAssayName((String)data[0],(String)data[1],getSessionToken())+"</a></td>\n";
            strRet += "\t<td class=\"tdoverview\"><a href='"+strGscfHome+"/study/showByToken/"+(String)data[1]+"'>"+mapStudyNames.get((String)data[1])+"</a></td>\n";
            strRet += "\t<td class=\"tdoverview\">"+data[2].toString()+" ("+strExprCount+")</td>\n";
            strRet += "</tr>\n";
        }
        session.close();

        return strRet;
    }

     /**
     * This function calls the constructor Overview() of this class and returns the result
     * @return a String containing the table rows
     */
    public String getContent() throws Exception307TemporaryRedirect {
        return this.Overview();
    }

    /**
     * @return the strOffset
     */
    public String getOffset() {
        return strOffset;
    }

    /**
     * @param strOffset the strOffset to set
     */
    public void setOffset(String strOffset) {
        this.strOffset = strOffset;
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
}
