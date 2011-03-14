package ctd.services.exceptions;

/**
 * This exception is thrown when the user isn't authorized to view the resource
 * that was requested.
 * The difference between 401 and 403 is that with a 403 a user can be authorized
 * to do requests, but not for a specific item.
 *
 * template from http://www.seasite.niu.edu/cs580java/testexception.htm
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */

public class Exception403Forbidden extends Exception
{
  String strMistake;

//----------------------------------------------
// Default constructor - initializes instance variable to unknown

  public Exception403Forbidden()
  {
    super();             // call superclass constructor
    strMistake = "Forbidden";
  }


//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.

  public Exception403Forbidden(String strErr)
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

