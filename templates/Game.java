import java.net.*;
import org.json.*;

public class Game
{
  public Socket serv_conn;
  public String game_name;
  public AI ai;

  public Game (Socket conn, String name, int verbosity)
  {
    NetworkUtility.verbosity = verbosity;
    serv_conn = conn;
    game_name = name;
    ai = new AI();
    //Don't know about this passing the connection to the ai business in the python template
  }
  
  public JSONObject receive ()
  {
    JSONObject message = new JSONObject(NetworkUtility.receive_string(serv_conn));
    
    if (message.getString("type").equals("changes"))
    {
      update_game(message);
    }
    else if (message.getString("type").equals("player_id"))
    {
      ai.my_player_id = message.getJSONObject("args").getInt("id");
    }
    else if (message.getString("type").equals("game_over"))
    {
      throw GameOverException(message.getJSONObject("args").getInt("winner"), message.getJSONObject("args").getString("reason"));
    }
    
    return message
  }
  
  public JSONObject wait_for(String[] types)
  {
    while (true)
    {
      JSONObject = receive();
      for (int i = 0; i < types.length; i++)
      {
        if (message.getString("type").equals(types[i]))
        {
          return message;
        }
      }
    }
  }
  
  public boolean login ()
  {
    
  }
}
