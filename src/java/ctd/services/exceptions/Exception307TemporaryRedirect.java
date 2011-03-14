package ctd.services.exceptions;

import java.util.ResourceBundle;

/**
 * This exception is thrown when the request needs to be redirected, e.g. when
 * the user needs to login in GSCF
 *
 * template from http://www.seasite.niu.edu/cs580java/testexception.htm
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */

public class Exception307TemporaryRedirect extends Exception
{
  String strMistake;

//----------------------------------------------
// Default constructor - initializes instance variable to unknown

  public Exception307TemporaryRedirect()
  {
    super();             // call superclass constructor
    ResourceBundle res = ResourceBundle.getBundle("settings");
    strMistake = res.getString("gscf.baseURL");
  }


//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.

  public Exception307TemporaryRedirect(String strErr)
  {
    super(strErr);        // call super class constructor
    strMistake = strErr;  // save message
  }


//------------------------------------------------
// public method, callable by exception catcher. It returns the error message.

  public String getError()
  {
    return strMistake;
  }
}

