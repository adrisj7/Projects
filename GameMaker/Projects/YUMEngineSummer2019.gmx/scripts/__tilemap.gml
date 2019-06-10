#define __tilemap
/// Tile map!
    // All scripts related to tile maps

    // Tilemap struct
    enum Tilemap {
        map,
        twidth,
        theight,
        sizeof
    }


#define __get_tilemap_grid
/// __get_tilemap_grid(tilemap)
    var tilemap/*:Tilemap*/ = argument0;

    return tilemap[Tilemap.map];


#define __get_tilemap_collision
/// __get_tilemap_collision()
    var g = __get_gamestate();
    return g._tilemap_collision;


#define __get_tilemap_event
/// __get_tilemap_event()
    var g = __get_gamestate();
    return g._tilemap_event;


#define __get_tilemap_character
/// __get_tilemap_character()
    var g = __get_gamestate();
    return g._tilemap_character;


#define __refill_collision_tilemap
/// __refill_collision_tilemap()
    // Refills the collision tilemap with all collideable objects and tiles
    // in the room

    var c = __get_tilemap_collision();

    // Step 1: Clear all collisions
    tilemap_clear(c, false);

    // Collider objects must inherit objYUMEngineCollider
    with (objYUMEngineCollider) {
        tilemap_collision_set_with_object(id, true);
    }

    // TODO: Tileset collideables


#define __refill_event_tilemap
/// __refill_event_tilemap()

    var tmap = __get_tilemap_event();

    // Clear. Events are filled in by other objects in the room
    tilemap_clear(tmap, noone);


#define __refill_character_tilemap
/// __refill_character_tilemap()

    // TODO: Characters


////////////////////////////////////////////////////////////////////////////////


#define tilemap_create
/// tilemap_create(width_in_tiles, height_in_tiles, twidth, theight)
    var width_in_tiles = argument0, height_in_tiles = argument1, twidth = argument2, theight = argument3;

    // Our new tilemap
    var result/*:Tilemap*/ = array_create(Tilemap.sizeof);
    result[@Tilemap.map]     = ds_grid_create(width_in_tiles, height_in_tiles);
    result[@Tilemap.twidth]  = twidth;
    result[@Tilemap.theight] = theight;

    return result;


#define tilemap_create_and_fill_room
/// tilemap_create_and_fill_room(twidth, theight)
    // creates a new tilemap that sets its width and height (in tiles) to fill the 
    // current room
    var twidth = argument0, theight = argument1;

    var tilemap_width  = ceil(room_width  / twidth),
        tilemap_height = ceil(room_height / theight);

    return tilemap_create(tilemap_width, tilemap_height, twidth, theight);


#define tilemap_clear
/// tilemap_clear(tilemap, val)
    // clears a tilemap to a specific value. Akin to ds_grid_clear
    var tilemap = argument0, val = argument1;

    var grid = __get_tilemap_grid(tilemap);
    ds_grid_clear(grid, val);


