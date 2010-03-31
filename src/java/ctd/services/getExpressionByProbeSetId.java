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
import ctd.ws.model.ProbeSetExpressionInfo;
import java.util.ArrayList;
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
public class getExpressionByProbeSetId {

    private String probeSetId;
    private String password;

    public String getExpressionByProbeSetId() throws NoImplementationException, SerializerException {

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        String message = "";
        ArrayList<ProbeSetExpressionInfo> pseia = new ArrayList<ProbeSetExpressionInfo>();


        if (webservice_password.contains(getPassword())) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            String psi = getProbeSetId();
            SQLQuery q = session.createSQLQuery("Select chip.name,expression.expression,study_sample_assay.X_REF,study_sample_assay.average,study_sample_assay.std,ticket.password FROM chip,chip_annotation,expression,study_sample_assay,ticket WHERE chip.id=chip_annotation.chip_id AND probeset='" + psi + "' AND chip_annotation.id=expression.chip_annotation_id AND expression.study_sample_assay_id=study_sample_assay.id AND study_sample_assay.ticket_id=ticket.id");

            Iterator it1 = q.list().iterator();
            while (it1.hasNext()) {
                ProbeSetExpressionInfo psei = new ProbeSetExpressionInfo();
                Object[] data = (Object[]) it1.next();
                String chip_name = (String) data[0];
                Double log2value = (Double) data[1];
                String local_accession = (String) data[2];
                Double average = (Double) data[3];
                Double std = (Double) data[4];
                String password = (String) data[5];

                psei.setChipName(chip_name);
                psei.setLog2Value(log2value);
                psei.setLocalAccession(local_accession);
                psei.setSTD(std);
                psei.setAverage(average);
                psei.setTicketPassword(password);

                pseia.add(psei);
            }

            session.close();
        }
        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToString(pseia);
        return message;
    }

    public ArrayList<ProbeSetExpressionInfo> getExpressionByProbeSetIdInternal() throws NoImplementationException, SerializerException {

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        String message = "";
        ArrayList<ProbeSetExpressionInfo> pseia = new ArrayList<ProbeSetExpressionInfo>();


        if (webservice_password.contains(getPassword())) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            String psi = getProbeSetId();
            SQLQuery q = session.createSQLQuery("Select chip.name,expression.expression,study_sample_assay.X_REF,study_sample_assay.average,study_sample_assay.std,ticket.password FROM chip,chip_annotation,expression,study_sample_assay,ticket WHERE chip.id=chip_annotation.chip_id AND probeset='" + psi + "' AND chip_annotation.id=expression.chip_annotation_id AND expression.study_sample_assay_id=study_sample_assay.id AND study_sample_assay.ticket_id=ticket.id");

            Iterator it1 = q.list().iterator();
            while (it1.hasNext()) {
                ProbeSetExpressionInfo psei = new ProbeSetExpressionInfo();
                Object[] data = (Object[]) it1.next();
                String chip_name = (String) data[0];
                Double log2value = (Double) data[1];
                String local_accession = (String) data[2];
                Double average = (Double) data[3];
                Double std = (Double) data[4];
                String password = (String) data[5];

                psei.setChipName(chip_name);
                psei.setLog2Value(log2value);
                psei.setLocalAccession(local_accession);
                psei.setSTD(std);
                psei.setAverage(average);
                psei.setTicketPassword(password);

                pseia.add(psei);
            }

            session.close();
        }

        return pseia;
    }


    /**
     * @return the probeSetId
     */
    public String getProbeSetId() {
        return probeSetId;
    }

    /**
     * @param probeSetId the probeSetId to set
     */
    public void setProbeSetId(String probeSetId) {
        this.probeSetId = probeSetId;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
