/// editor_save();


if (objDialogueEditor._fileName == "") {
    objDialogueEditor._fileName = get_save_filename("Json File| *.json", "untitled.json");
}

var resultMap = ds_map_create();

ds_map_add_map(resultMap, "dialogues", ds_map_create());

// Coordinates
var coordMap = ds_map_create();
ds_map_add_map(resultMap, "editor coordinates", coordMap);

// Tabs
var tabList = ds_list_create();
ds_list_copy(tabList, objDialogueEditor._tabNames);
ds_map_add_list(resultMap, "editor tabs", tabList);

with(objDialogueEditorDialogue) {
    var dialogueData = ds_list_create();
    // Add all of our element data
    for(var i = 0; i < ds_list_size(_elements); i++) {
        var elementData = ds_map_create();

        var element = _elements[| i];
        switch (element.object_index) {
            case objDialogueEditorText:
                elementData[? "type"] = "text";
                elementData[? "text"] = element._text;
                break;
            case objDialogueEditorChoice:
                elementData[? "type"] = "choice";
                elementData[? "text"] = element._text;
                var choices = ds_list_create();
                var targets = ds_list_create();
                var choiceList = element._choices;
                for(var j = 0; j < ds_list_size(choiceList); j++) {
                    var choice = choiceList[| j];
                    ds_list_add(choices, choice._text);
                    // Only add a target if we're connected: Otherwise, add an empty string.
                    if choice._connected != noone {
                        ds_list_add(targets, editor_choice_get_target(choice));
                    } else {
                        ds_list_add(targets, "");                    
                    }
                }
                ds_map_add_list(elementData, "choices", choices);
                ds_map_add_list(elementData, "targets", targets);
                break;
            case objDialogueEditorCode:
                elementData[? "type"] = "code";                
                elementData[? "key"] = element._key;
                break;
            default:
                show_error("Saving problem: INVALID OBJECT ELEMENT: " + object_get_name(element.object_index) + ".", false);
                break;
        }
        ds_list_add(dialogueData, elementData);
        ds_list_mark_as_map(dialogueData, i);
    }
    ds_map_add_list(resultMap[? "dialogues"], _name, dialogueData);

    // Handle position data
    var posMap = ds_map_create();
    posMap[? "x"] = x;
    posMap[? "y"] = y;
    ds_map_add_map(coordMap, _name, posMap);
}


// Write our map into a json file
var file = file_text_open_write(objDialogueEditor._fileName);
file_text_write_string(file, json_encode(resultMap));
file_text_close(file);
ds_map_destroy(resultMap);

return objDialogueEditor._fileName;
