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

#define inventory_get_item_ext
/// inventory_get_item_ext(inventory, index);
// Gets the item at a certain index

#define inventory_get_item_count_ext
/// inventory_get_item_count_ext(inventory, index);
// Gets the number of items (of the same type) at that index.

#define inventory_pop_item_ext
/// inventory_pop_item_ext(inventory, index);
// Removes an item from the inventory and returns its index.
// Useful when you want to "use" or "drop" an item.

#define inventory_add_item
/// inventory_add_item(item);
// Adds an item to our inventory
inventory_add_item_ext(INVENTORY_SYSTEM, item);

#define inventory_get_item_ext
/// inventory_get_item_ext(inventory, index);
// Gets the item at a certain index

#define inventory_get_item_count_ext
/// inventory_get_item_count_ext(inventory, index);
// Gets the number of items (of the same type) at that index.

#define inventory_pop_item_ext
/// inventory_pop_item_ext(inventory, index);
// Removes an item from the inventory and returns its index.
// Useful when you want to "use" or "drop" an item.
