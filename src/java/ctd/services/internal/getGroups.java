/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String transactionType = "";
    private String transactionResult = "";
    private String button = "";
    private String tableSamples;
    private String[] selectedSamples;
    private String samplesNoGroupList;
    private String selectedGroup = "";
    private String currentGroup = "";
    private String groupList;
    private String[] selectedSamplesGroup;
    private String samplesGroupList;

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



        Query q1 = (Query) session.createQuery("from Ticket Where title is not null");
        Iterator it1 = q1.list().iterator();

        String listbox = "";
        Boolean select_first = false;
        if (getSelected_id().equals("")){
            select_first=true;
        }


        while (it1.hasNext()) {
            Ticket ticket = (Ticket) it1.next();
            String name = ticket.getTitle();
            Integer ticket_id = ticket.getId();
            String selected_id_match = String.valueOf(ticket_id);
            if (select_first==false){
                if (ticket_id.equals(selected_id_match)){
                    listbox = listbox + "<option value=\"" + String.valueOf(ticket_id) + "\" selected>" + name + "</option>";
                }
                if (ticket_id.equals(selected_id_match)==false){
                    listbox = listbox + "<option value=\"" + String.valueOf(ticket_id) + "\">" + name + "</option>";
                }
            }
            
            if (select_first) {
                listbox = listbox + "<option value=\"" + String.valueOf(ticket_id) + "\" selected>" + name + "</option>";

                String fg = getFirstGroup(ticket_id.toString());
                setCurrentGroup(fg);
                setSelected_id(ticket_id.toString());

                select_first=false;
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


            Query q1 = session.createQuery("from StudySampleAssay where ticket_id=" + getSelected_id() + " order by nameRawfile");
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

            sessionFactory.close();
            session.close();

        }


        return table;
    }

    /**
     * @param tableSamples the tableSamples to set
     */
    public void setTableSamples(String tableSamples) {
        this.tableSamples = tableSamples;
    }

    /**
     * @return the selectedSamples
     */
    public String[] getSelectedSamples() {
        return selectedSamples;
    }

    /**
     * @param selectedSamples the selectedSamples to set
     */
    public void setSelectedSamples(String[] selectedSamples) {
        this.selectedSamples = selectedSamples;
    }

    /**
     * @return the samplesNoGroupList
     */
    public String getSamplesNoGroupList() {
        String option = "";

        if (getButton().equals("Retrieve")) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            Query q1 = session.createQuery("from StudySampleAssay where ticket_id=" + getSelected_id() + " AND groupName is null order by nameRawfile");
            Iterator it1 = q1.list().iterator();

            while (it1.hasNext()) {
                StudySampleAssay ssa = (StudySampleAssay) it1.next();
                Integer id = ssa.getId();
                String groupname = ssa.getGroupName();
                String rawfilename = ssa.getNameRawfile();
                if (groupname == null) {
                    option = option + "<option value=\"" + id.toString() + "\">" + rawfilename + "</option>";
                }
            }

            sessionFactory.close();
            session.close();
        }

        this.samplesNoGroupList = option;

        return option;

    }

    /**
     * @param samplesNoGroupList the samplesNoGroupList to set
     */
    public void setSamplesNoGroupList(String samplesList) {
        this.setSamplesNoGroupList(samplesList);
    }

    /**
     * @return the selectedGroup
     */
    public String getSelectedGroup() {
        return selectedGroup;
    }

    /**
     * @param selectedGroup the selectedGroup to set
     */
    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    /**
     * @return the groupList
     */
    public String getGroupList() {

        String option = "";

        if (getButton().equals("Retrieve")) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            Query q1 = session.createQuery("from StudySampleAssay where ticket_id=" + getSelected_id() + " order by groupName");
            Iterator it1 = q1.list().iterator();

            ArrayList<String> filter = new ArrayList<String>();
            String current_group_name = getCurrentGroup();
            String selected = "";
            String selected_groupname = "";
            Boolean do_first_selection = true;
            if (current_group_name.equals("") == false) {
                do_first_selection = false;

            }

            while (it1.hasNext()) {
                StudySampleAssay ssa = (StudySampleAssay) it1.next();
                Integer id = ssa.getId();
                String groupname = ssa.getGroupName();


                if (groupname != null) {
                    if (filter.contains(groupname) == false) {
                        filter.add(groupname);
                        
                        if (do_first_selection==false){
                            if (current_group_name.equals(groupname)){
                                option = option + "<option value=\"" + groupname + "\" selected>" + groupname + "</option>";
                            }
                            if (current_group_name.equals(groupname)==false){
                                option = option + "<option value=\"" + groupname + "\">" + groupname + "</option>";
                            }
                        }


                        if (do_first_selection) {
                            option = option + "<option value=\"" + groupname + "\" selected>" + groupname + "</option>";
                            do_first_selection = false;
                        }


                    }
                }
            }
           


            //set default group, first in list.
            if (getSelectedGroup() == null) {
                if (filter.size() != 0) {
                    setSelectedGroup(filter.get(0));
                }
            }

            setGroupList(option);

            sessionFactory.close();
            session.close();
        }

        return option;
    }

    /**
     * @param groupList the groupList to set
     */
    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }

    /**
     * @return the selectedSamplesGroup
     */
    public String[] getSelectedSamplesGroup() {
        return selectedSamplesGroup;
    }

    /**
     * @param selectedSamplesGroup the selectedSamplesGroup to set
     */
    public void setSelectedSamplesGroup(String[] selectedSamplesGroup) {
        this.selectedSamplesGroup = selectedSamplesGroup;
    }

    /**
     * @return the samplesGroupList
     */
    public String getSamplesGroupList() {

        String option = "";

        if (getButton().equals("Retrieve")) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            if (getSelectedGroup().equals("")) {
                String group_name = getFirstGroup(getSelected_id());
                setSelectedGroup(group_name);
            }


            Query q1 = session.createQuery("from StudySampleAssay where ticket_id=" + getSelected_id() + " AND groupName='" + getSelectedGroup() + "' order by nameRawfile");
            Iterator it1 = q1.list().iterator();

            while (it1.hasNext()) {
                StudySampleAssay ssa = (StudySampleAssay) it1.next();
                Integer id = ssa.getId();

                String rawfilename = ssa.getNameRawfile();

                option = option + "<option value=\"" + id.toString() + "\">" + rawfilename + "</option>";

            }

            sessionFactory.close();
            session.close();
        }

        this.samplesGroupList = option;

        return option;

    }

    /**
     * @param samplesGroupList the samplesGroupList to set
     */
    public void setSamplesGroupList(String samplesGroupList) {
        this.samplesGroupList = samplesGroupList;
    }

    private String getFirstGroup(String id) {
        String group_name = "";
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q1 = session.createQuery("from StudySampleAssay where ticket_id=" + id + " ORDER BY groupName");
        Iterator it1 = q1.list().iterator();

        ArrayList<String> filter = new ArrayList<String>();

        while (it1.hasNext()) {
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            String groupname = ssa.getGroupName();
            if (groupname != null) {
                group_name = groupname;
                break;
            }
        }

        sessionFactory.close();
        session.close();
        return group_name;
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
     * @return the transactionResult
     */
    public String getTransactionResult() {

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        if (getTransactionType().equals("ungroup")) {

            String[] ssa_ids = getSelectedSamplesGroup();
            transactionResult = "ungrouped " + String.valueOf(ssa_ids.length) + " samples";
            for (int i = 0; i < ssa_ids.length; i++) {
                String ssa_id = ssa_ids[i];
                Query q1 = (Query) session.createQuery("from  StudySampleAssay where id=" + ssa_id);
                StudySampleAssay ssa = (StudySampleAssay) q1.uniqueResult();
                ssa.setGroupName(null);
                session.update(ssa);

            }

        }

        if (getTransactionType().equals("group")) {
            String[] ssa_ids = getSelectedSamples();
            transactionResult = "grouped " + String.valueOf(ssa_ids.length) + " samples";
            for (int i = 0; i < ssa_ids.length; i++) {
                String ssa_id = ssa_ids[i];
                Query q1 = (Query) session.createQuery("from StudySampleAssay where id=" + ssa_id);
                StudySampleAssay ssa = (StudySampleAssay) q1.uniqueResult();
                ssa.setGroupName(getCurrentGroup());
                session.update(ssa);

            }
        }

        tr.commit();

        sessionFactory.close();
        session.close();

        setTransactionType("");

        return transactionResult;
    }

    /**
     * @param transactionResult the transactionResult to set
     */
    public void setTransactionResult(String transactionResult) {
        this.transactionResult = transactionResult;
    }

    /**
     * @return the currentGroup
     */
    public String getCurrentGroup() {

        return currentGroup;
    }

    /**
     * @param currentGroup the currentGroup to set
     */
    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }
}
