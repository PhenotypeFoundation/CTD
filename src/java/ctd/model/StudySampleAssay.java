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

package ctd.model;

import java.io.Serializable;

public class StudySampleAssay  implements Serializable {

     private int id;
     private Ticket ticket;
     private String XREF;
     private String chipTime;
     private String nameRawfile;
     private Double average;
     private Double std;
     private Integer LIST_POS;

    public StudySampleAssay() {
    }

	
    public StudySampleAssay(Integer id, Ticket ticket, Integer LIST_POS) {
        this.id = id;
        this.ticket = ticket;
        this.LIST_POS = LIST_POS;
    }
    public StudySampleAssay(Integer id, Ticket ticket, String XREF, String chipTime, String nameRawfile, Double average, Double std, Integer LIST_POS) {
       this.id = id;
       this.ticket = ticket;
       this.XREF = XREF;
       this.chipTime = chipTime;
       this.nameRawfile = nameRawfile;
       this.average = average;
       this.std = std;
       this.LIST_POS = LIST_POS;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Ticket getTicket() {
        return this.ticket;
    }
    
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
    public String getXREF() {
        return this.XREF;
    }
    
    public void setXREF(String XREF) {
        this.XREF = XREF;
    }
    public String getChipTime() {
        return this.chipTime;
    }
    
    public void setChipTime(String chipTime) {
        this.chipTime = chipTime;
    }
    public String getNameRawfile() {
        return this.nameRawfile;
    }
    
    public void setNameRawfile(String nameRawfile) {
        this.nameRawfile = nameRawfile;
    }
    public Double getAverage() {
        return this.average;
    }
    
    public void setAverage(Double average) {
        this.average = average;
    }
    public Double getStd() {
        return this.std;
    }
    
    public void setStd(Double std) {
        this.std = std;
    }
    public Integer getLIST_POS() {
        return this.LIST_POS;
    }
    
    public void setLIST_POS(Integer LIST_POS) {
        this.LIST_POS = LIST_POS;
    }




}


