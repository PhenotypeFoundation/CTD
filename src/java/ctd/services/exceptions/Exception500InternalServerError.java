package ctd.services.exceptions;

/**
 * This exception is thrown when there is an error in a file that can't be related
 * to any strMistake of the user
 *
 * template from http://www.seasite.niu.edu/cs580java/testexception.htm
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */

public class Exception500InternalServerError extends Exception
{
  String strMistake;

//----------------------------------------------
// Default constructor - initializes instance variable to unknown

  public Exception500InternalServerError()
  {
    super();             // call superclass constructor
    strMistake = "Internal Server Error";
  }


//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.

  public Exception500InternalServerError(String strErr)
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

