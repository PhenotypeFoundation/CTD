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

/**
 *
 * @author kerkh010
 */
public class Expression implements java.io.Serializable {

    private Integer chipAnnotationId;
    private Integer studySampleAssayId;
    private Double expression;


    public Expression() {
    }

    public Integer getChipAnnotationId() {
        return this.chipAnnotationId;
    }

    public void setChipAnnotationId(Integer chipAnnotationId) {
        this.chipAnnotationId = chipAnnotationId;
    }

    /**
     * @return the studySampleAssayId
     */
    public Integer getStudySampleAssayId() {
        return studySampleAssayId;
    }

    /**
     * @param studySampleAssayId the studySampleAssayId to set
     */
    public void setStudySampleAssayId(Integer studySampleAssayId) {
        this.studySampleAssayId = studySampleAssayId;
    }

    public Double getExpression() {
        return this.expression;
    }

    public void setExpression(Double expression) {
        this.expression = expression;
    }

}
