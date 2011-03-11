/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import ctd.model.StudySampleAssay;

import ctd.model.Ticket;
import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

/**
 *
 * @author kerkh010
 */
public class setDelete {

    private String wsPassword = "";
    private String transactionType = "";
    private String selected_id;
    private String transactionResult;
    private String experiments;
    private String button;

    /**
     * @return the wsPassword
     */
    public String getWsPassword() {
        return wsPassword;
    }

    /**
     * @param wsPassword the wsPassword to set
     */
    public void setWsPassword(String wsPassword) {
        this.wsPassword = wsPassword;
    }

    /**
     * @return the transactionType
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType the transactionType to set
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * @return the selected_id
     */
    public String getSelected_id() {
        return selected_id;
    }

    /**
     * @param selected_id the selected_id to set
     */
    public void setSelected_id(String selected_id) {
        this.selected_id = selected_id;
    }

    /**
     * @return the transactionResult
     */
    public String getTransactionResult() {

        ResourceBundle res = ResourceBundle.getBundle("settings");
        String ws_password = res.getString("ws.password");
        String ftp_folder = res.getString("ws.ftp_folder");
        String prefix_ftp_subfolder = res.getString("ws.prefix_ftp_subfolders");

        Integer count = 0;
        if (getTransactionType().equals("delete") && getWsPassword().equals(ws_password)) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            Query qqq = session.createQuery("from Ticket where id=" + getSelected_id());
            Ticket ticket = (Ticket) qqq.uniqueResult();


            //Delete, if ticket is retrieved.

            if (ticket != null) {
                //gather study_sampel_assay_ids to be deleted in the expression table
                Iterator it1 = ticket.getStudySampleAssaies().iterator();
                while (it1.hasNext()) {
                    StudySampleAssay ssa = (StudySampleAssay) it1.next();

                    session.delete(ssa);
                    count++;
                }
                //Delete ticket and its connected samples
                Transaction tx = (Transaction) session.beginTransaction();
                session.delete(ticket);
                tx.commit();

            }
            session.close();
            sessionFactory.close();

            //delete folder also
            //ctd ftp subfolder
            String remove_path = ftp_folder+prefix_ftp_subfolder + getSelected_id();
            String command4 = "rm -fr " + remove_path;
            Process child;
            try {

                child = Runtime.getRuntime().exec(command4);
            } catch (IOException ex) {
                String error = ex.toString();
            }

        }
        transactionResult = "Deleted " + count.toString() + " samples";

        return transactionResult;
    }

    /**
     * @param transactionResult the transactionResult to set
     */
    public void setTransactionResult(String transactionResult) {
        this.transactionResult = transactionResult;
    }

    /**
     * @return the experiments
     */
    public String getExperiments() {
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q1 = (Query) session.createQuery("from Ticket Where title is not null");
        Iterator it1 = q1.list().iterator();

        String listbox = "";

        while (it1.hasNext()) {
            Ticket ticket = (Ticket) it1.next();
            String name = ticket.getTitle();
            Integer ticket_id = ticket.getId();

            if (ticket_id != null) {
                listbox = listbox + "<option value=\"" + String.valueOf(ticket_id) + "\">" + name + "</option>";
            }
            
        }

        session.close();
        sessionFactory.close();
        return listbox;
    }

    /**
     * @param experiments the experiments to set
     */
    public void setExperiments(String experiments) {
        this.experiments = experiments;
    }

    /**
     * @return the button
     */
    public String getButton() {
        return button;
    }

    /**
     * @param button the button to set
     */
    public void setButton(String button) {
        this.button = button;
    }
}
