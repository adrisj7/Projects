/// editor_bump_dialogue_element(element, bump);
// bump is how far up or down to move the element. (ex. -1 moves the element back one)

var element = argument0,
    bump    = argument1;

var parent = element._parentDialogue;

var list = parent._elements;

var index = ds_list_find_index(list, element);
//show_message("Current Index: " + string(index) + ", size? " + string(ds_list_size(list)));
ds_list_delete(list, index);

//show_message("Current Index: " + string(index) + ", size? " + string(ds_list_size(list)));

index += bump;
index = wrap(index, 0, ds_list_size(list) + 1);

ds_list_insert(list, index, element);
//show_message("Target Index: " + string(index) + ", size? " + string(ds_list_size(list)));

