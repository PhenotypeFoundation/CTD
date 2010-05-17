/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services;

import ctd.model.Protocol;
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
public class addProtocol {

    private String password;
    private String name;
    private String description;

    public String addProtocol() {

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        String message = "";
        if (getPassword().equals(webservice_password)) {
            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();


            Query q = session.createQuery("from Protocol where name='" + getName() + "'");
            Object protocol = q.uniqueResult();

            if (protocol == null) {
                Protocol pr = new Protocol();
                pr.setDescription(getDescription());
                pr.setName(getName());

                Transaction tr = session.beginTransaction();
                session.saveOrUpdate(pr);
                session.persist(pr);
                tr.commit();
                session.close();
                sessionFactory.close();

                message = getName() + " is added.";
            }
            if (protocol != null) {
                message = getName() + ": This name is allready taken.";
            }

        }

        if (getPassword().equals(webservice_password) == false) {
            message = "Password is incorrect.";
        }

        return message;

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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
