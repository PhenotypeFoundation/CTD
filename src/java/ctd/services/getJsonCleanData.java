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
import ctd.model.Chip;
import ctd.model.ChipAnnotation;
import ctd.model.StudySampleAssay;
import ctd.model.Ticket;

import ctd.statistics.Statistics;
import ctd.ws.model.CleanDataResult;
import java.io.BufferedReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.io.IOException;
import java.io.InputStreamReader;
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
public class getJsonCleanData {

    private String ticketPassword;

    public String cleanData() throws IOException, NoImplementationException, SerializerException {

        CleanDataResult result = new CleanDataResult();

        String message = "";
        String error_message = "";
        //get parameters.
        ResourceBundle res = ResourceBundle.getBundle("settings");
        ResourceBundle cdf_list = ResourceBundle.getBundle("cdf");

        //Base directory ftp folder: Here the subfolders are found for each set of CEL-files.
        String ftp_folder = res.getString("ws.ftp_folder");
        String rscript_cleandata = res.getString("ws.rscript_cleandata");
        String rscript = res.getString("ws.rscript");
        //db
        String db_username = res.getString("db.username");
        String db_password = res.getString("db.password");
        String db_database = res.getString("db.database");

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();

        Query q = session.createQuery("from Ticket where password='" + getTicketPassword() + "'");

        Ticket ticket = null;
        String closed = "";
        if (q.list().size() != 0) {
            ticket = (Ticket) q.list().get(0);
            closed = ticket.getClosed();
        }
        if (ticket == null) {
            error_message = "Ticket password and CTD_REF don't match.";
        }
        if (closed.equals("yes")) {
            error_message = "Ticket is allready used for normalization of these CEL-files.";
            ticket = null;
        }
        if (ticket != null) {
            //get the folder
            String folder = ticket.getFolder();
            //create subfolder containing the derived zip file
            String zip_folder = ftp_folder + folder;
            //get content
            File dir = new File(zip_folder);
            String[] files = dir.list();
            //find the zip file.
            String cel_zip_file = "";
            String zip_file = "";
            String gct_file = "";

            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                if (file.contains("zip")) {
                    cel_zip_file = file;
                    zip_file = zip_folder + "/" + cel_zip_file;
                    gct_file = zip_folder + "/gctfile_" + folder;
                }
            }
            Process p3_0 = Runtime.getRuntime().exec("chmod 040 " + zip_file);
            Process p3_1 = Runtime.getRuntime().exec("chown robertk.ctd " + zip_file);
            

            //////////////////////////////////////////////////////////////////
            //Do a system call to normalize. R. (zip_folder zip_file gct_file rscript)
            String args = rscript + " --vanilla " + rscript_cleandata + " -i" + zip_file + " -o" + gct_file + " -w" + zip_folder;
            Process p = Runtime.getRuntime().exec(args);

            //Check if CEL files are unzipped allready
            boolean do_loop = true;
            while (do_loop) {
                File dir2 = new File(zip_folder);
                String[] files2 = dir2.list();
                //Check if Job is allready done, if gct-file is there.
                for (int i = 0; i < files2.length; i++) {
                    String file = files2[i];
                    if (file.endsWith("gct")) {
                        do_loop = false;
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(getCleanData.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            
           

            File dir2 = new File(zip_folder);
            String[] files2 = dir2.list();
            String chip_file = "";
            String chip_file_db = "";
            ArrayList<String> unziped_files = new ArrayList<String>();
            for (int i = 0; i < files2.length; i++) {
                String file = files2[i];
                if (file.endsWith("CEL")) {
                    unziped_files.add(file);
                }
                if (file.endsWith("chip")) {
                    chip_file = file;

                    chip_file_db = chip_file.split("_CDF_")[1];
                }
            }

            //Check if all CEL files are derived from the same chip.
            //This is essential for normalization.
            //initiate check hashmap. This map contains all the unique chip definition file names. There should be only one per analysis.

            ArrayList<StudySampleAssay> map = new ArrayList<StudySampleAssay>();

            for (int i = 0; i < unziped_files.size(); i++) {
                String cel_file = unziped_files.get(i);

                StudySampleAssay ssa = new StudySampleAssay();
                // Open the file that is the first
                // command line parameter
                String cel_file_path = zip_folder + "/" + cel_file;
                String name = cel_file.replaceAll(".CEL", "");
                ssa.setNameRawfile(name);
                ssa.setXREF(name);
                map.add(ssa);

                String command = "rm "+cel_file_path;
                Process  pr = Runtime.getRuntime().exec(command);


            }

            ticket.getStudySampleAssaies().addAll(map);
            session.saveOrUpdate(ticket);
            session.persist(ticket);
            tr.commit();
            session.close();

            //Storage chip definition file (CDF), creation gct file and database storage.
            SessionFactory sessionFactory1 = new Configuration().configure().buildSessionFactory();
            Session session1 = sessionFactory1.openSession();

            List<ChipAnnotation> chip_annotation = null;

            //check if cdf (chip definition file) is allready stored, if not, store it.

            Query q2 = session1.createQuery("from Chip Where Name='" + chip_file_db + "'");
            if (q2.uniqueResult() != null) {
                Chip chip = (Chip) q2.list().get(0);
                chip_annotation = chip.getChipAnnotation();
            }

            if (q2.uniqueResult() == null) {
                //Add this chip and its annotation
                Chip chip_new = new Chip();
                chip_new.setName(chip_file_db);

                //read chip file
                String chip_file_path = zip_folder + "/" + chip_file;
                chip_annotation = readChip(chip_file_path);

                //Store the whole
                chip_new.getChipAnnotation().addAll(chip_annotation);

                Transaction tr1 = session1.beginTransaction();
                session1.save(chip_new);
                session1.persist(chip_new);
                tr1.commit();
                session1.close();
            }

            //create the temp file for storage of the data_insert file.
            String data_file = zip_folder + "/expression.txt";
            FileOutputStream out = null;
            PrintStream pr = null;
            out = new FileOutputStream(data_file);
            pr = new PrintStream(out);

            //create array data input file for the database table, find correct foreign keys.
            //get the study_sample_assay id and the probeset ids.
            SessionFactory sessionFactory2 = new Configuration().configure().buildSessionFactory();
            Session session2 = sessionFactory2.openSession();

            //Get the cip_annotation_id
            Query q3 = session2.createQuery("from Chip Where Name='" + chip_file_db + "'");
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

            try {

                Query qt = session2.createQuery("from Ticket where password='" + getTicketPassword() + "'");

                ticket = null;

                if (qt.list().size() != 0) {
                    ticket = (Ticket) qt.list().get(0);
                }

                Iterator it3 = ticket.getStudySampleAssaies().iterator();
                while (it3.hasNext()) {
                    StudySampleAssay ssa = (StudySampleAssay) it3.next();
                    String name_raw_file = ssa.getNameRawfile();
                    String ssa_id = ssa.getId().toString();
                    error_message = error_message + name_raw_file;

                    String gct_file_generated = gct_file + ".gct";
                    ArrayList<Double> values = writeFile(pr, chip_annotation_ids, ssa_id, gct_file_generated, name_raw_file);

                    Statistics stat = new Statistics();
                    stat.setData(values);
                    Double average = stat.getAverage();
                    Double std = stat.getSTD();

                    ssa.setXREF(name_raw_file);
                    ssa.setAverage(average);
                    ssa.setStd(std);
                }

            } catch (IOException e) {
            }
            pr.close();
            out.close();

            Transaction tr2 = session2.beginTransaction();
            session2.update(ticket);
            session2.persist(ticket);
            tr2.commit();

            session2.close();

            String u = "--user=" + db_username;
            String passw = "--password=" + db_password;
            String[] commands = new String[]{"mysqlimport", u, passw, "--local", db_database, data_file};

            Process p4 = Runtime.getRuntime().exec(commands);

            message = message + " RMA and GRSN on the CEL-files is done, data is stored.";
            //close the ticket when finished, normalization can only be performed once by the client.
            CloseTicket();

            //remove data_file
            Process p5 = Runtime.getRuntime().exec("rm -f " + data_file);
            

            //set permissions folder
            String command1 = "chmod 500 " + zip_folder;
            String command1_1 = "chmod +t " + zip_folder;

            Process  p4_0_1 = Runtime.getRuntime().exec(command1_1);
            Process  p4_0 = Runtime.getRuntime().exec(command1);
            

            //set permissions gct-file
            String command2 = "chmod 040 "+gct_file +".gct";
            String command3 = "chown robertk "+gct_file+".gct";
            String command4 = "chgrp ctd "+gct_file+".gct";

            Process  p4_1 = Runtime.getRuntime().exec(command2);
            Process  p4_2 = Runtime.getRuntime().exec(command3);
            Process  p4_3 = Runtime.getRuntime().exec(command4);

        }

        ////////////////////
        //SKARINGA
        result.setErrorMessage(error_message);

        result.setMessage(message);
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToJsonString(result);
        return message;
    }

    private void CloseTicket() {

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q = session.createQuery("from Ticket where password='" + getTicketPassword() + "'");

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

    private ArrayList<ChipAnnotation> readChip(String file) throws FileNotFoundException, IOException {
        ArrayList<ChipAnnotation> ann_list = new ArrayList<ChipAnnotation>();
        BufferedReader input = new BufferedReader(new FileReader(file));

        int probe_set_id_index = 0;
        int gene_symbol_index = 0;
        int gene_title_index = 0;

        //read the header
        String line = null;
        line =
                input.readLine();
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

    /**
     * @return the password
     */
    public String getTicketPassword() {
        return ticketPassword;
    }

    /**
     * @param password the password to set
     */
    public void setTicketPassword(String password) {
        this.ticketPassword = password;
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
                    String header = columns[i].trim();
                    String xx = name_raw_file + ".CEL";
                    if (header.equals(xx)) {
                        expression_column = i;
                        do_data =
                                true;
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
}
