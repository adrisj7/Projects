#define dialogue
/** DIALOGUE
 *
 *
 *
 *
 *
 *
 *
 *
 */

#define dialogue_create
/// dialogue_create();
// Creates a dialogue object. It does not start the dialogue in game.

var d = instance_create(0, 0, objDialogue);

instance_deactivate_object(d);

return d;

#define dialogue_set_next
/// dialogue_set_next(dialogue, next);
// Sets the next dialogue of a dialogue.
// Once the first dialogue is finished, the next one is started.

var d    = argument0
    next = argument1;

    
d._next = next;

return d;

#define dialogue_text_create
/// dialogue_text_create(text);
// Creates a dialogue text window
var text = argument0;

var d = instance_create(0, 0, objDialogueText);
d._text = text;

instance_deactivate_object(d);

return d;

#define dialogue_start
/// dialogue_start(dialogue);
// Starts a dialogue in game, opening a window and stuff.

var d = argument0;

instance_activate_object(d);
d._finished = false;

// Initialize all of our objects.
switch(d.object_index) {
    case objDialogue:
        // Do nothing, really.
        d._finished = true;
        break;
    case objDialogueText:
        // Start our text from 0
        d._textIndex = 0;
        break;
    default:
        show_error("DIALOGUE PROBLEM! You forgot to define the initialize action "
        + " for the dialogue of object " + object_get_name(d.object_index) + ".", false);
}
