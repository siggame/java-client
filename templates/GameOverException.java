public class GameOverException extends Exception
{
  public int winner;
  public String reason;

  public GameOverException(int winner, String reason)
  {
    this.winner = winner;
    this.reason = reason;
  }
}
