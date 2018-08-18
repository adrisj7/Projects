/// editor_delete_dialogue_element(element);

var element = argument0;

var parent = element._parentDialogue;

var list = parent._elements;

var index = ds_list_find_index(list, element);
ds_list_delete(list, index);

instance_destroy(element);
