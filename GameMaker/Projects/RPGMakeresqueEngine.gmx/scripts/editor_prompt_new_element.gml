///editor_prompt_new_element(dialogue_id);

var dialogue_id = argument0;

var prompt = instance_create(0, 0, objDialogueEditorNewElement);

prompt._dialogueID = dialogue_id;

return prompt;
