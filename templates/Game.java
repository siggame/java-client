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
    Utility.send_string(serv_conn, Utility.login_json().toString());
    
    JSONObject result = wait_for(["success","failure"]);
    return (result.getString("type").equals("success"));
  }
  
  public boolean join_game ()
  {
    Utility.send_string(serv_conn, Utility.join_game_json().toString());
    
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
        Utility.send_string(serv_conn, Utility.end_turn_json().toString());
      }      
    }
  }
  
  public void get_log ()
  {
    Utility.send_string(serv_conn, Utility.get_log_json().toString());
    
    JSONObject result = wait_for(["success","failure"]);
    if (result.getString("type").equals("success"))
    {
      try
      {
        PrintWriter gamelog = new PrintWriter(game_name+".glog","UTF-8");
        gamelog.out.print(result.getJSONObject("args").getString("log"));
        gamelog.close();
      }
      catch (Exception e)
      {
        System.out.println("There was a problem creating a gamelog");
      }
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
  
  public boolean change_add (JSONObject change)
  {
    JSONObject values = change.getJSONObject("values");
    if (false) {} //So we can do an easy "else if" chain
% for model in models:
% if model.type == "Model":
    else if(change.getString("type").equals("${model.name}"))
    {
      ${model.name} newObject = new ${model.name}(serv_conn,this\
% for datum in model.data:
, values.get${type_convert_java_json(datum.type)}("${datum.name}")\
% endfor
);
      ai.${lowercase(model.plural)}.add(newObject);
    }
% endif
% endfor
    else
    {
        System.out.println("Attempted to add unknown model");
    }
    return true;
  }
  
   public boolean change_remove (JSONObject change)
  { 
    int remove_id = change.getInt("id");
    
% for model in models:
% if model.type == "Model":
    for (int i = 0; i < ai.${lowercase(model.plural)}.size(); i++)
    {
      if (ai.${lowercase(model.plural)}.get(i).id == change_id)
      {
        ai.${lowercase(model.plural)}.remove(i);
        return true;
      }
    }
% endif
% endfor
    return false;
  }
  
  public boolean change_update (JSONObject change)
  {
    int change_id = change.getInt("id");

% for model in models:
% if model.type == "Model":
    for (int i = 0; i < ai.${lowercase(model.plural)}.size(); i++)
    {
        if(ai.${lowercase(model.plural)}.get(i).id == change_id)
        {
% for datum in model.data:
          if(change.getJSONObject("changes").has("${datum.name}"))
          {
            ai.${lowercase(model.plural)}.get(i).${datum.name} = change.getJSONObject("changes").get${type_convert_java_json(datum.type)}("${datum.name}");
          }
% endfor
          return true;
        }
    }
% endif
% endfor
    return false;
  }
  
  public boolean change_global_update (JSONObject change)
  {
% for datum in globals:
    if(change.getJSONObject("values").has("${datum.name}"))
    {
      ai.${datum.name} = change.getJSONObject("values").get${type_convert_java_json(datum.type)}("${datum.name}");
    }
% endfor
    return true;
  }
  
  public boolean run ()
  {
    String game_over_message;
    if (! login())
      return false;
    if (! join_game())
      return false;
      
    recv_player_id();
    init_main();
    
    try
    {
      main_loop();
    }
    catch (GameOverException e)
    {
      if (e.winner == ai.my_player_id)
      {
        game_over_message = "You Win! - " + e.reason;
      }
      else
      {
        game_over_message = "You Lose! - " + e.reason;
      }
    }
    
    end_main();
    System.out.println(game_over_message);
    get_log();
    return true;
  }
}
