/// editor_new();
// Clears everything

with (objDialogueEditorDialogue) {
    instance_destroy();
}

with (objDialogueEditor) {
    instance_destroy();
}
instance_create(0, 0, objDialogueEditor);

//room_restart();

