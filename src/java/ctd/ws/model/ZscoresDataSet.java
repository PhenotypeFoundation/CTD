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

import java.util.ArrayList;

/**
 *
 * @author kerkh010
 */
public class ZscoresDataSet {
    private Double average;
    private Double standardDeviation;
    private ArrayList<ProbeSetZscore> probeSetZscoreList;

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
     * @return the standardDeviation
     */
    public Double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * @param standardDeviation the standardDeviation to set
     */
    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    /**
     * @return the probeSetExpressionList
     */
    public ArrayList<ProbeSetZscore> getProbeSetZscoreList() {
        return probeSetZscoreList;
    }

    /**
     * @param probeSetExpressionList the probeSetExpressionList to set
     */
    public void setProbeSetZscoreList(ArrayList<ProbeSetZscore> probeSetZscoreList) {
        this.probeSetZscoreList = probeSetZscoreList;
    }
}
