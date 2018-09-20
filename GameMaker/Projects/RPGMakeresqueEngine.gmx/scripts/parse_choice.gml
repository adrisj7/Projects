/// run_dialogue_code(key);

var key = argument0;

switch(key) {

    // Special start and end tokens: WARNING: Everything in between will be changed by dialogue editor.

    //!START

    case "Sweet Bread Use_Eat it_1":
        inventory_remove_search_item(ITEM.SWEET_BREAD);
        break;
    //!END

    default:
        show_error('Run Dialogue Code problem: No action for key "' + key + '" defined.', false);
        break;
}
