/// editor_load()


// Clear everything
editor_new();

// We're loading
objDialogueEditor._isLoading = true;

var fname;

if argument_count == 0 {
    fname = get_open_filename("Json file|*.json","");
} else {
    fname = argument[0];
}

var file = file_text_open_read(fname);
var text = file_text_read_string_full(file);

var map = json_decode(text);

var dialogueMap = map[? "dialogues"];
var coordMap = map[? "editor coordinates"];
var tabList = map[? "editor tabs"];
var key = ds_map_find_first(dialogueMap);

while !is_undefined(key) {
    var dialogueElements = dialogueMap[? key];
    var coords = coordMap[? key];
    var d = instance_create(coords[? "x"], coords[? "y"], objDialogueEditorDialogue);
    //TODO: Tabs dialogueThing._tab = _tabNames[| _tabSelected];
    d._name = key;

    for(var i = 0; i < ds_list_size(dialogueElements); i++) {
        var elementMap = dialogueElements[| i];
        var type = elementMap[? "type"];
        switch type {
            case "text":
                var text = editor_create_text_element(d);
                text._text = elementMap[? "text"];
                break;
            case "choice":
                var choice = editor_create_choice_element(d);
                choice._text = elementMap[? "text"];
                var choices = elementMap[? "choices"];
                //var targets = elementMap[? "targets"];
                for(var j = 0; j < ds_list_size(choices); j++) {
                    var choiceText = choices[| j];
                    //var target = targets[| j];
                    editor_choice_create_subelement(choice, choiceText);
                }
                break;
            case "code":
                var code = editor_create_code_element(d);
                code._key = elementMap[? "key"];
                break;
            default:
                show_error('Editor Load problem: Invalid element type "' + type + '".', false);
                break;
        };
    }


    key = ds_map_find_next(dialogueMap, key);    
}

// Tabs
for(var i = 0; i < ds_list_size(tabList); i++) {
    objDialogueEditor._tabNames[| i] = tabList[| i];
}

// Clean up
ds_map_destroy(map);

objDialogueEditor._isLoading = false;
