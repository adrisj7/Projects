#define __datatypes
/// Datatypes! Useful for various things.

////////////////////////////////////////////////////////////////////////////////
// Stack and Queue (standard way to use a ds_list like a stack/queue)
////////////////////////////////////////////////////////////////////////////////

#define ds_list_queue_push
/// ds_list_queue_push(list, value)
    var list = argument0, value = argument1;
    ds_list_add(list, value);
#define ds_list_queue_front
/// ds_list_queue_front(list)
    var list = argument0;
    return ds_list_find_value(list, 0);
#define ds_list_queue_pop
/// ds_list_queue_pop(list)
    var list = argument0;
    ds_list_delete(list, 0);
#define ds_list_stack_push
/// ds_list_stack_push(list, value)
    var list = argument0, value = argument1;
    ds_list_add(list, value);
#define ds_list_stack_top
/// ds_list_stack_top(list)
    var list = argument0;
    return ds_list_find_value(list, ds_list_size(list) - 1);
#define ds_list_stack_pop
/// ds_list_stack_pop(list)
    var list = argument0;
    ds_list_delete(list, ds_list_size(list) - 1);

////////////////////////////////////////////////////////////////////////////////
// Pair (kinda stupid but good to have it consistent)
////////////////////////////////////////////////////////////////////////////////
#define pair_create
/// pair_create()
    return array_create(2);

#define pair_set_first
/// pair_set_first(pair, first)
    var pair = argument0, first = argument1;
    pair[@ 0] = first;

#define pair_set_second
/// pair_set_second(pair, second)
    var pair = argument0, second = argument1;
    pair[@ 1] = second;

#define pair_get_first
/// pair_get_first(pair)
    var pair = argument0;
    return pair[@ 0];

#define pair_get_second
/// pair_get_second(pair)
    var pair = argument0;
    return pair[@ 1];