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

import com.skaringa.javaxml.DeserializerException;
import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Chip;
import ctd.model.ChipAnnotation;
import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import ctd.statistics.Statistics;
import ctd.ws.model.CleanDataResult;
import ctd.ws.model.TicketClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class storeGCTFile {

    private String password;

    public String storeGCTFile() throws NoImplementationException, SerializerException, DeserializerException, IOException {

        String message = "";
        String error_message = "";
        CleanDataResult result = new CleanDataResult();

        //get the ftp folder
        //get parameters.
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String ftp_folder = res.getString("ws.ftp_folder");
        String ws_password = res.getString("ws.password");

        String db_username = res.getString("db.username");
        String db_password = res.getString("db.password");
        String db_database = res.getString("db.database");


        //get the Ticket
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Query q1 = session.createQuery("from Ticket where password='" + getPassword() + "'");
        Ticket ticket = null;
        String closed = "";
        String folder = "";
        if (q1.list().size() != 0) {
            ticket = (Ticket) q1.list().get(0);
            closed = ticket.getClosed();
            folder = ticket.getFolder();
        }
        if (ticket == null) {
            error_message = "Ticket password and CTD_REF don't match.";
        }
        if (closed.equals("yes")) {
            error_message = "Ticket is allready used for normalization of these CEL-files.";
            ticket = null;
        }
        session.close();
        //get the folder
        if (ticket != null) {

            //create subfolder containing the derived zip file
            String session_folder = ftp_folder + folder;
            //get content
            File dir = new File(session_folder);
            String[] files = dir.list();
            //find the zip file.
            String cel_zip_file = "";
            String chip_file = "";
            String gct_file = "";
            String cdf_name = "";
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                if (file.endsWith("gct")) {
                    gct_file = session_folder + "/" + file;
                }
                if (file.endsWith("chip")) {
                    chip_file = session_folder + "/" + file;
                    cdf_name = file.replace(".chip", "");
                }
            }
            Process p3 = Runtime.getRuntime().exec("chmod 770 " + gct_file);
            Process p4 = Runtime.getRuntime().exec("chmod 770 " + chip_file);


            //store chip file , if not allready stored.
            SessionFactory sessionFactory1 = new Configuration().configure().buildSessionFactory();
            Session session1 = sessionFactory1.openSession();

            List<ChipAnnotation> chip_annotation = null;

            //check if cdf (chip definition file) is allready stored, if not, store it.

            Query q2 = session1.createQuery("from Chip Where Name='" + cdf_name + "'");
            if (q2.uniqueResult() != null) {
                Chip chip = (Chip) q2.list().get(0);
                chip_annotation = chip.getChipAnnotation();
            }
            if (q2.uniqueResult() == null) {
                //Add this chip and its annotation
                Chip chip_new = new Chip();
                chip_new.setName(cdf_name);

                //read chip file
                chip_annotation = readChip(chip_file);

                //Store the whole
                chip_new.getChipAnnotation().addAll(chip_annotation);

                Transaction tr1 = session1.beginTransaction();
                session1.save(chip_new);
                session1.persist(chip_new);
                tr1.commit();
                session1.close();
            }
            ///////////////////////////////////////////////////////////////////////////////////////
            //create array data input file for the database table, find correct foreign keys.
            //get the study_sample_assay id and the probeset ids.
            SessionFactory sessionFactory2 = new Configuration().configure().buildSessionFactory();
            Session session2 = sessionFactory2.openSession();
            Transaction tr = session2.beginTransaction();
            //Get the cip_annotation_id
            Query q3 = session2.createQuery("from Chip Where Name='" + cdf_name + "'");
            Chip chip = (Chip) q3.list().get(0);
            chip_annotation = chip.getChipAnnotation();
            Iterator it2 = chip_annotation.iterator();
            //for speed, put the chip annotation id in a hashmap
            HashMap<String, String> chip_annotation_ids = new HashMap<String, String>();
            while (it2.hasNext()) {
                ChipAnnotation ca = (ChipAnnotation) it2.next();
                String id = ca.getId().toString();
                String ps = ca.getProbeset();
                chip_annotation_ids.put(ps, id);
            }

            //Read the CEL-file names from the header of the GCT-file
            BufferedReader input = new BufferedReader(new FileReader(gct_file));
            String line;
            ArrayList<String> cel_file_array = new ArrayList<String>();
            while ((line = input.readLine()) != null) {

                String[] columns = line.split("\t");
                if (columns[0].equals("Name")) {
                    for (int i = 0; i < columns.length; i++) {
                        String header_name = columns[i].trim();
                        message = message + " " + header_name;
                        if (i > 1) {
                            cel_file_array.add(header_name);
                        }
                    }
                    break;
                }

            }

            //Store the cel_file_names, remove the .CEL extension.
            Query q = session2.createQuery("from Ticket where password='" + getPassword() + "'");

            Ticket ticket_update_1 = null;

            if (q.list().size() != 0) {
                ticket_update_1 = (Ticket) q.list().get(0);
                message = message + " " + ticket_update_1.getId().toString();
                ArrayList<StudySampleAssay> ssas = new ArrayList<StudySampleAssay>();
                for (int i = 0; i < cel_file_array.size(); i++) {
                    StudySampleAssay ssa = new StudySampleAssay();
                    String name_raw_file = cel_file_array.get(i).replace(".CEL", "");
                    ssa.setNameRawfile(name_raw_file);
  
                    ssas.add(ssa);
                }
                ticket_update_1.getStudySampleAssaies().addAll(ssas);
                session2.saveOrUpdate(ticket_update_1);
                session2.persist(ticket_update_1);
                tr.commit();
            }

            session2.close();

            //Create the data file for database storage, containing the secondairy keys to the probeset- and the assay-ids.
            //create the temp file for storage of the data_insert file.
            SessionFactory sessionFactory3 = new Configuration().configure().buildSessionFactory();
            Session session3 = sessionFactory3.openSession();

            String data_file = session_folder + "/data.txt";
            FileOutputStream out = null;
            PrintStream pr = null;
            try {
                out = new FileOutputStream(data_file);
                pr = new PrintStream(out);

                Query qt = session3.createQuery("from Ticket where password='" + getPassword() + "'");

                ticket = null;

                if (qt.list().size() != 0) {
                    ticket = (Ticket) qt.list().get(0);
                }

                Iterator it3 = ticket.getStudySampleAssaies().iterator();
                while (it3.hasNext()) {
                    StudySampleAssay ssa = (StudySampleAssay) it3.next();
                    String name_raw_file = ssa.getNameRawfile();
                    String ssa_id = ssa.getId().toString();
                    
                    ArrayList<Double> values = writeFile(pr, chip_annotation_ids, ssa_id, gct_file, name_raw_file);

                    Statistics stat = new Statistics();
                    stat.setData(values);
                    Double average = stat.getAverage();
                    Double std = stat.getSTD();

                    ssa.setAverage(average);
                    ssa.setStd(std);
                }
                
                pr.close();
            } catch (IOException e) {
            }
            out.close();
            Transaction tr3 = session3.beginTransaction();
            session3.update(ticket);
            session3.persist(ticket);
            tr3.commit();

            session3.close();

            String sql = "LOAD DATA LOCAL INFILE '" + data_file + "' INTO TABLE "+db_database+".expression";
            String u = "--user="+db_username;
            String p = "--password="+db_password;
            String[] commands = new String[]{"mysql", "-e",sql ,u , p};

            Process p5 = Runtime.getRuntime().exec(commands);

            //close the Ticket. Storage can only be done once.
            CloseTicket();

        }

        ///////////
        //SKARINGA
        result.setErrorMessage(error_message);
        result.setMessage(message);

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToString(result);
        return message;
    }

    private ArrayList<Double> writeFile(PrintStream pr, HashMap<String, String> chip_annotation_ids, String ssa_id, String gct_file_generated, String name_raw_file) throws FileNotFoundException, IOException {

        BufferedReader input = new BufferedReader(new FileReader(gct_file_generated));
        String line;

        Integer expression_column = null;
        Integer name_column = null;
        Integer description_column = null;
        Boolean do_header = false;
        Boolean do_data = false;
        Boolean start = false;
        ArrayList<Double> values = new ArrayList<Double>();

        while ((line = input.readLine()) != null) {

            String[] columns = line.split("\t");
            if (columns[0].equals("Name")) {
                do_header = true;
                name_column = 0;
            }

            if (do_header) {
                for (int i = 0; i < columns.length; i++) {
                    String header = columns[i].trim().replace(".CEL", "");
                    
                    if (header.equals(name_raw_file)) {
                        expression_column = i;
                        do_data = true;
                    }

//                    if (header.equals("Name")) {
//                        name_column = i;
//                    }
                }
            }
            do_header = false;

            if (start) {
                String probesetid = columns[name_column];

                Double value = Double.valueOf(columns[expression_column]);
                value = Math.log10(value) / Math.log10(2);

                values.add(value);

                String chip_annotation_id = chip_annotation_ids.get(probesetid);

                String expr_line = ssa_id + "\t" + chip_annotation_id + "\t" + value.toString();
                pr.println(expr_line);
            }

            if (do_data) {
                start = true;
            }

        }
        return values;
    }

    private ArrayList<ChipAnnotation> readChip(String file) throws FileNotFoundException, IOException {
        ArrayList<ChipAnnotation> ann_list = new ArrayList<ChipAnnotation>();
        BufferedReader input = new BufferedReader(new FileReader(file));

        int probe_set_id_index = 0;
        int gene_symbol_index = 0;
        int gene_title_index = 0;

        //read the header
        String line = null;
        line = input.readLine();
        String[] columns = line.split("\t");
        for (int i = 0; i <
                columns.length; i++) {
            String name = columns[i];
            if (name.equals("Probe Set Id")) {
                probe_set_id_index = i;
            }

            if (name.equals("Gene Symbol")) {
                gene_symbol_index = i;
            }

            if (name.equals("Gene Title")) {
                gene_title_index = i;
            }
        }

        String probeset = null;
        String gene_symbol = null;
        String gene_description = null;

        int count = 0;
        while ((line = input.readLine()) != null) {
            columns = line.split("\t");
            for (int j = 0; j <
                    columns.length; j++) {
                if (probe_set_id_index == j) {
                    probeset = columns[j];
                }

                if (gene_symbol_index == j) {
                    gene_symbol = columns[j];
                }

                if (gene_title_index == j) {
                    gene_description = columns[j];
                }

            }

            //Extend the chip object with ChipProbeSet
            ChipAnnotation chip_annotation = new ChipAnnotation();

            String accession = probeset.replaceAll("_at", "");
            if (gene_symbol.equals("NA") == false) {
                chip_annotation.setGeneSymbol(gene_symbol);
            }

            chip_annotation.setProbeset(probeset);
            chip_annotation.setGeneAccession(accession);
            if (gene_description.equals("NA") == false) {
                chip_annotation.setGeneAnnotation(gene_description);
            }
            count++;
            ann_list.add(chip_annotation);
        }

        input.close();
        return ann_list;
    }

    private void CloseTicket() {

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q = session.createQuery("from Ticket where password='" + getPassword() + "'");

        Ticket ticket = null;

        if (q.list().size() != 0) {
            ticket = (Ticket) q.list().get(0);
            ticket.setClosed("yes");
        }

        Transaction tr = session.beginTransaction();
        session.saveOrUpdate(ticket);
        session.persist(ticket);
        tr.commit();
        session.close();

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
