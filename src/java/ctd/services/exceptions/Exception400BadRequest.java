package ctd.services.exceptions;

/**
 * This exception is thrown when the request isn't formatted according to
 * the specification.
 *
 * template from http://www.seasite.niu.edu/cs580java/testexception.htm
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */

public class Exception400BadRequest extends Exception
{
  String strMistake;

//----------------------------------------------
// Default constructor - initializes instance variable to unknown

  public Exception400BadRequest()
  {
    super();             // call superclass constructor
    strMistake = "Bad request";
  }


//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.

  public Exception400BadRequest(String strErr)
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

