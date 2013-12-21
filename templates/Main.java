import java.net.*;

public class Main
{
  public static void main(String[] args)
  {
    char verbosity = 0;
    InetAddress conn_address = InetAddress.getByName("localhost");
    int conn_port = 19000;
    String game_name;
    
    //Parse Args
    int i = 0;
    try
    {
      while (i < args.length)
      {
        if (args[i].toLowerCase().equals("-a") || args[i].toLowerCase().equals("--address"))
        {
          conn_address = InetAddress.getByName(args[i+1]);
          i = i + 2;
        }
        else if (args[i].toLowerCase().equals("-p") || args[i].toLowerCase().equals("--port"))
        {
          conn_port = Integer.parseInt(args[i+1]);
          i = i + 2;
        }
        else if (args[i].toLowerCase().equals("-g") || args[i].toLowerCase().equals("--game"))
        {
          game_name = args[i+1];
          i = i + 2;
        }
        else if (args[i].toLowerCase().equals("-v") || args[i].toLowerCase().equals("--verbose"))
        {
          verbosity = 1;
          i = i + 1;
        }
        else if (args[i].toLowerCase().equals("-vv") || args[i].toLowerCase().equals("--very-verbose"))
        {
          verbosity = 2;
          i = i + 1;
        }
        else
        {
          System.out.println("Bad Argument Value");
          System.exit(1);
        }
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        System.out.println("Missing Argument Value");
        System.exit(1);
      }
    }
    
    //Create Connection
    try
    {
      Socket connection = new Socket(conn_address, conn_port);
    }
    catch (Exception e)
    {
      System.out.println("Failed to connect.");
      System.exit(1);
    }
    
    //Game
    Game game = new Game(connection, game_name, verbosity);
    game.run();
    
    //Close Connection
    connection.close();
  }
}
