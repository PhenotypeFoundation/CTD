package ctd.services.exceptions;

/**
 * This exception is thrown when the resource that is requested doesn't exist
 *
 * template from http://www.seasite.niu.edu/cs580java/testexception.htm
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */

public class Exception404ResourceNotFound extends Exception
{
  String strMistake;

//----------------------------------------------
// Default constructor - initializes instance variable to unknown

  public Exception404ResourceNotFound()
  {
    super();             // call superclass constructor
    strMistake = "Resource not found";
  }


//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.

  public Exception404ResourceNotFound(String strErr)
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

