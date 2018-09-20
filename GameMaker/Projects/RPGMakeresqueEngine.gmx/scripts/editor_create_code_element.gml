/// editor_create_code_element(dialogue_parent);

var dialogue_parent = argument0;

var code_element = instance_create(0, 0, objDialogueEditorCode);
code_element._parentDialogue = dialogue_parent;

// Key
code_element._key = dialogue_parent._name + "_" + string(objDialogueEditorCodeEditor._maxKeyIndex);
objDialogueEditorCodeEditor._maxKeyIndex++;

ds_list_add(dialogue_parent._elements, code_element);

return code_element;
