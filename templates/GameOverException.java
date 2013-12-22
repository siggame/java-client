public class GameOverException extends Exception
{
  public int winner;
  public String reason;

  public GameOverException(int winner, String Reason)
  {
    this.winner = winner;
    this.reason = reason;
  }
}
