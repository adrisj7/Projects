#define note
/** Notes
 *
 *     Notes are things that you can interact with in the world.
 *          Most often this is just a text description of the object you're viewing
 *          But it can be other things, like sound effect triggers, buttons, or chairs.
 *
 */

#define note_text_init
/// note_text_init(text);
// MUST be called from an objNote create event

var text = argument0;

event_inherited();

_type = NOTE_TYPE.TEXT;
_data[? "text"] = text;

#define note_dialogue_init_ext
/// note_dialogue_init_ext(dialogueFile, dialogueID);
// MUST be called from an objNote create event

var dialogueFile = argument0,
    dialogueID   = argument1;

event_inherited();

_type = NOTE_TYPE.DIALOGUE;
_data[? "dialogueFile"] = dialogueFile;
_data[? "dialogueID"]   = dialogueID;

#define note_dialogue_init
/// note_dialogue_init(dialogueID);
// MUST be called from an objNote create event

return note_dialogue_init_ext(DIALOGUE_SYSTEM._dialogues, argument0);

#define note_locked_init
/// note_locked_init();

event_inherited();

_type = NOTE_TYPE.LOCKED;

#define note_set_direction
/// note_set_direction(note, direction);

var n = argument0,
    d = argument1;

n._direction = d;

#define note_create_empty
/// note_create_empty(x, y);

var xa = argument0,
    ya = argument1;

var n = instance_create(xa, ya, objNote);
n.image_xscale = 2;
n.image_yscale = 2;

return n;