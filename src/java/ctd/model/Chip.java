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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kerkh010
 */
public class Chip implements Serializable{
    private Integer id;
    private String name;
    private String timestamp;
    private String dbname;
    private Integer taxId;
    private List<ChipAnnotation> chipAnnotation = new ArrayList<ChipAnnotation>();


    public Chip() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the database
     */
    public String getDbname() {
        return dbname;
    }

    /**
     * @param database the database to set
     */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * @return the taxId
     */
    public Integer getTaxId() {
        return taxId;
    }

    /**
     * @param taxId the taxId to set
     */
    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }

    /**
     * @return the chipAnnotation
     */
    public List<ChipAnnotation> getChipAnnotation() {
        return chipAnnotation;
    }

    /**
     * @param chipAnnotation the chipAnnotation to set
     */
    public void setChipAnnotation(List<ChipAnnotation> chipAnnotation) {
        this.chipAnnotation = chipAnnotation;
    }
}
