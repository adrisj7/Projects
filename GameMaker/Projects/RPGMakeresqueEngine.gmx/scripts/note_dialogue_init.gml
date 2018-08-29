/// note_dialogue_init(dialogueFile, dialogueID);
// MUST be called from an objNote create event

var dialogueFile = argument0,
    dialogueID   = argument1;

event_inherited();
_dialogueFile = dialogueFile;
_dialogueID = dialogueID;
