#define input
/** Input system
 *
 *
 *    Stores the various inputs we have. Can be used to change our input settings
 *
 */

#define input_system_create
/// input_system_create()

var input_system = ds_map_create();
return input_system;

#define input_get_ext
/// input_get_ext(input_system, input_key)
// Gets the keycode for a given input

var is  = argument0,
    key = argument1;

if (ds_map_exists(is, key)) {
    return is[? key];
}

// If our key doesn't exist, be sure to give some kind of a warning.
show_error("INPUT SYSTEM ERROR! Given key " + string(key) + " does not have a mapped input!", false);
return 0;

#define input_put_ext
/// input_put_ext(input_system, input_key, input_value);

var is  = argument0,
    key = argument1,
    val = argument2;

is[? key] = val;

#define input_get
/// input_get(key)

var key = argument0;

return input_get_ext(INPUT_SYSTEM, key);