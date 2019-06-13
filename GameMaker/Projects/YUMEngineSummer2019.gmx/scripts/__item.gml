#define __item
/// __item
// Item system!

    // Item Type Struct
    enum ItemType {
        name,
        description,
        useable,     // Can we use this item at all?
        consumable,  // When it's used, does it perish?
        script,
        sizeof
    }

    // Item Slot Struct (each slot in the inventory screen)
    enum ItemSlot {
        item_index,
        count,
        sizeof
    }


#define __get_item_system
/// __get_item_system()
    return singleton_get(__YUMEngineItemSystem);


#define __item_get_list
/// __item_get_list()
    var sys = __get_item_system();
    return sys._item_list;


#define __item_get_inventory
/// __item_get_inventory()
    var sys = __get_item_system();
    return sys._inventory;


#define __item_type_add
/// __item_type_add(type)
    var type = argument0;
    // Makes a new item, adding it to the item type list and returning its
    // index in the list
    var list = __item_get_list();
    var len  = array_length_1d(list);
    list[@ len] = type;
    return len;


#define __item_type_get
/// __item_type_get(index)
    var index = argument0;
    var list = __item_get_list();
    return list[@ index];


#define __inventory_get_slot
/// __inventory_get_slot(index)
    var index = argument0;
    var inv = __item_get_inventory();
    var size = inventory_get_count();
    var slot/*:ItemSlot*/ = ds_list_find_value(inv, index);
    return slot;


#define item_type_create
/// item_type_create(_name, _description)
    var _name = argument0, _description = argument1;
    // Create an item type. Returns the item id that can be used to reference it
    var item/*:ItemType*/ = array_create(ItemType.sizeof);
    item[@ItemType.name] = _name;
    item[@ItemType.description] = _description
    item[@ItemType.useable] = true;
    item[@ItemType.consumable] = false;
    item[@ItemType.script] = noone;

    return __item_type_add(item);


#define item_type_execute_script
/// item_type_execute_script(item_index)
    var item_index = argument0;
    // Runs the script that an item has
    var item/*:ItemType*/ = __item_type_get(item_index);
    if script_exists(item[ItemType.script]) {
        script_execute(item[ItemType.script]);
    } else {
        show_error('Item "' + item[ItemType.name] + '" does not have a script, but you '
                 + "tried to call it anyway.", false);
    }


#define item_type_set_useable
/// item_type_set_useable(item_index, useable)
    var item_index = argument0, useable = argument1;
    var item/*:ItemType*/ = __item_type_get(item_index);
    item[@ItemType.useable] = useable;
#define item_type_set_consumable
/// item_type_set_consumable(item_index, consumable)
    var item_index = argument0, consumable = argument1;
    var item/*:ItemType*/ = __item_type_get(item_index);
    item[@ItemType.consumable] = consumable;
#define item_type_set_script
/// item_type_set_script(item_index, script)
    var item_index = argument0, script = argument1;
    var item/*:ItemType*/ = __item_type_get(item_index);
    item[@ItemType.script] = script;
#define item_type_get_name
/// item_type_get_name(item_index)
    var item_index = argument0;
    var item/*:ItemType*/ = __item_type_get(item_index);
    return item[ItemType.name];
#define item_type_get_description
/// item_type_get_description(item_index)
    var item_index = argument0;
    var item/*:ItemType*/ = __item_type_get(item_index);
    return item[ItemType.description];
#define item_type_get_useable
/// item_type_get_useable(item_index)
    var item_index = argument0;
    var item/*:ItemType*/ = __item_type_get(item_index);
    return item[ItemType.useable];
#define item_type_get_consumable
/// item_type_get_consumable(item_index)
    var item_index = argument0;
    var item/*:ItemType*/ = __item_type_get(item_index);
    return item[ItemType.consumable];
#define item_type_get_script
/// item_type_get_script(item_index)
    var item_index = argument0;
    var item/*:ItemType*/ = __item_type_get(item_index);
    return item[ItemType.script];


