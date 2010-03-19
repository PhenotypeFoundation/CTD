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

/**
 *
 * @author kerkh010
 */
public class ChipAnnotation implements Serializable{
    private Integer id;
    private Integer chipId;
    private String probeset;
    private String geneAccession;
    private String geneSymbol;
    private String geneAnnotation;

    private Chip chip;

    private Integer LIST_POS;

    public ChipAnnotation() {
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
     * @return the chipId
     */
    public Integer getChipId() {
        return chipId;
    }

    /**
     * @param chipId the chipId to set
     */
    public void setChipId(Integer chipId) {
        this.chipId = chipId;
    }

    /**
     * @return the probeset
     */
    public String getProbeset() {
        return probeset;
    }

    /**
     * @param probeset the probeset to set
     */
    public void setProbeset(String probeset) {
        this.probeset = probeset;
    }

    /**
     * @return the geneAccession
     */
    public String getGeneAccession() {
        return geneAccession;
    }

    /**
     * @param geneAccession the geneAccession to set
     */
    public void setGeneAccession(String geneAccession) {
        this.geneAccession = geneAccession;
    }

    /**
     * @return the geneSymbol
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @param geneSymbol the geneSymbol to set
     */
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
     * @return the geneAnnotation
     */
    public String getGeneAnnotation() {
        return geneAnnotation;
    }

    /**
     * @param geneAnnotation the geneAnnotation to set
     */
    public void setGeneAnnotation(String geneAnnotation) {
        this.geneAnnotation = geneAnnotation;
    }

    /**
     * @return the LIST_POS
     */
    public Integer getLIST_POS() {
        return LIST_POS;
    }

    /**
     * @param LIST_POS the LIST_POS to set
     */
    public void setLIST_POS(Integer LIST_POS) {
        this.LIST_POS = LIST_POS;
    }

    /**
     * @return the chip
     */
    public Chip getChip() {
        return chip;
    }

    /**
     * @param chip the chip to set
     */
    public void setChip(Chip chip) {
        this.chip = chip;
    }



}
