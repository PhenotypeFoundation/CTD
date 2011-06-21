/* Copyright 2010 Wageningen University, Division of Human Nutrition.
 * Drs. R. Kerkhoven, robert.kerkhoven@wur.nl
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
package ctd.services;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.*;
import ctd.services.internal.GscfService;
import ctd.ws.model.ExpressionProbesetSample;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author kerkh010
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getMeasurementData {

    private String strSessionToken;
    private String strAssayToken;
    private LinkedList<String> strMeasurementToken = new LinkedList<String>();
    private LinkedList<String> strSampleToken = new LinkedList<String>();
    private boolean blnVerbose = false;

    /**
     * This function is used by the REST service getMeasurementData to create it's response
     *
     * @return a String in JSON format
     *
     * @throws Exception401Unauthorized This exception is thrown if GSCF indicates that a user is not authorized (getAuthorizationLevel)
     * @throws Exception500InternalServerError This exception is thrown when there is an error in this code
     * @throws Exception403Forbidden This exception is thrown if GSCF indicates that a user is not logged in (isUser)
     * @throws Exception404ResourceNotFound This exception is thrown when the requested assay isn't present in the database
     * @throws Exception400BadRequest This exception is thrown when not enough paremeters are set
     */

    public String[] getMeasurementData() throws Exception401Unauthorized, Exception500InternalServerError, Exception403Forbidden, Exception400BadRequest, Exception404ResourceNotFound {

        // Check if the minimal parameters are set
        if(strAssayToken==null || strSessionToken==null){
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        if(!objGSCFService.isUser(getSessionToken())) {
            throw new Exception403Forbidden();
        }

        //open hibernate connection
        Configuration objConf = new Configuration().configure();
        SessionFactory sessionFactory = objConf.buildSessionFactory();
        Session session = sessionFactory.openSession();

        // Check if the provided sessionToken has access to the provided assayToken
        // This needs to be verified with the studyToken
        String strQ = "SELECT DISTINCT study_token FROM study_sample_assay WHERE X_REF ='"+getAssayToken()+"'";
        SQLQuery sql = session.createSQLQuery(strQ);
        String strStudyToken = "";
        Iterator it1 = sql.list().iterator();
        while (it1.hasNext()) {
            strStudyToken = (String) it1.next();
        }

        // If no study can be found a 404 is thrown
        if(strStudyToken.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Get the authorization level of a user for a certain study
        HashMap<String,String> objParam = new HashMap();
        objParam.put("studyToken", strStudyToken);
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }

        // init parameters
        String[] strReturn = new String [2];
        ArrayList<Object> total = new ArrayList<Object>();
        ArrayList<String> lstSampleToken = new ArrayList<String>();
        ArrayList<String> lstMeasurementToken = new ArrayList<String>();
        ArrayList<Double> lstExpressions = new ArrayList<Double>();

        // If the optional parameter measurementToken is set, then we prepare
        // an extra condition for the query
        String strMeasurementQuery = getMeasurementToken();
        if(!strMeasurementQuery.isEmpty()) {
            strMeasurementQuery = " AND ca.probeset IN(" + strMeasurementQuery + ") ";
        }

        // If the optional parameter sampleToken is set, then we prepare
        // an extra condition for the query
        String strSampleQuery = getSampleToken();
        if(!strSampleQuery.isEmpty()) {
            strSampleQuery = " AND ssa.sample_token IN(" + strSampleQuery + ") ";
        }

        // Create the SQL query
        String strQuery2 = "SELECT ex.expression,ca.probeset,ssa.sample_token"
                        + " FROM expression ex,chip_annotation ca,study_sample_assay ssa"
                        + " WHERE ssa.X_REF='" + getAssayToken() +"'"
                        + " AND ex.chip_annotation_id=ca.id"
                        + " AND ex.study_sample_assay_id=ssa.id"
                        + strSampleQuery + strMeasurementQuery
                        + " ORDER BY ssa.sample_token ASC, ca.probeset ASC;";

        SQLQuery sql2 = session.createSQLQuery(strQuery2);
        Iterator it2 = sql2.list().iterator();
        if(!blnVerbose) {
            // If the Verbose parameter is false or not set, then the first
            // line of the JSON should be all sampleTokens, the second line
            // all measurementTokens and the third line all values

            // These hashmaps are used to make sure that a token is only reported once
            HashMap<String, String> mapSampleToken = new HashMap<String, String>();
            HashMap<String, String> mapMeasurementToken = new HashMap<String, String>();
            while (it2.hasNext()) {
                Object[] data = (Object[]) it2.next();
                Double value = (Double) data[0];
                String sMeasurementToken = (String) data[1];
                String sSampleToken = (String) data[2];

                if(!mapSampleToken.containsKey(sSampleToken)) {
                    // if this is the first occurence of a sampleToken
                    mapSampleToken.put(sSampleToken, "");
                    lstSampleToken.add(sSampleToken);
                }
                if(!mapMeasurementToken.containsKey(sMeasurementToken)) {
                    // if this is the first occurence of a measurementToken
                    mapMeasurementToken.put(sMeasurementToken, "");
                    lstMeasurementToken.add(sMeasurementToken);
                }
                // add the expression
                lstExpressions.add(value);
            }
            if(lstExpressions.size()>0) {
                // if the query returned results lstExpressions should be
                // bigger than zero
                total.add(lstSampleToken);
                total.add(lstMeasurementToken);
                total.add(lstExpressions);
            }
        } else {
            // if the parameter Verbose is set to true every line in the JSON
            // message should report a sampleToken, a measurementToken and a
            // value
            while (it2.hasNext()) {
                // Get the data
                Object[] data = (Object[]) it2.next();
                Double value = (Double) data[0];
                String sMeasurementToken = (String) data[1];
                String sSampleToken = (String) data[2];

                // Create a ExpressionProbeSample which represents a line in the JSON
                ExpressionProbesetSample objNew = new ExpressionProbesetSample();
                objNew.setMeasurementToken(sMeasurementToken);
                objNew.setSampleToken(sSampleToken);
                objNew.setValue(value);

                // Add the ExpressionProbeSample to a map
                total.add(objNew);
            }
        }

        // Close hibernate session
        session.close();
        // If no data is found, then a 404 is thrown
        if(total.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Use SKARINGA to transform the results into a valide JSON message
        ObjectTransformer trans = null;
        strReturn[1] = "";
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            strReturn[1] = trans.serializeToJsonString(total);
        } catch (Exception ex) {
            throw new Exception500InternalServerError("SKARINGA ERROR getMeasurementData: "+ex.getLocalizedMessage());
        }

        // HTTP response code 200 means 'OK'
        strReturn[0] = "200";

        return strReturn;
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
     * The measurementTokens are collected in a list. This function transformes
     * this list to a String
     * @return the strMeasurementToken
     */
    public String getMeasurementToken() {
        StringBuffer strRet = new StringBuffer();
        strRet.append("");

        // Boolean used to check for the first item
        boolean hasMeasurementToken = false;

         for(int i=0; i<strMeasurementToken.size(); i++) {
            if(hasMeasurementToken) {
                // Seperate items with a comma
                strRet.append( "," );
            } else {
                hasMeasurementToken = true;
            }

            // Add the measurementtokens (with surrounding quotes)
            strRet.append( "'" ).append( strMeasurementToken .get(i) ).append( "'" );
        }
        return strRet.toString();
    }

    /**
     * @param strMeasurementToken the strMeasurementToken to set
     */
    public void setMeasurementToken(String measurementToken) {
        this.strMeasurementToken.add(measurementToken);
    }

    /**
     * The sampleTokens are collected in a list. This function transformes
     * this list to a String
     * @return the strSampleToken
     */
    public String getSampleToken() {
        StringBuffer strRet = new StringBuffer();
        strRet.append("");

        // Boolean used to check for the first item
        boolean hasSampleToken = false;

         for(int i=0; i<strSampleToken.size(); i++) {
            if(hasSampleToken) {
                // Seperate items with a comma
                strRet.append( "," );
            } else {
                hasSampleToken = true;
            }

            // Add the measurementtokens (with surrounding quotes)
            strRet.append( "'" ).append( strSampleToken .get(i) ).append( "'" );
        }
        return strRet.toString();
    }

    /**
     * @param strSampleToken the strSampleToken to set
     */
    public void setSampleToken(String sampleToken) {
        this.strSampleToken.add(sampleToken);
    }

    /**
     * @return the blnVerbose
     */
    public boolean getVerbose() {
        return blnVerbose;
    }

    /**
     * @param blnVerbose the blnVerbose to set
     */
    public void setVerbose(boolean blnVerbose) {
        this.blnVerbose = blnVerbose;
    }
}
