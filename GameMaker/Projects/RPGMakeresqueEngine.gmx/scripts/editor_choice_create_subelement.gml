/// editor_choice_create_subelement(parent, text, target);

var parent = argument0,
    text   = argument1;
    //target = argument2;

var newElement = instance_create(0, 0, objDialogueEditorChoiceElement);
newElement._parent = parent;
newElement._text = text;
//newElement._target = target;
ds_list_add(parent._choices, newElement);
