/// editor_choice_get_target(choice_element);
var element = argument0;

var text = element._text;
var dialogueName = element._parent._parentDialogue._name;

return dialogueName + "_" + text;
