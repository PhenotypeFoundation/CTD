package ctd.services.exceptions;

/**
 * This exception is thrown when the user isn't authorized to do a request.
 * The difference between 401 and 403 is that with a 403 a user can be authorized
 * to do requests, but not for a specific item.
 *
 * template from http://www.seasite.niu.edu/cs580java/testexception.htm
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */

public class Exception401Unauthorized extends Exception
{
  String strMistake;

//----------------------------------------------
// Default constructor - initializes instance variable to unknown

  public Exception401Unauthorized()
  {
    super();             // call superclass constructor
    strMistake = "Unauthorized";
  }


//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.

  public Exception401Unauthorized(String strErr)
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

