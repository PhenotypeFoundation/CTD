/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.services;

import ctd.model.Ticket;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class addTitle {
    private String password;
    private String title;

    public String addTitle(){

        String message = "";
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q = session.createQuery("from Ticket where password='"+getPassword()+"'");
        Ticket ticket = (Ticket) q.uniqueResult();


        ticket.setTitle(getTitle());

        Transaction tr = session.beginTransaction();
        session.saveOrUpdate(ticket);
        session.persist(ticket);
        tr.commit();
        session.close();
        sessionFactory.close();

        message = "The title is added.";

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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
