package hospital.exceptions;

public class DuplicateUsernameException extends Exception {

  public DuplicateUsernameException(String username) {
    super(username);
  }
}