#define inventory_add
/// inventory_add(item_index)
    var item_index = argument0;
    // Adds an item to our inventory system. By default this is added
    // to the end of the inventory, but if the item already exists it's stacked
    var len = inventory_get_count();
    // By inserting at the beginning, we're not using arraylists effectively.
    // Is that inefficient and O(n) each time?
    // Yes.
    // Does it really matter, since our inventory will never really get that big
    // and this is only called like a few times?
    // No.
    inventory_add_ext(item_index, 0);//len);


#define inventory_add_ext
/// inventory_add_ext(item_index, ifnew_index)
    var item_index = argument0, ifnew_index = argument1;
    // Adds an item at a certain index.
    // If it already exists, ignore that index and add it where it should be

    var inv = __item_get_inventory();

    var find_index = inventory_find_item(item_index);
    if find_index == noone {
        // New item, add it to the list
        var slot/*:ItemSlot*/ = array_create(ItemSlot.sizeof);
        slot[@ItemSlot.item_index] = item_index
        slot[@ItemSlot.count] = 1;
        // Insert it where we told it to be if it's a new item
        ds_list_insert(inv, ifnew_index, slot);
    } else {
        // We already have an item, add its count
        var slot/*:ItemSlot*/ = __inventory_get_slot(find_index);
        ++slot[@ItemSlot.count];
    }


#define inventory_remove
/// inventory_remove(item_index)
    var item_index = argument0;
    // Removes an item of a specific type. Only removes one item at a time
    var index = inventory_find_item(item_index);
    if index == noone {
        show_error("Did not find item of index " + string(item_index) + ".", false);
    } else {
        inventory_remove_at(index);
    }


#define inventory_remove_at
/// inventory_remove_at(index)
    var index = argument0;
    // Removes an item at a particular index of the inventory.
    // Only removes one item at a time
    var inv = __item_get_inventory();
    var slot/*:ItemSlot*/ = ds_list_find_value(inv, index);
    --slot[@ItemSlot.count];
    if slot[ItemSlot.count] <= 0 {
        // We ran out of items, delete the entry
        ds_list_delete(inv, index);
    }


#define inventory_clear
/// inventory_clear()
    // Removes all items from the inventory
    var list = __item_get_inventory();
    ds_list_clear(list);


#define inventory_get_count
/// inventory_get_count()
    var inv = __item_get_inventory();
    return ds_list_size(inv);


#define inventory_empty
/// inventory_empty()
    // Is our inventory empty?
    return inventory_get_count() == 0;


#define inventory_find_item
/// inventory_find_item(item_index)
    var item_index = argument0;
    // Searches for an item of its id and returns its index if found.
    // If not found, returns noone.
    for (var i = 0; i < inventory_get_count(); ++i) {
        var check_id = inventory_get(i);
        if check_id == item_index {
            return i;
        }
    }
    // Nothing found
    return noone;


#define inventory_has_item
/// inventory_has_item(item_index)
    var item_index = argument0;

    var found = inventory_find_item(item_index);
    return (found != noone);


#define inventory_get
/// inventory_get(index)
    var index = argument0;
    // Gets the item id in a certain slot
    var slot/*:ItemSlot*/ = __inventory_get_slot(index);
    if is_undefined(slot) {
        return noone;
    }
    return slot[ItemSlot.item_index];


#define inventory_get_quantity
/// inventory_get_quantity(index)
    var index = argument0;
    // Gets how many of an item is in a certain slot
    // If no items exist at that index, return 0.
    var slot/*:ItemSlot*/ = __inventory_get_slot(index);
    if is_undefined(slot) {
        return 0;
    }
    return slot[ItemSlot.count];


#define item_type_exists
/// item_type_exists(item_index)
    var item_index = argument0;
    var list = __item_get_list();
    return !is_undefined(item_index)
        && item_index >= 0
        && item_index < array_length_1d(list);
#define item_type_get_by_name
/// item_type_get_by_name(item_name)
    var item_name = argument0;
    // Returns the item type that has that name

    var list = __item_get_list();
    for (var i = 0; i < ds_list_size(list); ++i) {
        var item = list[| i];
        if item_type_get_name(item) == item_name {
            return item;
        }
    }
    // none found
    return noone;