package ctd.services.internal;

import ctd.services.exceptions.Exception307TemporaryRedirect;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
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

    /**
     * This function generates the table that is shown on the "My Studies" page
     * @return a String containing HTML
     * @throws Exception307TemporaryRedirect if the user is not logged in a redirect is thrown
     */

    public String Overview() throws Exception307TemporaryRedirect {
        String strRet = "";

        // Create a GSCF service
        GscfService objGSCFService = new GscfService();
        
        // Load the CTD settings
        ResourceBundle res = ResourceBundle.getBundle("settings");
        
        //Check if the user is logged in
        if(!objGSCFService.isUser(getSessionToken())) {
            String urlAuthRemote = objGSCFService.urlAuthRemote(getSessionToken(), res.getString("ctd.moduleURL")+"/index.jsp?p=overview");
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

        // Call GSCF in order to get all assays of this module and user
        LinkedList lstGetAssays = objGSCFService.callGSCF2(getSessionToken(),"getAssays",null);

        // Init params
        String strStudyQuery = "";
        String strStudyCall = "";
        Map mapAssayNames = new HashMap();

        // if this user doesn't have any assays, the empty string is returned
        if(lstGetAssays.size()==0) {
            return "";
        }

        // We need some data of each assay in order to be able to query the database
        for(int i=0; i<lstGetAssays.size(); i++) {
            HashMap<String, String> objMap = (HashMap) lstGetAssays.get(i);

            // Get all studyTokens (comma seperated) for the database query
            if(!strStudyQuery.equals("")) strStudyQuery += ",";
            strStudyQuery += "'"+objMap.get("parentStudyToken")+"'";

            // Get all studyTokens for the REST call
            if(!strStudyCall.equals("")) strStudyCall += "&studyToken=";
            strStudyCall += objMap.get("parentStudyToken");

            // Save the assay names and tokens in a map for future use
            mapAssayNames.put(objMap.get("assayToken"), objMap.get("name"));
        }

        // Call GSCF in order to get info on all the studies we have
        HashMap<String, String> objParam = new HashMap();
        objParam.put("studyToken",strStudyCall);
        LinkedList lstGetStudies = objGSCFService.callGSCF2(getSessionToken(),"getStudies",objParam);

        // For each studytoken, we want to know which name to attach
        Map mapStudyNames = new HashMap();
        for(int i=0; i<lstGetStudies.size(); i++) {
            HashMap<String, String> objMap = (HashMap) lstGetStudies.get(i);
            // add the token and name to he map
            mapStudyNames.put(objMap.get("studyToken"), objMap.get("title"));
        }

        // create a clause for the studytokens
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
                            +" "+strOffset+"";
        
        SQLQuery sql = session.createSQLQuery(strQuery);
        Iterator it2 = sql.list().iterator();
        int iRownr = 1;

        // the gscf url in order to be able to refer to study and assay details
        String strGscfHome = res.getString("gscf.baseURL");
        LinkedList<String> lstRijen = new LinkedList<String>();
        while (it2.hasNext()) {
            Object[] data = (Object[]) it2.next();

            // strClass is used to give even and odd rows a different background color
            String strClass = "odd";
            if(iRownr%2==0) strClass = "even";

            // Collect stats on the number of files that have been uploaded for an assay
            String strExprCount = "0";
            if(data[3]!=null) {
                strExprCount = data[3].toString();
            }

            // Count rows for odd and even style classes
            iRownr++;

            // Create a line in the table
            String strLine = (String)mapStudyNames.get((String)data[1])+(String)mapAssayNames.get((String)data[0])+"!!SEP!!<tr class=\""+strClass+"\">\n";
            strLine += "\t<td class=\"tdoverview\">"+mapAssayNames.get((String)data[0])+" (<a href='"+strGscfHome+"/assay/showByToken/"+(String)data[0]+"'>details in GSCF</a>)</td>\n";
            strLine += "\t<td class=\"tdoverview\">"+mapStudyNames.get((String)data[1])+" (<a href='"+strGscfHome+"/study/showByToken/"+(String)data[1]+"'>details in GSCF</a>)</td>\n";
            strLine += "\t<td class=\"tdoverview\"><span id='numsamp"+(String)data[0]+"'>"+data[2].toString()+"</span>&nbsp;(<a href='#' onClick='showOverviewDetails(\""+(String)data[0]+"\")'>more info</a>)\n";
            strLine += "</tr>\n";
            strLine += "<tbody id=\""+(String)data[0]+"HiddenRows\" style='display: none;'><tr><td class=\"tdoverview tdoverviewdetails\" colspan='4' id=\""+(String)data[0]+"Details\"><img src='./images/wait.gif' /></td></tr></tbody>\n";
            lstRijen.add(strLine);
        }

        // This ugly hack is implemented in order to be able to order the rows alphabeticly
        for(int i=0; i<lstRijen.size(); i++) {
            String[] arrLine = lstRijen.get(i).split("!!SEP!!");
            strRet += arrLine[1];
        }

        // close hibernate session
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
