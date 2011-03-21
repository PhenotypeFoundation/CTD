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

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import ctd.services.getTicket;
import ctd.services.exceptions.*;
import ctd.services.internal.GscfService;
import ctd.ws.model.ProbeSetExpression;
import ctd.ws.model.ExpressionProbesetSample;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
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

    public String[] getMeasurementData() throws NoImplementationException, SerializerException, Exception401Unauthorized, Exception500InternalServerError, Exception403Forbidden, Exception400BadRequest, Exception404ResourceNotFound {
/*
        if(strAssayToken==null || strSessionToken==null){
            throw new Exception400BadRequest();
        }

        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",null);
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            throw new Exception403Forbidden();
        }

        HashMap<String,String> objParam = new HashMap();
        objParam.put("assayToken", strAssayToken);
        strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }*/

        String[] strReturn = new String [2];
        ArrayList<Object> total = new ArrayList<Object>();
        ArrayList<String> lstSampleToken = new ArrayList<String>();
        ArrayList<String> lstMeasurementToken = new ArrayList<String>();
        ArrayList<Double> lstExpressions = new ArrayList<Double>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        String strMeasurementQuery = "";
        if(!getMeasurementToken().equals("")) {
            strMeasurementQuery += " AND ca.probeset IN(" + getMeasurementToken() + ") ";
        }

        String strSampleQuery = "";
        if(!getSampleToken().equals("")) {
            strSampleQuery += " AND ssa.sample_token IN(" + getSampleToken() + ") ";
        }

//        Ticket ticket = null;
//        Integer ssa_id = null;
//        Query q = session.createQuery("from Ticket where password='" + getSessionToken() + "'");
//        ticket = (Ticket) q.uniqueResult();
//
//        Iterator it1 = ticket.getStudySampleAssaies().iterator();
//        while (it1.hasNext()) {
//            StudySampleAssay ssa = (StudySampleAssay) it1.next();
//            String name = ssa.getXREF();
//            if (name.equals(getAssayToken())) {
//                Integer id = ssa.getId();
//                assayids.add(id);
//                assaynames.add(name);
//            }
//        }
//
//        Integer count = 0;
//        for (int i = 0; i < assayids.size(); i++) {
//            count++;
//            Integer assay_id = assayids.get(i);
        String strQuery = "SELECT ex.expression,ca.probeset,ssa.sample_token"
                        + " FROM expression ex,chip_annotation ca,study_sample_assay ssa"
                        + " WHERE ssa.X_REF='" + getAssayToken() +"'"
                        + " AND ex.chip_annotation_id=ca.id"
                        + " AND ex.study_sample_assay_id=ssa.id"
                        + strSampleQuery + strMeasurementQuery
                        + " ORDER BY ssa.sample_token ASC, ca.probeset ASC;";
        SQLQuery sql = session.createSQLQuery(strQuery);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Q: "+strQuery);
        Iterator it2 = sql.list().iterator();
        if(!blnVerbose) {
            HashMap<String, String> mapSampleToken = new HashMap<String, String>();
            HashMap<String, String> mapMeasurementToken = new HashMap<String, String>();
            while (it2.hasNext()) {
                Object[] data = (Object[]) it2.next();
                Double value = (Double) data[0];
                String sMeasurementToken = (String) data[1];
                String sSampleToken = (String) data[2];

                if(!mapSampleToken.containsKey(sSampleToken)) {
                    mapSampleToken.put(sSampleToken, "");
                    lstSampleToken.add(sSampleToken);
                }
                if(!mapMeasurementToken.containsKey(sMeasurementToken)) {
                    mapMeasurementToken.put(sMeasurementToken, "");
                    lstMeasurementToken.add(sMeasurementToken);
                }
                lstExpressions.add(value);
            }
            if(lstExpressions.size()>0) {
                total.add(lstSampleToken);
                total.add(lstMeasurementToken);
                total.add(lstExpressions);
            }
        } else {
            while (it2.hasNext()) {
                Object[] data = (Object[]) it2.next();
                Double value = (Double) data[0];
                String sMeasurementToken = (String) data[1];
                String sSampleToken = (String) data[2];

                ExpressionProbesetSample objNew = new ExpressionProbesetSample();
                objNew.setMeasurementToken(sMeasurementToken);
                objNew.setSampleToken(sSampleToken);
                objNew.setValue(value);
                total.add(objNew);
            }
        }

        session.close();

        if(total.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }

        strReturn[0] = "200";
        strReturn[1] = trans.serializeToJsonString(total);

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
     * @return the strMeasurementToken
     */
    public String getMeasurementToken() {
        String strRet = "";
        for(int i=0; i<strMeasurementToken.size(); i++) {
            if(!strRet.equals("")) {
                strRet += ",";
            }
            strRet += "'" + strMeasurementToken.get(i) + "'";
        }
        return strRet;
    }

    /**
     * @param strMeasurementToken the strMeasurementToken to set
     */
    public void setMeasurementToken(String measurementToken) {
        this.strMeasurementToken.add(measurementToken);
    }

    /**
     * @return the strSampleToken
     */
    public String getSampleToken() {
        String strRet = "";
        for(int i=0; i<strSampleToken.size(); i++) {
            if(!strRet.equals("")) {
                strRet += ",";
            }
            strRet += "'" + strSampleToken.get(i) + "'";
        }
        return strRet;
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
