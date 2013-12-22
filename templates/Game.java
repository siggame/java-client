import java.net.*;
import java.io.*;
import org.json.*;

public class Game
{
  public Socket serv_conn;
  public String game_name;
  public AI ai;

  public Game (Socket conn, String name, int verbosity)
  {
    Utility.verbosity = verbosity;
    serv_conn = conn;
    game_name = name;
    ai = new AI();
    //Don't know about this passing the connection to the ai business in the python template
  }
  
  private JSONObject login_json (String username)
  {
    JSONObject json = new JSONObject();
    json.put("type","login");
    JSONObject args = new JSONObject();
    args.put("username",username);
    args.put("connection_type", "${repr(name)}");
    json.put("args",args);
    return json;
  }
  
  private JSONObject join_game_json ()
  {
    JSONObject json = new JSONObject();
    json.put("type","join_game");
    JSONObject args = new JSONObject();
    if (game_name != null)
    {
      args.put("game_name", game_name);
    }
    json.put("args",args);
    return json;
  }
  private JSONObject end_turn_json ()
  {
    JSONObject json = new JSONObject();
    json.put("type","end_turn");
    JSONObject args = new JSONObject();
    json.put("args",args);
    return json;
  }
  private JSONObject get_log_json ()
  {
    JSONObject json = new JSONObject();
    json.put("type"," get_log");
    JSONObject args = new JSONObject();
    json.put("args",args);
    return json;
  }
  
  public JSONObject receive ()
  {
    JSONObject message = new JSONObject(Utility.receive_string(serv_conn));
    
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
    Utility.send_string(serv_conn, login_json().toString());
    
    JSONObject result = wait_for(["success","failure"]);
    return (result.getString("type").equals("success"));
  }
  
  public boolean join_game ()
  {
    Utility.send_string(serv_conn, join_game_json().toString());
    
    JSONObject result = wait_for(["success","failure"]);
    if (result.getString("type").equals("success"))
    {
      game_name = result.getJSONObject("args").getString("name");
      System.out.println("Game created: " + game_name);
      return true;
    }
    return false; 
  }
  
  public void recv_player_id ()
  {
    wait_for(['player_id']);
  }
  
  public void init_main ()
  {
    wait_for(['start_game']);
    ai.init();
  }
  
  public void end_main ()
  {
    ai.end();
  }
  
  public void main_loop ()
  {
    while (true)
    {
      JSONObject message = self.wait_for(['start_turn', 'game_over']);
      if (message.getString("type").equals("game_over"))
      {  
        return;
      }
      if (ai.my_player_id == ai.player_id)
      {
        Utility.v_print("Turn Number: " + ai.turn_number);
        ai.run();
        Utility.send_string(serv_conn, end_turn_json().toString());
      }      
    }
  }
  
  public void get_log ()
  {
    Utility.send_string(serv_conn, get_log_json().toString());
    
    JSONObject result = wait_for(["success","failure"]);
    if (result.getString("type").equals("success"))
    {
      PrintWriter gamelog = new PrintWriter(game_name+".glog","UTF-8");
      gamelog.out.print(result.getJSONObject("args").getString("log"));
      gamelog.close();
    }
  }
  
  public boolean update_game (JSONObject message)
  {
    if (! message.getString("type").equals("changes"))
    {
      return false;
    }
    
    JSONArray changes = message.getJSONObject("args").getJSONArray("changes");    
    for (int i = 0; i < changes.length(); i++)
    {
      JSONObject change = changes.getJSONObject(i);
      if (change.getString("action").equals("add"))
      {
        change_add(change);
      }
      else if (change.getString("action").equals("remove"))
      {
        change_remove(change);
      }
      else if (change.getString("action").equals("update"))
      {
        change_update(change);
      }
      else if (change.getString("action").equals("global_update"))
      {
        change_global_update(change);
      }
    }
    return true;
  }
}
