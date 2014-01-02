<%
  rerun_for('model,models')
%>

import java.net.*;
import org.json.*;

//\
// @class ${model.name}
% if model.doc:
//  @brief ${model.doc}
% endif

% if model.parent:
public class ${model.name} inherits ${model.parent.name}
% else:
public class ${model.name} inherits GameObject
{
% for datum in model.data:
  private ${type_convert_java_type(datum.type)} _${datum.name};
% endfor

  public ${model.name} (Socket connection, Game parent_game\
% for datum in model.data:
  , ${type_convert_java_type(datum.type)} ${datum.name}/
% endfor
  )
  _connection = connection;
  _parent_game = parent_game;
% for datum in model.data:
    _${datum.name} = ${datum.name};
% endfor

% for func in model.functions:
//\
// @fn ${func.name}
% if func.doc:
//  @brief ${func.doc}
% endif
% for args in func.arguments:
% if args.doc:
//  @param ${args.name} ${args.doc}
% endif
% endfor
  public boolean ${func.name}(\
% for args in func.arguments:
, ${type_convert_java_type(args.type)} ${args.name}\
% endfor
)
  {
    JSONObject function_call = Utility.function_call_json();

% for args in func.arguments:
    function_call.getJSONObject("args").put("${repr(args.name)}", ${args.name});
% endfor
    Utility.send_string(_connection, function_call.toString());

    boolean received_status = false;
    boolean status = false;
    while (! received_status)
    {
      JSONObject message = Utility.receive_string(_connection);

      if (message.getString("type").equals("success"))
      {
        received_status = true;
        status = true;
      }
      else if (message.getString("type").equals("failure"))
      {
        received_status = true;
        status = false;
      }
      else if (message.getString("type").equals("changes"))
      {
        _parent_game.update_game(message);
      }
    }
    return status;
% endfor
  }

% for local in model.locals:
  public ${type_convert_java_type(local.type)} ${local.name} ()
  {
    return _${local.name};
  }
% endif
% endfor
% for remote in model.remotes:
  public ${type_convert_java_type(remote.type)} ${remote.name} ()
  {
    return _${remote.through}.${remote.name}();
  }
% endfor
% for relation in model.relations:
  public ${relation.name} ${relation.name} ()
  {
    for (int i = 0; i < parent_game.ai.${lowercase(relation.plural)}.size(), i++)
    {
      if (parent_game.ai.${lowercase(relation.plural)}.get(i).id == _${relation.name}_id)
      {
        return parent_game.ai.${lowercase(relation.plural)}.get(i);
      }
    }
    return null;
% endfor
}
