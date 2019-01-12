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

dialogue_deactivate(d);
//instance_deactivate_object(d);

return d;

#define dialogue_set_next
/// dialogue_set_next(dialogue, next...);
// Sets the next dialogue of a dialogue.
// Once the first dialogue is finished, the next one is started.

// You can chain these together. For instance:
// dialogue_set_next(start, 2nd, 3rd, 4th, ....);

var d = argument[0];

var prev = d;
for(var i = 1; i < argument_count; i++) {
    var current = argument[i];
    prev._next = current;

    // Update start and end for the current dialogue and the next.
    if (prev.object_index == objDialogueText && current.object_index == objDialogueText) {
        prev._end = false;
        current._start = false;
    }

    // Make sure we extend off of any dialogue chains, instead of interrupting them!
    prev = dialogue_get_last(current);//current;
}

return d;

/*
var d    = argument[0];
    next = argument1;


// Update start and end for the current dialogue and the next.
if (d.object_index == objDialogueText && next.object_index == objDialogueText) {
    d._end = false;
    next._start = false;
}

d._next = next;

return d;
*/

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

    
    dialogue_deactivate(d);
    
    prevD = d;
}

return d0;

#define dialogue_choice_create
/// dialogue_choice_create(key, text, ...);

// Create a dialogue choice.
// The key is used to determine how to handle the dialogue choices.
// The extra arguments

var key = argument[0];
var text = argument[1];

// TODO: Is this necessary for calculations? It should be...
draw_set_font(font_menu);

// Max width of the characters
var maxWidth = 0;

var d = instance_create(0, 0, objDialogueChoice);

d._text = text;
d._key = key;
d._option_count = argument_count - 2;
for(var i = 0; i < d._option_count; i++) {
    var choice = argument[i + 2];
    d._options[i] = choice;

    var w = string_width(choice);
    if w > maxWidth {
        maxWidth = w;
    }
}

// Configure window size plus text variables (see objDialogueChoice Create event 5)

var windowTextWidth = max(maxWidth, d._window_min_width);

var textHeight = string_height_ext(
    text, 
    3 + font_get_size(font_menu), 
    windowTextWidth
);

// Our text width plus the buffer on both sides is our window width
var windowWidth =  windowTextWidth + 2*d._window_text_buffer;
// buffer + Text at the top + buffer + Options + buffer
var windowHeight = textHeight + (font_get_size(font_menu) + 10)*d._option_count + 3*d._window_text_buffer;

var windowX = WINDOW_WIDTH / 2 - windowWidth / 2;
var windowY = d._window_center_ypos - windowHeight / 2;

d._window_xpos = windowX;
d._window_ypos = windowY;
d._window_width = windowWidth;
d._window_height = windowHeight;
d._max_text_width = windowTextWidth;
d._top_text_height = textHeight;

dialogue_deactivate(d);

return d;

#define dialogue_start
/// dialogue_start(dialogue);
// Starts a dialogue in game, opening a window and stuff.

var d = argument0;

dialogue_activate(d);

// Default constructor. 
d._finished = false; // We're not finished
d._state = DIALOGUE_STATE.OPENING; // we start at the OPENING stage.
d._transitionCounter = 0; // Start our transition from zero.

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
        d._floatyNextCounter = 0; // Make sure that the floaty next transition starts properly!
        // Don't re-open
        if (!d._start) {
            d._state = DIALOGUE_STATE.OPEN;
        }
        break;

    case objDialogueChoice:
        d._selected = 0;
        break;

    default:
        show_error("DIALOGUE PROBLEM! You forgot to define the initialize action "
        + " for the dialogue of object " + object_get_name(d.object_index) + ".", false);
}

#define dialogue_deactivate
/// dialogue_deactivate(dialogue);
var d = argument0;

d.visible = false;
//instance_deactivate_object(d);

#define dialogue_activate
/// dialogue_activate(dialogue);
var d = argument0;

d.visible = true;
//instance_activate_object(d);

#define dialogue_get_last
/// dialogue_get_last(root);
// Gets the last dialogue in a dialogue chain

var root = argument0;

var last = root;

while(last._next != noone) {
    last = last._next;
}

return last;