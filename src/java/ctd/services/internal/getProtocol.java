/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import ctd.model.Protocol;

import java.util.Iterator;
import java.util.ResourceBundle;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class getProtocol {

    private String wsPassword = "";
    private String selected_id = "1";
    private String names;
    private String description;
    private String submit;

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
     * @return the name
     */
    public String getNames() {
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Protocol pr = new Protocol();

        Query q1 = (Query) session.createQuery("from Protocol");
        Iterator it1 = q1.list().iterator();

        String listbox = "";

        while (it1.hasNext()) {
            Protocol protocol = (Protocol) it1.next();
            String name = protocol.getName();
            Integer protocol_id = protocol.getId();
            String selected_id_match = String.valueOf(protocol_id);
            if (selected_id_match.equals(getSelected_id())) {
                listbox = listbox + "<option value=\"" + String.valueOf(protocol_id) + "\" selected>" + name + "</option>";
            } else {
                listbox = listbox + "<option value=\"" + String.valueOf(protocol_id) + "\">" + name + "</option>";
            }
        }

        session.close();
        sessionFactory.close();
        return listbox;
    }

    /**
     * @param name the name to set
     */
    public void setNames(String names) {
        this.names = names;
    }

    /**
     * @return the description
     */
    public String getDescription() {

        ResourceBundle res = ResourceBundle.getBundle("settings");
        String password = res.getString("ws.password");

        String descr = "";

        if (password.equals(getWsPassword())) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            if (getSubmit().equals("Change")) {
                Transaction tr = session.beginTransaction();

                Protocol pr = null;
                Query q1 = (Query) session.createQuery("from Protocol where id=" + getSelected_id());
                Iterator it1 = q1.list().iterator();

                while (it1.hasNext()) {
                    pr = (Protocol) it1.next();
                    pr.setDescription(this.description);
                }

                session.update(pr);
                tr.commit();
                descr = this.description;
            }

            if (getSubmit().equals("Get")) {



                Protocol pr = new Protocol();

                Query q1 = (Query) session.createQuery("from Protocol where id=" + getSelected_id());
                Iterator it1 = q1.list().iterator();

                while (it1.hasNext()) {
                    Protocol proto = (Protocol) it1.next();
                    descr = proto.getDescription();
                }
            }

            session.close();
            sessionFactory.close();

        }

        return descr;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the selected_name
     */
    public String getSelected_id() {
        return selected_id;
    }

    /**
     * @param selected_name the selected_name to set
     */
    public void setSelected_id(String selected_id) {
        this.selected_id = selected_id;
    }

    /**
     * @return the submit
     */
    public String getSubmit() {
        return submit;
    }

    /**
     * @param submit the submit to set
     */
    public void setSubmit(String submit) {
        this.submit = submit;
    }
}
