/// parse_choice(key, option_index);

var key = argument0,
    sel = argument1;

//show_message(key + " : " + string(sel));

// Initialize ALL text boxes used here
if !instance_exists(CHOICE_MISC_CONTAINER) {
    CHOICE_MISC_CONTAINER = instance_create(0, 0, objContainer);
    with (CHOICE_MISC_CONTAINER) {
        macheteDialogue0 = dialogue_text_create("You picked up the machete");
        macheteDialogue0a = dialogue_text_create("... You already threw away the machete...");
        macheteDialogue1   = dialogue_choice_create("pick_machete throw", "Really?", "No", "Yes");
        macheteDialogue1_2 = dialogue_text_create("You threw the machete away...", "...<wait 50>?", "Why would you do that?");
    }
}

// To make it easier (kinda like "using namespace" lmao)
with (CHOICE_MISC_CONTAINER) {
    switch key {

        // Pick up machete?
        case "pick_machete":
            switch sel {
                case 0: // Pick up!
                    if variable_get("machete throw away") {
                        dialogue_start(macheteDialogue0a);
                    } else {
                        dialogue_start(macheteDialogue0);
                    }
                    break;
                case 1: // Throw away!
                    dialogue_start(macheteDialogue1);
                    break;
                case 2: // Don't
                    break;
            }
            break;

        // Really throw away the machete?
        case "pick_machete throw":
            switch sel {
                case 0: // No
                    break;
                case 1: // Yes
                    dialogue_start(macheteDialogue1_2);
                    variable_set("machete throw away", true);
                    break;
            }
            break;

        default:
            show_error("CHOICE PARSING ERROR: KEY " + key + " NOT RECOGNIZED!", false);
    }
}
