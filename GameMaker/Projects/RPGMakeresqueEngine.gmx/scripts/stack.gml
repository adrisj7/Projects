#define stack
/* stack
 *      A stack datatype (just a ds list but accessed differently
 */

#define stack_create
/// stack_create()

return ds_list_create();

#define stack_push
/// stack_push(stack, value);
var s = argument0,
    v = argument1;

ds_list_add(s, v);

#define stack_peek
/// stack_peek(stack);
var s = argument0;

if ds_list_size(s) == 0 {
    show_debug_message("Error: Attempting to access stack that's empty. Will create problems");
}

return s[| ds_list_size(s) - 1];

#define stack_pop
/// stack_pop(stack);

var s = argument0;

var item = stack_peek(s);
ds_list_delete(s, ds_list_size(s) - 1);

return item;