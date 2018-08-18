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

#define dialogue_file_start_dialogue
///dialogue_file_start_dialogue(dialogue_file, dialogue_id);
// Each dialogue has an ID, which is the same thing as the key of the dialogue in the json file.
// This function passes through a dialogue file and said ID/key, and starts said dialogue.

// A dialogue consists of a full "cutscene". This includes text messages, choices, ect. that are opened in sequence.

var dialogue_file = argument0,
    dialogue_id   = argument1;

// A sequence of all the dialogue "windows" in this dialogue
var dialogueSequence = dialogue_file[? dialogue_id];

// Initialize / Reset our dialogue handler
with (DIALOGUE_SYSTEM) {
    _dialogueSequence = dialogueSequence;
    _dialogueID = dialogue_id;
    _dialogueFile = dialogue_file;
    _dialogueIndex = 0;
    _dialogueCount = ds_list_size(dialogueSequence);
}

dialogue_system_next();

#define dialogue_system_next
///dialogue_system_next();
// Finishes the current dialogue and starts our next one

with (DIALOGUE_SYSTEM) {

    // Make our dialogue handlers inactive for now:
    _textHandler._active = false;
    //_choiceHandler._active = false;

    // If we're done
    if (_dialogueIndex >= _dialogueCount) {
        // Let the game know we're out of dialogue!
        return 0;
    }

    // The window data that we're currently reading from.
    var currentWindow = _dialogueSequence[| _dialogueIndex];
    var type = currentWindow[? "type"];
    switch(type) {
        case "text":
            // Initialize our dialogue text handler
            with (_textHandler) {
                _active = true;                                      // Make sure we're visible.
                _paused = false;                                     // Not paused (only paused during a choice menu)
                _finished = false;                                   // We start un-finished
                _transitionCounter = 0;                              // Start our transition from zero
                if (DIALOGUE_SYSTEM._dialogueIndex == 0) {           // Set our opening state, depending on whether we're at the start
                    _state = DIALOGUE_STATE.OPENING;
                } else {
                    _state = DIALOGUE_STATE.OPEN;
                }
                _end = false;
                if DIALOGUE_SYSTEM._dialogueIndex == DIALOGUE_SYSTEM._dialogueCount - 1 { // Should our text window close with an animation?
                    _end = true;
                } else {
                    var nextWindow = DIALOGUE_SYSTEM._dialogueSequence[| DIALOGUE_SYSTEM._dialogueIndex + 1];
                    var nextType = nextWindow[? "type"];
                    if nextType != "text" {
                        _end = true;
                    }
                }
                _rawText = currentWindow[? "text"];                  // Update raw text
                _commandData = parse_text_commands(_rawText);        // Parse command data
                _text = parse_text_commands_get_text(_commandData);  // Update real text
                _textCounter = 0;                                    // Start our text from 0
                _commandIndex = 0;                                   // Start from the 0th command
                _speeding = false;                                   // We're never speeding from the start
                _floatyNextCounter = 0;                              // Make sure our floaty transition starts properly

            }
            break;
        case "choice":
            // Initialize our dialogue choice handler
            with (_choiceHandler) {
                _dialogueFile = DIALOGUE_SYSTEM._dialogueFile;
                _active = true;
                _finished = false;                                   // We start unfinished
                _transitionCounter = 0;                              // Start our transition from zero
                _state = DIALOGUE_STATE.OPENING;                     // We start opening!
                _selected = 0;                                       // Our selection starts on the first object
                _text = currentWindow[? "text"];                     // The text above our choices
                _choices = currentWindow[? "choices"];               // Our choices
                _targets = currentWindow[? "targets"];               // Our dialogue targets
                _choices_count = ds_list_size(_choices);             // How many choices we have
                dialogue_choice_set_dimensions(_text, _choices);     // Configure the dimensions (size and position) of our choice handler
            }
            break;
        case "code":
            var key = currentWindow[? "key"];
            parse_choice(key);
            break;
        default:
            show_error('DIALOGUE SYSTEM PROBLEM: Invalid dialogue type: "' + type + '".', false);
            break;
    }

    _dialogueIndex++;
}

return 0;

#define dialogue_system_create
/// dialogue_system_create();
return instance_create(0, 0, objDialogueHandler);
#define dialogue_choice_set_dimensions
///dialogue_choice_set_dimensions(text, options);
// Configure the dimensions of our dialogue choice window handler.
// This automatically wraps the size to the text and the choices, leaving some buffer room.

var text = argument0,
    options = argument1;

// The choice handler
var d = DIALOGUE_SYSTEM._choiceHandler;
    
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

var windowTextWidth = max(maxWidth, d._window_min_width);

var textHeight = string_height_ext(
    text, 
    3 + font_get_size(font_menu), 
    windowTextWidth
);

// Our text width plus the buffer on both sides is our window width
var windowWidth =  windowTextWidth + 2*d._window_text_buffer;
// buffer + Text at the top + buffer + Options + buffer
var windowHeight = textHeight + (font_get_size(font_menu) + 10)*optioncount + 3*d._window_text_buffer;

var windowX = WINDOW_WIDTH / 2 - windowWidth / 2;
var windowY = d._window_center_ypos - windowHeight / 2;

d._window_xpos = windowX;
d._window_ypos = windowY;
d._window_width = windowWidth;
d._window_height = windowHeight;
d._max_text_width = windowTextWidth;
d._top_text_height = textHeight;

return d;