#define tilemap_get_tile
/// tilemap_get_tile(tilemap, tile_xpos, tile_ypos)
    // returns the value of a tile stored in this tilemap. If it's an invalid tile 
    // it returns -1
    var tilemap = argument0, tile_xpos = argument1, tile_ypos = argument2;

    var grid = __get_tilemap_grid(tilemap);

    return grid[# tile_xpos, tile_ypos];


#define tilemap_set_tile
/// tilemap_set_tile(tilemap, tile_xpos, tile_ypos, value)
    var tilemap = argument0, tile_xpos = argument1, tile_ypos = argument2, value = argument3;

    var grid = __get_tilemap_grid(tilemap);

    grid[# tile_xpos, tile_ypos] = value;


#define tilemap_resize
/// tilemap_resize(tilemap, width_in_tiles, height_in_tiles)
    var tilemap = argument0, width_in_tiles = argument1, height_in_tiles = argument2;

    var grid = __get_tilemap_grid(tilemap);

    ds_grid_resize(grid, width_in_tiles, height_in_tiles);


#define tilemap_resize_to_fill_room
/// tilemap_resize_to_fill_room(tilemap)
    // resizes a tilemap's width and height (in tiles) to fill the current room
    var tilemap = argument0;

    var twidth =  tilemap_get_tile_width(tilemap),
        theight = tilemap_get_tile_height(tilemap);

    var tilemap_width  = ceil(room_width  / twidth),
        tilemap_height = ceil(room_height / theight);

    tilemap_resize(tilemap, tilemap_width, tilemap_height);


#define tilemap_meeting
/// tilemap_meeting(xa, ya, tilemap)
    // effectively place_meeting but for tilemaps. (in theory, both can be used 
    // interchangeably for collisions)
    var dx      = argument0 - x,
        dy      = argument1 - y,
        tmap_id = argument2;

    var tleft   = tilemap_get_tile_x(tmap_id, bbox_left + dx),
        tright  = tilemap_get_tile_x(tmap_id, bbox_right + dx),
        ttop    = tilemap_get_tile_y(tmap_id, bbox_top + dy),
        tbot    = tilemap_get_tile_y(tmap_id, bbox_bottom + dy);

    // Go through all tiles within the bounding box
    for (var tx = tleft; tx <= tright; ++tx) {
        for (var ty = ttop; ty <= tbot; ++ty) {
            if tilemap_get_tile(tmap_id, tx, ty)
                return true;
        }
    }

    return false;


#define tilemap_get_pos_x
/// tilemap_get_pos_x(tilemap, tile_xpos)
    // gets the in-game x pixel coordinate of a tile
    var tilemap = argument0, tile_xpos = argument1;

    // Simply scale by the tile width
    var twidth = tilemap_get_tile_width(tilemap);
    return tile_xpos * twidth;


#define tilemap_get_pos_y
/// tilemap_get_pos_y(tilemap, tile_ypos)
    // gets the in-game y pixel coordinate of a tile
    var tilemap = argument0, tile_ypos = argument1;

    // Simply scale by the tile height
    var theight = tilemap_get_tile_height(tilemap);
    return tile_ypos * theight;


#define tilemap_get_tile_x
/// tilemap_get_tile_x(tilemap, pos_x)
    // gets the tile coordinate of an in-game x pixel coordinate
    var tilemap = argument0, pos_x = argument1;

    // Simply scale down by the tile width
    var twidth = tilemap_get_tile_width(tilemap);
    return floor(pos_x / twidth);


#define tilemap_get_tile_y
/// tilemap_get_tile_y(tilemap, pos_y)
    // gets the tile coordinate of an in-game y pixel coordinate
    var tilemap = argument0, pos_y = argument1;

    // Simply scale down by the tile height
    var theight = tilemap_get_tile_height(tilemap);
    return floor(pos_y / theight);


#define tilemap_get_width_in_tiles
/// tilemap_get_width_in_tiles(tilemap);
    // returns the tilemap's width (in tiles)
    var tilemap = argument0;

    var grid = __get_tilemap_grid(tilemap);

    return ds_grid_width(grid);


#define tilemap_get_height_in_tiles
/// tilemap_get_height_in_tiles(tilemap);
    // returns the tilemap's height (in tiles)
    var tilemap = argument0;

    var grid = __get_tilemap_grid(tilemap);

    return ds_grid_height(grid);


#define tilemap_get_tile_width
/// tilemap_get_tile_width(tilemap)
    // returns the width of a tilemap's tiles (in pixels)
    var tilemap/*:Tilemap*/ = argument0;

    return tilemap[Tilemap.twidth];


#define tilemap_get_tile_height
/// tilemap_get_tile_height(tilemap)
    // returns the width of a tilemap's tiles (in pixels)
    var tilemap/*:Tilemap*/ = argument0;

    return tilemap[Tilemap.theight];


#define tilemap_global_set_tile_size
/// tilemap_global_set_tile_size(twidth, theight)
    // sets the tile size of all background tilemaps. 
    // This will cause all tilemaps to reload and re-fill themselves, so this 
    // should in theory only be done once at the beginning of the game
    var twidth = argument0, theight = argument1;

    // Set gamestate properties
    var g = __get_gamestate();
    g._tile_width = twidth;
    g._tile_height = theight;

    var col/*:Tilemap*/ = __get_tilemap_collision(),
        evt/*:Tilemap*/ = __get_tilemap_event(),
        cha/*:Tilemap*/ = __get_tilemap_character();


    // Manually change the tilemap tile size
    col[@Tilemap.twidth] = twidth;
    col[@Tilemap.theight] = theight;
    evt[@Tilemap.twidth] = twidth;
    evt[@Tilemap.theight] = theight;
    cha[@Tilemap.twidth] = twidth;
    cha[@Tilemap.theight] = theight;

    // Resize the tilemaps
    tilemap_resize_to_fill_room(col);
    tilemap_resize_to_fill_room(evt);
    tilemap_resize_to_fill_room(cha);


    // Refill them
    __refill_collision_tilemap();
    __refill_event_tilemap();
    __refill_character_tilemap();


#define tilemap_global_get_tile_x
/// tilemap_global_get_tile_x(pos_x)
    // gets the tile coordinate of an in-game x pixel coordinate
    var pos_x = argument0;
    return tilemap_get_tile_x(__get_tilemap_collision(), pos_x);


#define tilemap_global_get_tile_y
/// tilemap_global_get_tile_y(pos_y)
    // gets the tile coordinate of an in-game y pixel coordinate
    var pos_y = argument0;
    return tilemap_get_tile_y(__get_tilemap_collision(), pos_y);


#define tilemap_global_get_pos_x
/// tilemap_global_get_pos_x(tile_xpos)
    // gets the in-game x pixel coordinate of a tile
    var tile_xpos = argument0;
    return tilemap_get_pos_x(__get_tilemap_collision(), tile_xpos);


#define tilemap_global_get_pos_y
/// tilemap_global_get_pos_y(tile_ypos)
    // gets the in-game y pixel coordinate of a tile
    var tile_ypos = argument0;
    return tilemap_get_pos_y(__get_tilemap_collision(), tile_ypos);


#define tilemap_global_get_tile_width
/// tilemap_global_get_tile_width()
    var g = __get_gamestate();
    return g._tile_width;


#define tilemap_global_get_tile_height
/// tilemap_global_get_tile_height()
    var g = __get_gamestate();
    return g._tile_height;


#define tilemap_collision_set
/// tilemap_collision_set(tile_xpos, tile_ypos, collide)
    var tile_xpos = argument0, tile_ypos = argument1, collide = argument2;

    var c = __get_tilemap_collision();
    tilemap_set_tile(c, tile_xpos, tile_ypos, collide);


#define tilemap_collision_get
/// tilemap_collision_get(tile_xpos, tile_ypos)
    var tile_xpos = argument0, tile_ypos = argument1;

    var c = __get_tilemap_collision();
    return tilemap_get_tile(c, tile_xpos, tile_ypos);


#define tilemap_collision_set_with_object
/// tilemap_collision_set_with_object(ind, value)
    // sets the collision value of all tiles that overlap an object's mask
    var ind = argument0, value = argument1;

    var tleft   = tilemap_global_get_tile_x(ind.bbox_left),
        tright  = tilemap_global_get_tile_x(ind.bbox_right),
        ttop    = tilemap_global_get_tile_y(ind.bbox_top),
        tbot    = tilemap_global_get_tile_y(ind.bbox_bottom);
    for(var xx = tleft; xx <= tright; ++xx) {
        for(var yy = ttop; yy <= tbot; ++yy) {
            tilemap_collision_set(xx, yy, true);
            //show_debug_message("[DEBUG] : Set " + string(xx) + "\t,\t " + string(yy));
        }
    }


#define tilemap_collision_meeting
/// tilemap_collision_meeting(dx, dy)
    var dx = argument0, dy = argument1;


#define object_get_tile_x
/// object_get_tile_x(ind)
    var ind = argument0;

    var pos_x = ind.x;
    return tilemap_global_get_tile_x(pos_x);


#define object_get_tile_y
/// object_get_tile_y(ind)
    var ind = argument0;

    var pos_y = ind.y;
    return tilemap_global_get_tile_y(pos_y);