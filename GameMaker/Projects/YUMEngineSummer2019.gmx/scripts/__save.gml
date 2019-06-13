#define __save
/// Save system!

#define save_game
/// save_game(fname)
    var fname = argument0;
    // Saves the current game to a file

    var map = ds_map_create();

    /// General stuff
    map[? "room"] = room;

    /// TODO: Stats

    /// Characters
    var char_list = ds_list_create();
    var player_pos = noone;
    var counter = 0;
    with (__YUMEngineCharacter) {

        if character_get_player() == id {
            player_pos = counter;
        }

        var char_map = ds_map_create();
        char_map[? "tile_x"] = character_get_tile_x(id);
        char_map[? "tile_y"] = character_get_tile_y(id);
        char_map[? "orientation"] = character_get_orientation(id);
        char_map[? "sprite"] = _movement_sprite;
        char_map[? "subimg"] = _movement_sprite_subimg;
        char_map[? "sprite_left"] = _movement_sprite_left;
        char_map[? "sprite_top"] = _movement_sprite_left;
        char_map[? "sprite_width"] = _movement_sprite_width;
        char_map[? "sprite_height"] = _movement_sprite_height;
        char_map[? "frames_per_tile"] = _movement_frames_per_tile;
        char_map[? "origin_x"] = _movement_origin_x;
        char_map[? "origin_y"] = _movement_origin_y;
        char_map[? "move_speed"] = _move_speed;
        char_map[? "auto_orient"] = _auto_orient;
        char_map[? "ignore_dialogue"] = _ignore_dialogue;
        char_map[? "ignore_collisions"] = _ignore_collisions;
        char_map[? "diagonal_movement"] = _diagonal_movement;

        // Add our char map to the list
        ds_list_add_map(char_list, char_map);
        ++counter;
    }
    ds_map_add_list(map, "characters", char_list);

    // Save where our player is in the character list
    map[? "characters_player_pos"] = player_pos;

    /// Inventory
    var inventory_list = ds_list_create();
    for (var i = 0; i < inventory_get_count(); ++i) {
        var item_map = ds_map_create();

        var item = inventory_get(i);
        item_map[? "name"] = item_type_get_name(item);
        item_map[? "quantity"] = inventory_get_quantity(i);

        // Add our item map to the list
        ds_list_add_map(inventory_list, item_map);
    }
    ds_map_add_list(map, "inventory", inventory_list);

    /// TODO: Variable system

    // Save the darn thing
    var result_string = json_encode(map);
    var file = file_text_open_write(fname);
    file_text_write_string(file, result_string);
    file_text_close(file);


#define load_game
/// load_game(fname)
    var fname = argument0;
    // Force loads a game

    var file = file_text_open_read(fname);
    var result_string = file_text_read_string(file);
    file_text_close(file);

    var map = json_decode(result_string);

    /// General stuff
    room_goto(map[? "room"]);

    // TODO: Stats

    /// Characters

    // Start by deleting all of them
    with __YUMEngineCharacter {
        instance_destroy();
    }

    var char_list = map[? "characters"];
    // Re-add all of the characters that were around at the time of the save
    for(var i = 0; i < ds_list_size(char_list); ++i) {
        var char_map = char_list[| i];

        var char = character_create(char_map[? "tile_x"], char_map[? "tile_y"]);

        // Player
        if i == map[? "characters_player_pos"] {
            character_set_player(char);
        }

        char._orientation = char_map[? "orientation"];
        character_set_move_spritesheet_part(
                char,
                char_map[? "sprite"],
                char_map[? "subimg"],
                char_map[? "sprite_left"],
                char_map[? "sprite_top"],
                char_map[? "sprite_width"],
                char_map[? "sprite_height"]
        );
        char._movement_frames_per_tile = char_map[? "frames_per_tile"];
        char._movement_origin_x = char_map[? "origin_x"];
        char._movement_origin_y = char_map[? "origin_y"];
        char._move_speed = char_map[? "move_speed"];
        char._auto_orient = char_map[? "auto_orient"];
        char._ignore_dialogue = char_map[? "ignore_dialogue"];
        char._ignore_collisions = char_map[? "ignore_collisions"];
        char._diagonal_movement = char_map[? "diagonal_movement"];

    }

    /// Inventory

    // Start by emptying everything
    inventory_clear();

    var inventory_list = map[? "inventory"];
    for (var i = 0; i < ds_list_size(inventory_list); ++i) {
        var item_map = inventory_list[| i];
        
        var item = item_type_get_by_name(item_map[? "name"]);
        var quantity = item_map[? "quantity"];
        repeat(quantity) {
            inventory_add(item);
        }
    }
    ds_map_add_list(map, "inventory", inventory_list);

    /// TODO: Variable system
