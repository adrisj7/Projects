/// editor_create_text_element(dialogue_parent);

var dialogue_parent = argument0;

var text_element = instance_create(0, 0, objDialogueEditorText);
text_element._parentDialogue = dialogue_parent;

if !objDialogueEditor._isLoading {
    var text = get_string("Text: ", "");
    text_element._text = text;
}

ds_list_add(dialogue_parent._elements, text_element);

return text_element;
