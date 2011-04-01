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

package ctd.ws.model;

/**
 *
 * @author kerkh010
 */
public class ProbeSetExpressionInfo {
    private Double log2Value;
    private String localAccession;
    private String chipName;
    private String ticketPassword;
    private Double average;
    private Double STD;
    private String title;
    private String groupName;
    private Integer ticketId;

    /**
     * @return the log2Value
     */
    public Double getLog2Value() {
        return log2Value;
    }

    /**
     * @param log2Value the log2Value to set
     */
    public void setLog2Value(Double log2Value) {
        this.log2Value = log2Value;
    }

    /**
     * @return the localAccession
     */
    public String getLocalAccession() {
        return localAccession;
    }

    /**
     * @param localAccession the localAccession to set
     */
    public void setLocalAccession(String localAccession) {
        this.localAccession = localAccession;
    }

    /**
     * @return the chipName
     */
    public String getChipName() {
        return chipName;
    }

    /**
     * @param chipName the chipName to set
     */
    public void setChipName(String chipName) {
        this.chipName = chipName;
    }

    /**
     * @return the ticketPassword
     */
    public String getTicketPassword() {
        return ticketPassword;
    }

    /**
     * @param ticketPassword the ticketPassword to set
     */
    public void setTicketPassword(String ticketPassword) {
        this.ticketPassword = ticketPassword;
    }

    /**
     * @return the average
     */
    public Double getAverage() {
        return average;
    }

    /**
     * @param average the average to set
     */
    public void setAverage(Double average) {
        this.average = average;
    }

    /**
     * @return the STD
     */
    public Double getSTD() {
        return STD;
    }

    /**
     * @param STD the STD to set
     */
    public void setSTD(Double STD) {
        this.STD = STD;
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

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the ticketId
     */
    public Integer getTicketId() {
        return ticketId;
    }

    /**
     * @param ticketId the ticketId to set
     */
    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }
}
