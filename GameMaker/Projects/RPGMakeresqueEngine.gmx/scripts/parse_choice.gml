/// run_dialogue_code(key);

var key = argument0;

switch(key) {

    // Special start and end tokens: WARNING: Everything in between will be changed by dialogue editor.

    //!START
    
    case "dialogue key 1":
        
        break;
    case "code0":
        // Do the thing
        show_message("OI");
        break;
    //!END

    default:
        show_error('Run Dialogue Code problem: No action for key "' + key + '" defined.', false);
        break;
}
