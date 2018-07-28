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

//instance_deactivate_object(d);

return d;

#define dialogue_set_next
/// dialogue_set_next(dialogue, next);
// Sets the next dialogue of a dialogue.
// Once the first dialogue is finished, the next one is started.

var d    = argument0
    next = argument1;


// Update start and end for the current dialogue and the next.
if (d.object_index == objDialogueText && next.object_index == objDialogueText) {
    d._end = false;
    next._start = false;
}

d._next = next;

return d;

#define dialogue_text_create
/// dialogue_text_create(...);
// Creates a dialogue text window


if (argument_count == 0) {
    show_error("DIALOGUE TEXT PROBLEM: You gave in no text arguments!", false);
}

var d0;
var prevD;

for(var i = 0; i < argument_count; i++) {
    var d = instance_create(0, 0, objDialogueText);
    if (i == 0) {
        d0 = d;
    } else {
        dialogue_set_next(prevD, d);
    }

    var text = argument[i];
    d._rawText = text;
    d._commandData = parse_text_commands(text);
    d._text = parse_text_commands_get_text(d._commandData);

    //instance_deactivate_object(d);

    prevD = d;
}

return d0;

#define dialogue_start
/// dialogue_start(dialogue);
// Starts a dialogue in game, opening a window and stuff.

var d = argument0;

instance_activate_object(d);

// Default constructor. 
// We're not finished and we start at the OPENING stage.
d._finished = false;
d._state = DIALOGUE_STATE.OPENING;


// Initialize all of our objects.
switch(d.object_index) {
    case objDialogue:
        // Do nothing, really. Just open the next dialogue immediately
        d._finished = true;
        break;
    case objDialogueText:
        d._textCounter = 0;       // Start our text from 0
        d._commandIndex = 0;      // Start from our first command
        d._speeding = false;      // You can't start speeding immediately
        d._transitionCounter = 0; // Start our transition from zero.
        d._floatyNextCounter = 0; // Make sure that the floaty next transition starts properly!
        // Don't re-open
        if (!d._start) {
            d._state = DIALOGUE_STATE.OPEN;
        }
        break;
    default:
        show_error("DIALOGUE PROBLEM! You forgot to define the initialize action "
        + " for the dialogue of object " + object_get_name(d.object_index) + ".", false);
}
