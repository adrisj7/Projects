#define parse_text_commands
/// parse_text_commands(string);
// Parses the commands in the text in a format that lets us use these commands.

/* EXAMPLE TEXTS with COMMANDS: 
 *      "Hi there!<wait 10> What a wonderful day we're having."
 *      "So I decided I'd follow you for laughs.<next> Incidentally, where the heck you'd come from?"
 */
 
/* COMMAND LIST:
 *
 *      <wait [time]>               :           Wait for [time] ticks before moving on to the next text
 *      <next>                      :           Wait for the user to press action before moving on
 *      <speed [speed_factor]       :           Scale our text speed by a certain factor
 *      <skip>                      :           Instantly fill all of the text after this command, until we hit a </skip>
 *      </skip>                     :           Stop skippig (see above ^)
 */

var text = argument0;

if game_is_debug() {
    show_debug_message("     PARSING TEXT: " + text);
}

var result = ds_map_create();
var commands = ds_list_create();

var length = string_length(text);

// The text, without commands
var commandlessText = "";
// What index we're in, excluding commands
var commandlessIndex = 0;

for(var i = 0; i < length; i++) {
    var char = string_char_at(text, i + 1);
    // If we're starting a command
    if (char == "<") {
        var commandNameEnd = string_search(text, i, " ");
        var commandArgEnd = string_search(text, i, ">");

        var commandName;
        var commandArg;
        var commandNameLength;
        // If there are no arguments:
        if (commandArgEnd < commandNameEnd) {
            commandArg = "";
            commandNameLength = commandArgEnd - i;
        } else {
            commandNameLength = commandNameEnd - i;
            var commandArgLength = commandArgEnd - commandNameEnd - 1;
            commandArg = string_copy(text, commandNameEnd + 2, commandArgLength);
        }
        commandName = string_copy(text, i + 2, commandNameLength - 1);

        var commandData = ds_map_create();
        commandData[? "name"] = commandName;
        commandData[? "index"] = commandlessIndex;
        commandData[? "arg"] = commandArg;
        commandlessIndex--; // ?? Here to fix an off-by-one accumulation error.
        // For example: If there are 3 commands before our current command, our current command will be 3 characters off.

        ds_list_add(commands, commandData);        
        ds_list_mark_as_map(commands, ds_list_size(commands) - 1);

        // example: command[? 10] = {"wait", "60"}.
        // At line 10, wait 60 frames.
        //ds_map_add_list(commands, commandlessIndex, commandData);
        //commands[? commandlessIndex] = commandData;


        if game_is_debug() {
            show_debug_message("-> COMMAND NAME: " + commandName);
            show_debug_message("   COMMAND ARG: " + commandArg);
            show_debug_message("   COMMAND INDEX: " + string(commandlessIndex));
        }

        i = commandArgEnd;
    } else {
        commandlessText += char;
    }
    commandlessIndex++;
}

result[? "text"] = commandlessText;
ds_map_add_list(result, "commands", commands);
//result[? "commands"] = commands;

return result;

#define string_search
/// string_search(string, startIndex, token);
// Searches for a token within a string, returning the index of the first letter the token was found in.

var str   = argument0,
    start = argument1,
    token = argument2;

var length = string_length(str);
var tokenLength = string_length(token);

if (tokenLength <= 0) {
    show_error("STRING SEARCH PROBLEM: Token Length can't be zero!", false);
    return -1;
}

// Note, less than or equal to on purpose.
for(var i = start; i <= length - tokenLength; i++) {
    var copied = string_copy(str, i+1, tokenLength);
    if (copied == token) {
        return i;
    }
}

return -1;

#define parse_text_commands_get_text
/// parse_text_commands_get_text(commandData);
// Reads the raw text from a given commandData

var data = argument0;

return data[? "text"];

#define parse_text_commands_get_command_name
/// parse_text_commands_get_command_name(commandData, commandIndex);

var data  = argument0,
    index = argument1;

// Nested maps are a pain hmm
var command = ds_list_find_value(data[? "commands"],  index);

if is_undefined(command) {
    return "";
}

return command[? "name"];

#define parse_text_commands_get_command_argument
/// parse_text_commands_get_command_argument(commandData, commandIndex);

var data  = argument0,
    index = argument1;

// Nested maps are a pain hmm
var command = ds_list_find_value(data[? "commands"],  index);
return command[? "arg"];

#define parse_text_commands_get_command_charindex
/// parse_text_commands_get_command_charindex(commandData, commandIndex);

var data  = argument0,
    index = argument1;

// Nested maps are a pain hmm

var command = ds_list_find_value(data[? "commands"],  index);

if is_undefined(command) {
    return -1;
}

return command[? "index"];

#define parse_text_commands_execute_command
/// parse_text_commands_execute_command(commandData, commandIndex);
// Parse the command at a certain index! To be used in objDialogueText.

var data  = argument0,
    index = argument1;

var commandName = parse_text_commands_get_command_name    (data, index);
var commandArg  = parse_text_commands_get_command_argument(data, index);

switch commandName {
    case "wait":
        _effect = DIALOGUE_TEXT_EFFECT.WAIT;
        _effect_wait_timer = real(commandArg);
        break;
    case "next":
        _effect = DIALOGUE_TEXT_EFFECT.NEXT;
        break;
    case "speed":
        _effect_speed_scale = real(commandArg);
        break;
    case "skip":
        // Skip to where the next "/skip" command is
        var name = parse_text_commands_get_command_name(data, index);
        for(var checkIndex = index + 1; name != ""; checkIndex++) {
            if (parse_text_commands_get_command_name(data, checkIndex) == "/skip") {
                _textCounter = parse_text_commands_get_command_charindex(data, checkIndex);
                break;
            }
        }
        break;
    case "/skip":
        // Do nothing, we're just here for show.
        break;
    default:
        show_error("COMMAND EXECUTING PROBLEM: Command not recognized: " + '"' + commandName + '"' + ", with arguments " + commandArg + ".", false);
        
}

//show_message("Command executed: " + commandName + ", args: " + commandArg);