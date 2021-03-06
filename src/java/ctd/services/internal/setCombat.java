/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services.internal;

import ctd.model.Ticket;
import ctd.services.getTicket;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

/**
 *
 * @author kerkh010
 */
public class setCombat {

    private String wsPassword = "";
    private String transactionType = "";
    private String[] experiment_ids;
    private String transactionResult;
    private String experiments;
    private String button;
    private String link = "";

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
     * @return the transactionResult
     */
    public String getTransactionResult() throws IOException {
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String ws_password = res.getString("ws.password");
        String ftp_root = res.getString("ws.ftp_folder");
        String ftp_username = res.getString("ws.ftp_username");
        String owner = res.getString("ws.owner");
        String group = res.getString("ws.group");
        String rscript_combat = res.getString("ws.rscript_combat");
        String rscript = res.getString("ws.rscript");
        
        String hostname = res.getString("ws.hostname");

        String prefix_ftp_subfolder = res.getString("ws.prefix_ftp_subfolders");


        String link = "";
        if (getTransactionType().equals("combat") && getWsPassword().equals(ws_password)) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();


            //gather gct_files in arraylist and define destination folder.
            ArrayList<String> files = new ArrayList<String>();
            String dest_folder = "";
            for (int i = 0; i < getExperiment_ids().length; i++) {
                String id = getExperiment_ids()[i];

                Query qqq = session.createQuery("from Ticket where id=" + id);
                Ticket ticket = (Ticket) qqq.uniqueResult();
                String folder = ticket.getFolder();
                String gctfile = ftp_root + folder + "/gctfile_" + folder + ".gct";
                files.add(gctfile);
                dest_folder = dest_folder + "_" + folder;

            }
            //create destination folder
            String new_folder = ftp_root + dest_folder;
            boolean success = (new File(new_folder)).mkdir();
            //copy gct files to destination folder
            //Adjust permissions
            String command1 = "chown "+ftp_username+" " + new_folder;
            String command2 = "chgrp "+group+" " + new_folder;
            String command3 = "chmod 700 " + new_folder;
            Process child;
            try {
                child = Runtime.getRuntime().exec(command1);
                child = Runtime.getRuntime().exec(command2);
                child = Runtime.getRuntime().exec(command3);
            } catch (IOException ex) {
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            //copy files to destination folder
            for (int i = 0; i < files.size(); i++) {
                String gctfile = files.get(i);
                String command = "cp " + gctfile + " " + new_folder;
                child = Runtime.getRuntime().exec(command);
            }

            /////////////////////////////////////////////
            //Do a system call to normalize with ComBAT.
            String args = rscript + " --vanilla " + rscript_combat + " -w" + new_folder + " -o" + new_folder + "/Combat";
            Process p = Runtime.getRuntime().exec(args);

            File f = new File(new_folder + "/Combat.gct");
            while (f.exists() == false) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(setCombat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            link = "sftp://" + ftp_username + "@" + hostname + ":" + new_folder + "/Combat.gct";



            String link1 = "<a href=\"" + link + "\">download normalized data (sftp)</a>";
            setLink(link1);


            session.close();
            sessionFactory.close();

            //set permissions folder
            String command1_1 = "chmod 500 " + new_folder;
            String command1_2 = "chmod +t " + new_folder;
            Process p2 = Runtime.getRuntime().exec(command1_2);
            Process p1 = Runtime.getRuntime().exec(command1_1);


            //set permissions combat-file
            String command2_1 = "chmod 040 " + new_folder + "/Combat.gct";
            String command2_2 = "chown "+owner+" " + new_folder + "/Combat.gct";
            String command2_3 = "chgrp "+group+" " + new_folder + "/Combat.gct";

            Process p4_1 = Runtime.getRuntime().exec(command2_1);
            Process p4_2 = Runtime.getRuntime().exec(command2_2);
            Process p4_3 = Runtime.getRuntime().exec(command2_3);


        }





        return link;
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

    /**
     * @return the experiment_ids
     */
    public String[] getExperiment_ids() {
        return experiment_ids;
    }

    /**
     * @param experiment_ids the experiment_ids to set
     */
    public void setExperiment_ids(String[] experiment_ids) {
        this.experiment_ids = experiment_ids;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }
}
