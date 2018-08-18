/// editor_create_choice_element(dialogue_parent);

var dialogue_parent = argument0;

var choice_element = instance_create(0, 0, objDialogueEditorChoice);
choice_element._parentDialogue = dialogue_parent;

if !objDialogueEditor._isLoading {
    var text = get_string("Choice Text (ok if empty): ", "");
    choice_element._text = text;
}

ds_list_add(dialogue_parent._elements, choice_element);

return choice_element;
