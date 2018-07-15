#define tileset_create
///tileset_create(sprite, collision_map, tileWidth, tileHeight);

var spr = argument0,
    col = argument1,
    tw  = argument2,
    th  = argument3;

var tileset = instance_create(0,0,objTileset);
tileset.sprite_index = spr;
tileset._tileWidth = tw;
tileset._tileHeight = th;

// Make a surface and draw our collision sprite to it
var tempSurface = surface_create(sprite_get_width(col), sprite_get_height(col));
surface_set_target(tempSurface);
draw_sprite(col,0,0,0);
surface_reset_target();

// Make our temp buffer holding our collision sprite surface data
var tempBuffer = buffer_create(sprite_get_width(col) * sprite_get_height(col) * 4,buffer_fast, 1);
buffer_get_surface(tempBuffer, tempSurface, 0,0,0);

// Copy over only the important values from the temp buffer into our collisionBuffer
tileset._collisionBuffer = buffer_create(sprite_get_width(col) * sprite_get_height(col),buffer_fast,1);
for(var i = 0; i < buffer_get_size(tileset._collisionBuffer); i++) {
    buffer_write(tileset._collisionBuffer, buffer_u8, buffer_read(tempBuffer, buffer_u8));
    buffer_seek(tempBuffer, buffer_seek_relative, 3);
}

// Clean up
surface_free(tempSurface);
buffer_delete(tempBuffer);

return tileset;

#define tileset_draw_tile
///tileset_draw_tile(tileset, tileIndex, x, y);

var tileset   = argument0,
    tileIndex = argument1,
    xa        = argument2,
    ya        = argument3;

// tileIndex tells us what tile to grab from the spritesheet.
// Now we must decipher it:

// TODO: Should these be stored? ehh
var tilesheetTileWidth = sprite_get_width(tileset.sprite_index) / tileset._tileWidth;

// Tile coordinates of the sprite within the tileset
var sheetTileX = tileIndex % tilesheetTileWidth;
var sheetTileY = floor(tileIndex / tilesheetTileWidth);

// Pixel coordinates of the sprite within the tileset
var sheetX = sheetTileX * tileset._tileWidth;
var sheetY = sheetTileY * tileset._tileHeight;


draw_sprite_part(tileset.sprite_index, 0,
        sheetX,                sheetY, 
        tileset._tileWidth,    tileset._tileHeight, 
        xa, ya
        );

#define tileset_get_tile_width
///tileset_get_tile_width(tileset);
var tileset = argument0;

return tileset._tileWidth;

#define tileset_get_tile_height
///tileset_get_tile_height(tileset);
var tileset = argument0;

return tileset._tileHeight;

#define tileset_tile_has_collision
///tileset_tile_has_collision(tileset, tileIndex);
// Is the tile at "tileIndex" flagged for collisions?

var tileset   = argument0,
    tileIndex = argument1;

var collisionData = buffer_peek(tileset._collisionBuffer, tileIndex, buffer_u8);

return (collisionData == 255);

