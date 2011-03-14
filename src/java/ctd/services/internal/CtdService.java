package ctd.services.internal;

import ctd.services.exceptions.*;
import ctd.services.getTicket;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;

/**
 * This service is used to process incoming REST calls
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class CtdService {

    /**
     * ProcessRestCall          receives the name of a RESTcall and the query
     *                          that is being posed to the RESTful service
     *
     * @param strRestService    the String that indicates what RESTful service
     *                          should be called
     * @param strQuerystring    the String that contains the parameters that
     *                          will be passed to the RESTful service
     * @return strRet           the String[] that contains the HTTP-status code
     *                          on the first position, and the response to the
     *                          query in the second position
     * @throws Exception        any Exception that is not handled in this
     *                          function
     */
    public String[] ProcessRestCall(String strRestService, String strQuerystring) throws Exception {
        String[] strRet = new String[2];

        try {
            // Decoding the Querystring, sorry for the ugly hack
            URI objURI = new URI("http://www.example.com/dummy?"+strQuerystring);
            strQuerystring = objURI.getQuery();

            String strClassBase = "ctd.services.";

            Class clsClass = Class.forName(strClassBase+strRestService); // Classname of the RESTful service
            Object objClassMethods = clsClass.newInstance(); // To invoke methods with
            Object objClassInstance = clsClass.newInstance(); // To invoke methods on

            // Obtain all 'set'-methods contained in the RESTful service and
            // build a hashmap that contains for each 'set'-method (key) the
            // required parameter type (value)
            Method[] arrMethods = clsClass.getMethods(); //Obtain methods
            HashMap<String, Class> mapParameterTypes = new HashMap<String, Class>();
            Class[] clsPar = null;
            for(int i = 0; i < arrMethods.length; i++){
                clsPar = arrMethods[i].getParameterTypes();
                if(clsPar.length==1 && arrMethods[i].getName().contains("set")) {
                    // Restrict to 'set'-methods with one parameter
                    mapParameterTypes.put(arrMethods[i].getName(), clsPar[0]);
                }
            }

            // Process the query string.
            // Split into different groups of 'name=value'
            String[] arrQueries = strQuerystring.split("&");
            for(int i=0; i<arrQueries.length; i++) {
                String[] arrSplit = arrQueries[i].split("=");

                arrSplit[0] = arrSplit[0].substring(0, 1).toUpperCase()+arrSplit[0].substring(1, arrSplit[0].length());

                // Set each 'name' with the appropriate 'value' if there is a
                // 'set'-function associated with the 'name', else thow a 400
                if(mapParameterTypes.containsKey("set"+arrSplit[0])) {
                   Object[] arguments = new Object[1];
                   arguments[0] = arrSplit[0];

                   // Cast parameter value to required type
                   Object[] objParam = determineArguments(mapParameterTypes.get("set"+arrSplit[0]).toString(),arrSplit[1]);

                   // Obtain and invoke 'set'-function
                   Method thisMethodSet = objClassMethods.getClass().getDeclaredMethod("set"+arrSplit[0], mapParameterTypes.get("set"+arrSplit[0]));
                   thisMethodSet.invoke(objClassInstance, objParam);

                } else {
                    //throw new Exception400BadRequest();
                    Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Supplied parameter '"+arrSplit[0]+"' is not known by the service "+strRestService+".");
                }
            }

            // Obtain and invoke RESTful-service method
            Class[] clsNull = null;
            Method thisMethodGo = objClassMethods.getClass().getDeclaredMethod(strRestService, clsNull);
            Object[] objNull = null;
            strRet = (String[]) thisMethodGo.invoke(objClassInstance, objNull);

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if(cause instanceof Exception307TemporaryRedirect){
                Exception307TemporaryRedirect eNew = (Exception307TemporaryRedirect) cause;
                strRet[0] = "307";
                strRet[1] = eNew.getError();
            } else if(cause instanceof Exception400BadRequest) {
                Exception400BadRequest eNew = (Exception400BadRequest) cause;
                strRet[0] = "400";
                strRet[1] = eNew.getError();
            } else if(cause instanceof Exception401Unauthorized){
                Exception401Unauthorized eNew = (Exception401Unauthorized) cause;
                strRet[0] = "401";
                strRet[1] = eNew.getError();
            } else if(cause instanceof Exception403Forbidden){
                Exception403Forbidden eNew = (Exception403Forbidden) cause;
                strRet[0] = "403";
                strRet[1] = eNew.getError();
            } else if(cause instanceof Exception404ResourceNotFound){
                Exception404ResourceNotFound eNew = (Exception404ResourceNotFound) cause;
                strRet[0] = "404";
                strRet[1] = eNew.getError();
            } else {
                strRet[0] = "500";
                strRet[1] = "Internal Server Error: Unknown InvocationTargetException"+" "+cause.toString();
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Internal Server Error: "+cause.toString()+"\n"+cause.getMessage());
            }
        } catch (Exception e) {
            //throw new Exception(e);
            strRet[0] = "500";
            strRet[1] = "Internal Server Error: CtdService";
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Internal Server Error: CtdService:\n"+e.getMessage());
        }

        return strRet;
    }

    static public Object[] determineArguments(String strType, String strValue){
        Object[] returnValues = new Object[1];
        if(strType.equals("int")){
            returnValues[0] = Integer.valueOf((String) strValue);
        } else if(strType.equals("double")){
            returnValues[0] = Double.valueOf((String) strValue);
        } else if(strType.equals("boolean")){
            if(strValue.toLowerCase().equals("true")){
                returnValues[0] = true;
            }
            if(strValue.toLowerCase().equals("false")){
                returnValues[0] = true;
            }
        } else {
            returnValues[0] = strValue;
        }
        return returnValues;
    }
}