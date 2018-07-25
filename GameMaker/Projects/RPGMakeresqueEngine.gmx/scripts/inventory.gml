#define inventory
/** INVENTORY
 *
 *
 *
 */

#define inventory_create
/// inventory_create();
// Creates an inventory to store our items

#define inventory_add_item_ext
/// inventory_add_item_ext(inventory, item);
// Adds an item to our inventory
var inv  = argument0,
    item = argument1;

#define inventory_get_item_ext
/// inventory_get_item_ext(inventory, index);
// Gets the item at a certain index
var inv   = argument0,
    index = argument1;

#define inventory_get_item_count_ext
/// inventory_get_item_count_ext(inventory, index);
// Gets the number of items (of the same type) at that index.
var inv   = argument0,
    index = argument1;

#define inventory_pop_item_ext
/// inventory_pop_item_ext(inventory, index);
// Removes an item from the inventory and returns its index.
// Useful when you want to "use" or "drop" an item.
var inv   = argument0,
    index = argument1;

#define inventory_add_item
/// inventory_add_item(item);
// Adds an item to our inventory
var item = argument0;
inventory_add_item_ext(INVENTORY_SYSTEM, item);

#define inventory_get_item
/// inventory_get_item(index);
// Gets the item at a certain index
var index = argument0;
return inventory_get_item_ext(INVENTORY_SYSTEM, index);

#define inventory_get_item_count
/// inventory_get_item_count(index);
// Gets the number of items (of the same type) at that index.
var index = argument0;
return inventory_get_item_count_ext(INVENTORY_SYSTEM, index);

#define inventory_pop_item
/// inventory_pop_item(index);
// Removes an item from the inventory and returns its index.
// Useful when you want to "use" or "drop" an item.
var index = argument0;
return inventory_pop_item_ext(INVENTORY_SYSTEM, index);