import java.net.*;

//\
// @class BaseAI
//  @brief Class to store competitor-accessible data and functions
public class BaseAI
{
  public Socket connection;
  public String game_name;
  //\
  // @var my_player_id
  //  @breif The player_id of the competitor.
  public int my_player_id = 0;
% for datum in globals:
  //\
  // @var ${datum.name}
% if datum.doc:
  //  @brief ${datum.doc}
% endif
  public ${type_convert_java_type(datum.type)} ${datum.name};
% endfor
% for model in models:
% if model.type == "Model":
  //\
  // @var ${lowercase(model.plural)}
  //  @brief List containing all ${model.plural}.
  public ArrayList<${model.name}> ${lowercase(model.plural)};
% endif
% endfor

  public BaseAI () 
  {
  game_name = "${name}";
% for model in models:
% if model.type == "Model":
  ${lowercase(model.plural)} = new ArrayList<${model.name}>();
% endif
% endfor
  }

% for datum in globals:
  //\
  // @fn get_${datum.name}
  //  @breif Accessor function for ${datum.name}
  public ${type_convert_java_type(datum.type)} get_${datum.name} ()
  {
    return self.${datum.name}
  }
% endfor
}
