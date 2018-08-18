#define tile
/** TILE
 *
 *  Mainly for collisions. In the future, maybe for custom tilesets.
 *
 *
 *
 */

#define tileset_load_collision_buffer
///tileset_load_collision_buffer(tileset);

var tileset = argument0;

var fname = tileset_get_collision_buffer_name(tileset);

// Make our file if it doesn't exist
if !file_exists(fname) {
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

#define tileset_get_collision_buffer_name
/// tileset_get_collision_buffer_name(background);
// Returns the name of the picture that stores the collision buffer data of a tileset

var bg = argument0;

var name = background_get_name(bg);

var modifiedName = "background\collision\" + name + "_collision_mask.png";

return modifiedName;

#define tileset_save_collision_buffer
/// tileset_save_collision_buffer(tileset, collision_buffer);

var tileset   = argument0,
    colbuffer = argument1;

var fname = tileset_get_collision_buffer_name(tileset);

var colWidth = background_get_width(tileset) / TILE_WIDTH;
var colHeight = background_get_height(tileset) / TILE_HEIGHT;

var saveSurface = surface_create(colWidth, colHeight);

// Draw to the surface
surface_set_target(saveSurface);
for(var i = 0; i < colWidth*colHeight; i++) {
    var value = buffer_read(colbuffer, buffer_u8);
    var xa = value % colWidth;
    var ya = floor(value / colWidth);

    draw_point_colour(xa, ya, make_color_rgb(value, value, value));
}
surface_reset_target();

surface_save(saveSurface, fname);

// Cleanup
surface_free(saveSurface);

#define tile_check_collideable
/// tile_check_collideable(tile);


