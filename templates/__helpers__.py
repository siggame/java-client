def type_convert_java_json(type):
    if type == int:
        return "Int"
    elif type == float:
        return "Double"
    elif type == str:
        return "String"
        
def type_convert_java_type(type):
    if type == int:
        return "int"
    elif type == float:
        return "double"
    elif type == str:
        return "String"
