package ctd.services.internal;

import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Tjeerd van Dijk
 */

public class responseComparator implements Comparator {

    private String strMapKey = "";
    private boolean blnIsDouble = false;

    /**
     * @param strIn: the field in the map you want to use to sort the LinkedList
     */
    public responseComparator(String strIn) {
        strMapKey = strIn;
    }

    /**
     * @param strIn: the field in the map you want to use to sort the LinkedList
     * @param blnIn: a boolean indicating if this field is an integer
     */
    public responseComparator(String strIn, boolean blnIn) {
        strMapKey = strIn;
        blnIsDouble = blnIn;
    }

    /**
     * This function is used to sort items in a list by comparing them to each other
     * @param arg0 the first object
     * @param arg1 the secont object
     * @return: -1 the first object should be placed before the second object
     *          0 the first object and the second object are the same
     *          1 the second object should be placed before the first object
     */
    @Override
    public int compare(Object arg0, Object arg1) {
        HashMap<String, String> map0 = (HashMap<String, String>)arg0;
        HashMap<String, String> map1 = (HashMap<String, String>)arg1;

        if(!blnIsDouble) {
            if(map0.get(strMapKey).compareTo(map1.get(strMapKey))<0) {
                return -1;
            } else if(map0.get(strMapKey).equals(map1.get(strMapKey))) {
                return 0;
            } else {
                return 1;
            }
        } else {
            double v0 = Double.valueOf(map0.get(strMapKey));
            double v1 = Double.valueOf(map1.get(strMapKey));
            if(v0<v1) {
                return -1;
            } else if(v0==v1) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
