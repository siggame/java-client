<%
  rerun_for('model,models')
%>

import org.json.*;

% if model.parent:
public class ${model.name} inherits ${model.parent.name}
% else:
public class ${model.name} inherits GameObject
{

}
