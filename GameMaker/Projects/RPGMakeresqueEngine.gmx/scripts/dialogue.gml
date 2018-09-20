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

#define dialogue_system_create
/// dialogue_system_create();
return instance_create(0, 0, objDialogueHandler);
#define load_dialogue_file
/// load_dialogue_file(dialogueName);
// Loads a dialogue file, returning a map of dialogues.

var fname = working_directory + "" + argument0 + ".json";

// The file containing our dialogue
var file = file_text_open_read(fname);
if (file == -1) {
    show_error("Dialogue File reading problem: Unable to read dialogue file at " + fname + ".", false);
}

// Read file text
var text = file_text_read_string_full(file);
file_text_close(file);

// Parse the text json
var dmap = json_decode(text);
if (dmap == -1) {
    show_error("Dialogue file reading problem: Unable to parse json!", false);
}

// Return the "root" of all dialogues
return dmap[? "dialogues"];

#define dialogue_file_start_dialogue_ext
///dialogue_file_start_dialogue_ext(dialogue_file, dialogue_id);
// Each dialogue has an ID, which is the same thing as the key of the dialogue in the json file.
// This function passes through a dialogue file and said ID/key, and starts said dialogue.

// A dialogue consists of a full "cutscene". This includes text messages, choices, ect. that are opened in sequence.

var dialogue_file = argument0,
    dialogue_id   = argument1;

// A sequence of all the dialogue "windows" in this dialogue
var dialogueSequence = dialogue_file[? dialogue_id];

if is_undefined(dialogueSequence) {
    show_error(printf("Dialogue reading error: DIALOGUE UNDEFINED: %.", dialogue_id), false);
    return -1;
}

// Initialize / Reset our dialogue handler
with (DIALOGUE_SYSTEM) {
    _dialogueSequence = dialogueSequence;
    _dialogueID = dialogue_id;
    _dialogueFile = dialogue_file;
    _dialogueIndex = 0;
    _dialogueCount = ds_list_size(dialogueSequence);
}

game_set_state(GAME_STATE.DIALOGUE);

dialogue_system_next();

#define dialogue_file_start_dialogue
/// dialogue_file_start_dialogue(dialogue id);

var did = argument0;

var dialogue_file = DIALOGUE_SYSTEM._dialogues;

return dialogue_file_start_dialogue_ext(dialogue_file, did);

#define dialogue_system_next
///dialogue_system_next();
// Finishes the current dialogue and starts our next one

with (DIALOGUE_SYSTEM) {

    // Make our dialogue handlers inactive for now:
    _textHandler._active = false;
    //_choiceHandler._active = false;

    // If we're done
    if (_dialogueIndex >= _dialogueCount) {
        // We're out of dialogue!
        game_set_state(GAME_STATE.GAME);
        return 0;
    }

    // The window data that we're currently reading from.
    var currentWindow = _dialogueSequence[| _dialogueIndex];
    var type = currentWindow[? "type"];
    var prevWindow = -1;
    if _dialogueIndex != 0 {
        prevWindow =    _dialogueSequence[| _dialogueIndex - 1];
    }
    switch(type) {
        case "text":
            // Initialize our dialogue text handler
            with (_textHandler) {
                dialogue_start_dialogue_text( currentWindow[? "text"] );
                // Set our opening state, depending on whether we're at the start
                if (prevWindow != -1 && prevWindow[? "type"] == "text") {////DIALOGUE_SYSTEM._dialogueIndex == 0) {
                    transition_open_instant(_transition);
                }
                // Should our text window close with an animation?
                if DIALOGUE_SYSTEM._dialogueIndex == DIALOGUE_SYSTEM._dialogueCount - 1 { 
                    _end = true;
                } else {
                    var nextWindow = DIALOGUE_SYSTEM._dialogueSequence[| DIALOGUE_SYSTEM._dialogueIndex + 1];
                    var nextType = nextWindow[? "type"];
                    if nextType != "text" {
                        _end = true;
                    }
                }
            }
            break;
        case "choice":
            // Initialize our dialogue choice handler
            with (_choiceHandler) {
                _dialogueFile = DIALOGUE_SYSTEM._dialogueFile;
                _active = true;
                _finished = false;                                   // We start unfinished
                _selected = 0;                                       // Our selection starts on the first object
                _chooser = chooser_create_from_ds_list(currentWindow[? "choices"]);
                chooser_set_text(_chooser, currentWindow[? "text"]);
                chooser_open(_chooser);
                //_text = currentWindow[? "text"];                     // The text above our choices
                //_choices = currentWindow[? "choices"];               // Our choices
                _targets = currentWindow[? "targets"];               // Our dialogue targets
                //_choices_count = chooser_get_choice_count(_chooser); // How many choices we have
                dialogue_choice_set_dimensions(_chooser);     // Configure the dimensions (size and position) of our choice handler
                transition_open(_transition);
            }
            break;
        case "code":
            var key = currentWindow[? "key"];
            parse_choice(key);
            // Move on
            _dialogueIndex++;
            return dialogue_system_next();
            break;
        default:
            show_error('DIALOGUE SYSTEM PROBLEM: Invalid dialogue type: "' + type + '".', false);
            break;
    }

    _dialogueIndex++;
}

