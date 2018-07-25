#define variable
/** VARIABLE
 *
 *  "global" game variables and events
 *
 *
 */

#define variable_system_create
/// variable_system_create();
// Creates a variable system

var system = ds_map_create();

#define variable_set_ext
/// variable_set_ext(variable_system, key, value);
// Sets a game variable, specifying a variable system.

var system = argument0,
    key    = argument1,
    value  = argument2;

ds_map_add(system, key, value);

#define variable_get_ext
/// variable_get_ext(variable_system, key);
// Gets a game variable, specifying a variable system.

var system = argument0,
    key    = argument1;

if (ds_map_exists(system, key)) {
    return ds_map_find_value(system, key);
}
return 0;

#define variable_system_destroy
/// variable_system_destroy(variable_system);

var system = argument0;
ds_map_destroy(system);
#define variable_set
/// variable_set(key, value);
// Sets a variable in our global variable system

var key   = argument0,
    value = argument1;

variable_set_ext(VARIABLE_SYSTEM, key, value);

#define variable_get
/// variable_get(key); 
// Gets a variable from our global variable system

var key   = argument0;

variable_get_ext(VARIABLE_SYSTEM, key);
