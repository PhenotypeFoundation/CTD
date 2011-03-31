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
package ctd.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;



/**
 *
 * @author kerkh010
 */
public class Statistics {

    private ArrayList<Double> data = new ArrayList<Double>();
    

    public Statistics() {
    }

    public Double getHigh(){
        Collections.sort(data);
        Double value = data.get(0);
        return value;
    }

    public Double getLow(){
        Collections.sort(data);
        int s = data.size();
        Double value = data.get(s-1);
        return value;
    }

    public Double getAverage(){
        Double average = null;
        ArrayList<Double> dataset = getData();

        //summation
        double sum = 0.0D;
        Iterator it1 = dataset.iterator();
        while (it1.hasNext()){
            Double value = (Double) it1.next();
            sum = sum + value;
        }
        average = sum / dataset.size();
        
        return average;
    }

    public Double getSTD() {
        Double std = null;
        Double temp_value = 0D;
        
        ArrayList<Double> values = getData();
        Double average = getAverage();
        
        for (int i = 0; i < values.size(); i++) {
            Double exp = values.get(i);
            temp_value = temp_value + ((exp - average) * (exp - average));
        }

        Double temp_value1 = temp_value / values.size();
        std = Math.sqrt(temp_value1);

        return std;
    }

    /**
     * @return the data
     */
    public ArrayList<Double> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList<Double> data) {
        this.data = data;
    }

}
