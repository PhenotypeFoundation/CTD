/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
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
public class getGroups {

    private String selected_id = "1";
    private String experiments;
    private String to_do = "";
    private String button = "";
    private String tableSamples;

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
     * @return the experiments
     */
    public String getExperiments() {
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();



        Query q1 = (Query) session.createQuery("from Ticket");
        Iterator it1 = q1.list().iterator();

        String listbox = "";

        while (it1.hasNext()) {
            Ticket ticket = (Ticket) it1.next();
            String name = ticket.getTitle();
            Integer ticket_id = ticket.getId();
            String selected_id_match = String.valueOf(ticket_id);
            if (selected_id_match.equals(getSelected_id())) {
                listbox = listbox + "<option value=\"" + String.valueOf(ticket_id) + "\" selected>" + name + "</option>";
            } else {
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
     * @return the to_do
     */
    public String getTo_do() {
        return to_do;
    }

    /**
     * @param to_do the to_do to set
     */
    public void setTo_do(String to_do) {
        this.to_do = to_do;
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

    /**
     * @return the tableSamples
     */
    public String getTableSamples() {


        String table = "";
        if (getButton().equals("Retrieve")) {

            table = "<table class=\"text_normal\" width=\"600\" border=\"0\" cellspacing=\"2\" cellpadding=\"1\">";
            //Add header columns
            table = table + "<tr><td width=\"100\">Sample name</td><td width=\"490\">Group name</td></tr>";

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            Query q1 = session.createQuery("from StudySampleAssay where ticket_id=" + getSelected_id()+" order by nameRawfile");
            Iterator it1 = q1.list().iterator();
            
            while (it1.hasNext()) {
                StudySampleAssay ssa = (StudySampleAssay) it1.next();
                Integer id = ssa.getId();
                String groupname = ssa.getGroupName();
                String rawfilename = ssa.getNameRawfile();
                if (groupname == null) {
                    groupname = "";
                }
                String textfield = "<input name=\"" + id.toString() + "\" type=\"text\" id=\"" + id.toString() + "\" size=\"45\" value=\"" + groupname + "\">";

                table = table + "<tr><td width=\"150\">" + rawfilename + "</td><td width=\"440\">" + textfield + "</td></tr>";
            }

            table = table + "</table>";
        }

        return table;
    }

    /**
     * @param tableSamples the tableSamples to set
     */
    public void setTableSamples(String tableSamples) {
        this.tableSamples = tableSamples;
    }
}
