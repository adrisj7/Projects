#define tilemap_create
///tilemap_create(width, height, tileset)

var width =      argument0,
    height =     argument1,
    tileset =    argument2;

var ida = instance_create(0,0,objTilemap);

ida._tiles = ds_grid_create(width, height);
//ida._tileWidth = tileWidth;
//ida._tileHeight = tileHeight;
ida._tileset = tileset;
ida._tileSurface = surface_create(width*tileset_get_tile_width(tileset), height*tileset_get_tile_height(tileset));

// Fill the grid empty
for(var yy = 0; yy < height; yy++) {
    for(var xx = 0; xx < width; xx++) {
        tilemap_set_tile(ida, xx, yy, -1);
    }
}

return ida;

#define tilemap_set_tile
///tilemap_set_tile(tilemap, x, y, tileIndex);

var tilemap =   argument0,
    xa =        argument1,
    ya =        argument2,
    tileIndex = argument3;

ds_grid_set(tilemap._tiles, xa, ya, tileIndex);

#define tilemap_get_tile
///tilemap_get_tile(tilemap, x, y);

var tilemap = argument0,
    xa =      argument1,
    ya =      argument2;

if (xa < 0 || ya < 0 || xa >= tilemap_get_width(tilemap) || ya >= tilemap_get_height(tilemap)) {
    return -1;
}
return ds_grid_get(tilemap._tiles, xa, ya);

#define tilemap_set_position
///tilemap_set_position(tilemap, x, y);

var tilemap = argument0,
    xa =      argument1,
    ya =      argument2;

tilemap.x = xa;
tilemap.y = ya;

#define tilemap_get_width
///tilemap_get_width(tilemap);

var tilemap = argument0;

return ds_grid_width(tilemap._tiles);

#define tilemap_get_height
///tilemap_get_height(tilemap);

var tilemap = argument0;

return ds_grid_height(tilemap._tiles);

#define tilemap_destroy
///tilemap_destroy(tilemap);

var tilemap = argument0;

with (tilemap) {
    instance_destroy();
}

#define tilemap_tile_place
/// @  DEPRECATED  @

print("[tilemap_tile_place] This function is deprecated. Avoid using it.");

///tilemap_tile_place(tilemap, x, y, listInfo);
/* 
 * To be used by objects trying to collide with tiles.
 * Tells whether you're colliding with a tile or not, and the index of said tile.
 *
 * -1 means no tile was found.
 */

var tilemap =  argument0,
    xa =       argument1,
    ya =       argument2,
    listInfo = argument3; // Pass a list to store tile position info

var dx = xa - x;
var dy = ya - y;

var left =   dx + bbox_left   - tilemap.x;
var right =  dx + bbox_right  - tilemap.x;
var top =    dy + bbox_top    - tilemap.y;
var bottom = dy + bbox_bottom - tilemap.y;

var tileWidth = tilemap_get_tile_width(tilemap);
var tileHeight = tilemap_get_tile_height(tilemap);

var tile_left = floor(left / tileWidth);
var tile_right = floor(right / tileWidth);
var tile_top = floor(top / tileHeight);
var tile_bottom = floor(bottom / tileHeight);

for(var ty = tile_top; ty <= tile_bottom; ty++) {
    for(var tx = tile_left; tx <= tile_right; tx++) {
        var tile = tilemap_get_tile(tilemap, tx, ty);
        if (tile != -1 && tileset_tile_has_collision(tilemap._tileset, tile)) {
            // Pixel position of tile, relative to universe
            var tile_real_pos_x = tx * tileWidth + tilemap.x;
            var tile_real_pos_y = ty * tileHeight + tilemap.y;

            ds_list_add(listInfo, tile_real_pos_x, tile_real_pos_y);

            return tile;
        }
    }
}

return -1;

#define tilemap_get_position_x
///tilemap_get_position_x(tilemap);
var tilemap = argument0;

return tilemap.x;

#define tilemap_get_position_y
///tilemap_get_position_y(tilemap);
var tilemap = argument0;

return tilemap.y;

#define tilemap_get_tile_width
///tilemap_get_tile_width(tilemap)
var tilemap = argument0;

return tileset_get_tile_width(tilemap._tileset);

#define tilemap_get_tile_height
///tilemap_get_tile_height(tilemap)
var tilemap = argument0;

return tileset_get_tile_height(tilemap._tileset);

#define tilemap_redraw_surface
///tilemap_redraw_surface(tilemap);
// Redraws our surface, filling it with all the tiles

var tilemap = argument0;

surface_set_target(tilemap._tileSurface);

// Clear this surface
draw_clear_alpha(c_black,0);

//draw_set_alpha_test(true);
//draw_set_alpha_test_ref_value(0);


// Go through the entire grid, find the index at each grid and draw by that index
for(var yy = 0; yy < ds_grid_height(tilemap._tiles); yy++) {
    for(var xx = 0; xx < ds_grid_width(tilemap._tiles); xx++) {

        // Don't draw if we're outside the view
        /*
        var realX = xx*TILE_WIDTH + tilemap_get_position_x(tilemap);
        var realY = yy*TILE_HEIGHT + tilemap_get_position_y(tilemap);

        if (realX < view_xview - 1.5*TILE_WIDTH 
         || realX > view_xview + view_wview + 1.5*TILE_WIDTH 
         || realY < view_yview - 1.5*TILE_HEIGHT
         || realY > view_yview + view_hview + 1.5*TILE_HEIGHT) {
            continue;
         }
         */

        var tileIndex = ds_grid_get(tilemap._tiles, xx, yy);

        // If the tile is empty, clear this rectangle
        if (tileIndex == -1) {
            continue;
        }

        // Draw our tile relative to this tilemap
        tileset_draw_tile(tilemap._tileset, tileIndex, xx*tilemap_get_tile_width(tilemap), yy*tilemap_get_tile_height(tilemap));
    }
}

//draw_set_alpha_test(false);

surface_reset_target();