return 0;

#define dialogue_choice_set_dimensions
///dialogue_choice_set_dimensions(chooser);
// Configure the dimensions of our dialogue choice window handler.
// This automatically wraps the size to the text and the choices, leaving some buffer room.

var chooser = argument0;

var text    = chooser._text,
    options = chooser._choice;

// The choice handler
//var d = DIALOGUE_SYSTEM._choiceHandler;
    
// TODO: Is this necessary for calculations? It should be...
draw_set_font(font_menu);

// Max width of the characters
var maxWidth = 0;

var optioncount = ds_list_size(options);
for(var i = 0; i < optioncount; i++) {
    var choice = options[| i];
    var w = string_width(choice);
    if w > maxWidth {
        maxWidth = w;
    }
}

// Configure window size plus text variables (see objDialogueChoice Create event 5)

var windowTextWidth = max(maxWidth, chooser._window_min_width);

var textHeight = string_height_ext(
    text, 
    3 + font_get_size(font_menu), 
    windowTextWidth
);

// Our text width plus the buffer on both sides is our window width
var windowWidth =  windowTextWidth + 2*chooser._window_text_buffer;
// buffer + Text at the top + buffer + Options + buffer
var windowHeight = textHeight + (font_get_size(font_menu) + 10)*optioncount + 3*chooser._window_text_buffer;

var windowX = WINDOW_WIDTH / 2 - windowWidth / 2;
var windowY = chooser._window_center_ypos - windowHeight / 2;

chooser._window_xpos = windowX;
chooser._window_ypos = windowY;
chooser._window_width = windowWidth;
chooser._window_height = windowHeight;
chooser._max_text_width = windowTextWidth;
chooser._top_text_height = textHeight;

return chooser;

#define dialogue_start_dialogue_text
/// dialogue_start_dialogue_text(text);

var text = argument0;

game_set_state(GAME_STATE.DIALOGUE);

with (DIALOGUE_SYSTEM._textHandler) {
    _active = true;                                      // Make sure we're visible.
    _paused = false;                                     // Not paused (only paused during a choice menu)
    _finished = false;                                   // We start un-finished
    //_transitionCounter = 0;                              // Start our transition from zero
    //_state = DIALOGUE_STATE.OPENING;                     // Start opening
    _end = true;
    _rawText = text;                                     // Update raw text
    _commandData = parse_text_commands(_rawText);        // Parse command data
    _text = parse_text_commands_get_text(_commandData);  // Update real text
    _textCounter = 0;                                    // Start our text from 0
    _commandIndex = 0;                                   // Start from the 0th command
    _speeding = false;                                   // We're never speeding from the start
    _floatyNextCounter = 0;                              // Make sure our floaty transition starts properly
    transition_open(_transition);
}

#define chooser_create
/// chooser_create( choices... );


var list = ds_list_create();
for(var i = 0; i < argument_count; i++) {
    ds_list_add(list, argument[i]);
}

return chooser_create_from_ds_list(list);

#define chooser_create_from_ds_list
/// chooser_create_from_ds_list(list);

var list = argument0;

var chooser = instance_create(0, 0, objChooser);

chooser._choice = list;

return chooser;

#define chooser_open
/// chooser_open(chooser);
var chooser = argument0;

transition_open(chooser._transition);
chooser._selected = 0;
chooser._selectedFinal = -1;

#define chooser_get_selected
/// chooser_get_selected(chooser);
// Returns our final selection if it has been made, or -1 if we haven't made a selection yet.

var chooser = argument0;

if !instance_exists(chooser) || !transition_is_closed(chooser._transition) {
    return -1;
}

return chooser._selectedFinal;

#define chooser_get_choice_count
/// chooser_get_choice_count(chooser);
var chooser = argument0;

return ds_list_size(chooser._choice);

#define chooser_set_text
/// chooser_set_text(chooser, text);
var chooser = argument0,
    text    = argument1;

chooser._text = text;

#define chooser_destroy
/// chooser_destroy(chooser);
var chooser = argument0;

instance_destroy(chooser);

#define chooser_set_window_properties
/// chooser_set_window_properties(chooser, x, y, width, height);
var chooser = argument0,
    xa      = argument1,
    ya      = argument2,
    w       = argument3,
    h       = argument4;
    
// WARNING: Might be problematic, since min width and min height are variables...
chooser._window_xpos = xa;
chooser._window_ypos = ya;
chooser._window_width = w;
chooser._window_height = h;

#define chooser_close
/// chooser_close(chooser);
// Closes the chooser without triggering a select

var chooser = argument0;

if !transition_is_closed(chooser._transition)
    transition_close(chooser._transition);
chooser._selectedFinal = -1;