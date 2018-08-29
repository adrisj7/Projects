#define tile
/** TILE
 *
 *  Mainly for collisions. In the future, maybe for custom tilesets.
 *
 *
 *
 */

#define tile_collision_system_create
/// tile_collision_system_create();

return instance_create(0, 0, objTileCollisionSystem);

#define DEPtileset_load_collision_buffer_from_file
///tileset_load_collision_buffer_from_file(tileset);

var tileset = argument0;

var fname = DEPtileset_get_collision_buffer_name(tileset);

// Make our file if it doesn't exist
if !file_exists(fname) {
    show_message("Uh Oh! The tile collision file for " + background_get_name(tileset) + " does not have an included file. An empty shell has been created in the appdata/<game here> folder, so be sure to copy it over and add it as an included file!");
    var tileWidth = TILE_WIDTH; 
    var tileHeight = TILE_HEIGHT;
    var makeSurface = surface_create(background_get_width(tileset) / tileWidth, background_get_height(tileset) / tileHeight);
    surface_save(makeSurface, fname);
    surface_free(makeSurface);
}

var colbg = background_add(fname, false, false);

// Make a surface and draw our collision sprite to it
var tempSurface = surface_create(background_get_width(colbg), background_get_height(colbg));
surface_set_target(tempSurface);
draw_background(colbg,0,0);
surface_reset_target();

// Make our temp buffer holding our collision sprite surface data
var tempBuffer = buffer_create(surface_get_width(tempSurface) * surface_get_height(tempSurface) * 4,buffer_fast, 1);
buffer_get_surface(tempBuffer, tempSurface, 0,0,0);

// Copy over only the important values from the temp buffer into our collisionBuffer
var collisionBuffer = buffer_create(surface_get_width(tempSurface) * surface_get_height(tempSurface),buffer_fast,1);
for(var i = 0; i < buffer_get_size(collisionBuffer); i++) {
    buffer_write(collisionBuffer, buffer_u8, buffer_read(tempBuffer, buffer_u8));
    buffer_seek(tempBuffer, buffer_seek_relative, 3); // Move 3 at a time, since we're only looking for R (skipping G, B and A.)
}

// Clean up
surface_free(tempSurface);
buffer_delete(tempBuffer);
background_delete(colbg);

return collisionBuffer;

#define DEPtileset_get_collision_buffer_name
/// tileset_get_collision_buffer_name(background, <include predirectory thing>);
// Returns the name of the picture that stores the collision buffer data of a tileset

var bg = argument[0];
var include = true;

if argument_count > 1 {
    include = argument[1];
}

var name = background_get_name(bg);

var modifiedName = name + "_collision_mask.png";

if include {
    modifiedName = working_directory + "tilecollisions/" + modifiedName;
}

return modifiedName;

#define DEPtile_check_collideable
/// tile_check_collideable(tile);
// Is this tile collideable?

var t = argument0;

var tileset = tile_get_background(t);
var xa = floor(tile_get_left(t) / TILE_WIDTH),
    ya = floor(tile_get_top(t)  / TILE_HEIGHT);

var buff = DEPtileset_get_collision_buffer(tileset);

var val = DEPtileset_collision_buffer_get(buff, tileset, xa, ya);

return (val > 127);

#define DEPtileset_get_collision_buffer
///tileset_get_collision_buffer(tileset);
// Gets the collision buffer of a tileset, loading it if necessary.

var tileset = argument0;

var map = TILE_COLLISION_SYSTEM._tileCollisionMap;

if ds_map_exists(map, tileset) {
    return map[? tileset];
} else {
    map[? tileset] = DEPtileset_load_collision_buffer_from_file(tileset);
    return map[? tileset];
}


#define DEPtileset_collision_buffer_get
/// tileset_collision_buffer_get(buffer, background, x, y);
var buff = argument0,
    tset = argument1,
    xa   = argument2,
    ya   = argument3;

var numCols = DEPtileset_get_collision_buffer_width(tset);

return buffer_peek(buff, xa + ya*numCols, buffer_u8);

#define DEPtileset_collision_buffer_set
/// tileset_collision_buffer_set(buffer, background, x, y, value);
var buff = argument0,
    tset = argument1,
    xa   = argument2,
    ya   = argument3,
    val  = argument4;

var numCols = DEPtileset_get_collision_buffer_width(tset);

buffer_seek(buff, buffer_seek_start, xa + ya*numCols);
buffer_write(buff, buffer_u8, val);

#define DEPtileset_get_collision_buffer_width
/// tileset_get_collision_buffer_width(background);

var bg = argument0;

return background_get_width(bg) / TILE_WIDTH;

#define DEPtileset_get_collision_buffer_height
/// tileset_get_collision_buffer_height(background);

var bg = argument0;

return background_get_height(bg) / TILE_HEIGHT;

#define DEPtileset_get_at
/// tileset_get_at(tileX, tileY, result ds List);
// Stores a DS list of all tiles at tileX, tileY in "result"

var tx         = argument0,
    ty         = argument1,
    resultList = argument2;

ds_list_clear(resultList);

var checkX = floor(tx)*TILE_WIDTH,
    checkY = floor(ty)*TILE_HEIGHT;

var depthList = TILE_COLLISION_SYSTEM._tileDepths;
for(var i = 0; i < ds_list_size(depthList); i++) {
    var dep = depthList[| i];
    var tid = tile_layer_find(dep, checkX, checkY);
    if tid != -1 {
        ds_list_add(resultList, tid);
    }
}

return resultList;

#define tileset_meeting
/// tileset_meeting(x, y, < collisionListInfo >);

var dx =   argument[0] - x,
    dy =   argument[1] - y;
var data = -1;
if argument_count > 2
    data = argument[2];

var bbl = bbox_left + dx,
    bbr = bbox_right + dx,
    bbt = bbox_top + dy,
    bbb = bbox_bottom + dy;

// Convert "adjusted" bounding box coordinates to tile coordinates
bbl = floor(bbl / tileset_get_collision_width());
bbr = floor(bbr / tileset_get_collision_width());
bbt = floor(bbt / tileset_get_collision_height());
bbb = floor(bbb / tileset_get_collision_height());

// Go through collision box checking for tile collisions
for(var xa = bbl; xa <= bbr; xa++) {
    for(var ya = bbt; ya <= bbb; ya++) {
        if tileset_get_collision(xa, ya) {
            var w = tileset_get_collision_width(),
                h = tileset_get_collision_height();
            if data != -1 {
                data[| 0] = xa * w;
                data[| 1] = ya * h;
                data[| 2] = w; // TODO: Isn't this optional?
                data[| 3] = h;
            }
            return true;
        }
    }
}

return false;

#define tileset_get_collision_width
/// tileset_get_collision_width();

return objTileCollisionSystem._collisionTileWidth;

#define tileset_get_collision_height
/// tileset_get_collision_height();

return objTileCollisionSystem._collisionTileHeight;
#define tileset_set_collision
/// tileset_set_collision(tile_x, tile_y, collision?)
var tx = argument0,
    ty = argument1,
    c  = argument2;

var grid = objTileCollisionSystem._grid;

grid[# tx, ty] = c;

#define tileset_get_collision
/// tileset_get_collision(tile_x, tile_y)
var tx = argument0,
    ty = argument1;

var grid = objTileCollisionSystem._grid;

return grid[# tx, ty];