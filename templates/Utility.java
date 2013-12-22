import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class Utility
{
  public static int verbosity = 0;
  
  public static String receive_string(Socket conn)
  {
    //read 4 bytes into length
    byte[] length_data = byte[4];
    conn.getInputStream().read(length_data, 0, 4);
    //convert to int
    int length = ByteBuffer.wrap(length_data).getInt();
    
    //read message bytes
    byte[] message_data = byte[length];
    conn.getInputStream().read(message_data, 0, 4);
    String message = new String(message_data, "UTF-8");
    vv_print("Recieved: " + message);
    return message;
  }
  
  public static void send_string(Socket conn, String message)
  {
    vv_print("Sending: " + message);
    //encode message
    byte[] message_data = message.getBytes(Charset.forName("UTF-8"));
    //Make header
    byte[] length_data = ByteBuffer.allocate(4).putInt(message_data.length).array();
    //Concatenate Arrays
    byte[] sent_data = Arrays.copyOf(length_data, length_data.length + message_data.length);
    System.arraycopy(message_data, 0, sent_data, length_data.length, message_data.length);
    //send
    conn.getOutputStream().write(sent_data);
  }
  
  public static void vv_print(String message)
  {
    if (verbosity > 1)
    {
      System.out.println(message);
    }
  }
  
  public static void v_print(String message)
  {
    if (verbosity >= 1)
    {
      System.out.println(message);
    }
  }

}
