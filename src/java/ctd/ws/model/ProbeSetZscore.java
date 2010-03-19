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
public class ProbeSetZscore {
    private Double zScore;
    private String probeSetName;

    /**
     * @return the zScore
     */
    public Double getZScore() {
        return zScore;
    }

    /**
     * @param zScore the zScore to set
     */
    public void setZScore(Double zScore) {
        this.zScore = zScore;
    }

    /**
     * @return the probeSetName
     */
    public String getProbeSetName() {
        return probeSetName;
    }

    /**
     * @param probeSetName the probeSetName to set
     */
    public void setProbeSetName(String probeSetName) {
        this.probeSetName = probeSetName;
    }
}
