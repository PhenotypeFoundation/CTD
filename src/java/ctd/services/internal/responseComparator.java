package ctd.services.internal;

import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Tjeerd van Dijk
 */

public class responseComparator implements Comparator {

    private String strMapKey = "";

    /**
     * @param strIn: the field in the map you want to use to sort the LinkedList
     */
    public responseComparator(String strIn) {
        strMapKey = strIn;
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
        if(map0.get(strMapKey).compareTo(map1.get(strMapKey))<0) {
            return -1;
        } else if(map0.get(strMapKey).equals(map1.get(strMapKey))) {
            return 0;
        } else {
            return 1;
        }
    }
}
