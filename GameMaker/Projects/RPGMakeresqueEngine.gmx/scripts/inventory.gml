#define inventory
/** INVENTORY
 *
 *
 *
 */

#define inventory_create
/// inventory_create();
// Creates an inventory to store our items

return instance_create(0, 0, objInventorySystem);

#define inventory_add_item_ext
/// inventory_add_item_ext(inventory, item);
// Adds an item to our inventory
var inv  = argument0,
    item = argument1;

var items = inv._item;
var count = inv._item_count;

var search = ds_list_find_index(items, item);

// We found a search for an object
if search != -1 {
    count[| search] ++;
} else {
    ds_list_add(items, item);
    ds_list_add(count, 1);
}

#define inventory_get_item_ext
/// inventory_get_item_ext(inventory, index);
// Gets the item at a certain index
var inv   = argument0,
    index = argument1;

var items = inv._item;
return items[| index];

#define inventory_get_item_count_ext
/// inventory_get_item_count_ext(inventory, index);
// Gets the number of items (of the same type) at that index.
var inv   = argument0,
    index = argument1;

var count = inv._item_count;
return count[| index];

#define inventory_pop_item_ext
/// inventory_pop_item_ext(inventory, index);
// Removes an item from the inventory and returns its index.
// Useful when you want to "use" or "drop" an item.
var inv   = argument0,
    index = argument1;

var result = inventory_get_item_ext(inv, index);

var items = inv._item;
var count = inv._item_count;

var itemCount = count[| index];

// We run out of items
if itemCount <= 1 {
    ds_list_delete(items, index);
    ds_list_delete(count, index);
} else {
    count[| index] --;
}

return result;

#define inventory_get_size_ext
/// inventory_get_size_ext(inventory);
// Returns how many unique items there are in an inventory

var inv = argument0;

return ds_list_size(inv._item);

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
#define inventory_get_size
/// inventory_get_size();
// Returns how many unique items there are in the inventory system

return inventory_get_size_ext(INVENTORY_SYSTEM);