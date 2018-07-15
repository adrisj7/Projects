#define world_create
///world_create();

// World holds tilesets and world information.
// Very special.

var world = instance_create(0,0,objWorld);

return world;

#define world_get_tilemap
///world_get_tilemap(world, x, y);
// Gets the tileset used at this position
// Returns -1 if no tileset is found

var world = argument0,
    xa =    argument1,
    ya =    argument2;

for(var chunkIndex = 0; chunkIndex < ds_list_size(world._chunks); chunkIndex++) {
    var tilemap = world._chunks[| chunkIndex];
    var tx = tilemap_get_position_x(tilemap);
    var ty = tilemap_get_position_y(tilemap);

    if (xa >= tx && ya >= ty && xa < tx + CHUNK_WIDTH*TILE_WIDTH && ya < ty + CHUNK_HEIGHT*TILE_HEIGHT) {
        return tilemap;
    }
}

return -1;

//return world._tilemap;

#define world_tile_place
///world_tile_place(world, x, y, listInfo);
// Finds the proper tilemap and calls tilemap_tile_place on it

var world =    argument0,
    xa =       argument1,
    ya =       argument2,
    listInfo = argument3;

var dx = xa - x;
var dy = ya - y;

// Absolute tile coordinates of our collision box
var left =   dx + bbox_left;
var right =  dx + bbox_right;
var top =    dy + bbox_top;
var bottom = dy + bbox_bottom;

var tileWidth = TILE_WIDTH;
var tileHeight = TILE_HEIGHT;

var tile_left = floor(left / tileWidth);
var tile_right = floor(right / tileWidth);
var tile_top = floor(top / tileHeight);
var tile_bottom = floor(bottom / tileHeight);

for(var ty = tile_top; ty <= tile_bottom; ty++) {
    for(var tx = tile_left; tx <= tile_right; tx++) {
        var tilemap = world_get_tilemap(world, tx * tileWidth, ty * tileHeight);
        
        // Tile coordinates of collision check relative to tilemap chunk
        var tilemapX = tx - tilemap_get_position_x(tilemap) / tileWidth;
        var tilemapY = ty - tilemap_get_position_y(tilemap) / tileHeight;

        var tile = tilemap_get_tile(tilemap, tilemapX, tilemapY);
        if (tile != -1 && tileset_tile_has_collision(tilemap._tileset, tile)) {
            // Pixel position of tile, relative to universe
            var tile_real_pos_x = tx * tileWidth;
            var tile_real_pos_y = ty * tileHeight;

            ds_list_add(listInfo, tile_real_pos_x, tile_real_pos_y);

            return tile;
        }
    }
}

return -1;

/* Old method relying on tilemap_tile_place
var tilemap = world_get_tilemap(world, xa, ya);

if (tilemap == -1) {
    return -1;
}

return tilemap_tile_place(tilemap, xa, ya, listInfo);

//return -1;

*/

#define world_generate_chunk
///world_generate_chunk(world, chunk);
// Generates a chunk into a given tilemap

var world = argument0,
    chunk = argument1; // A tilemap

var chunkTileX = floor( (tilemap_get_position_x(chunk) + world.x) / TILE_WIDTH);
var chunkTileY = floor( (tilemap_get_position_y(chunk) + world.y) / TILE_HEIGHT);

// works

for(var xx = 0; xx < CHUNK_WIDTH; xx++) {

    var tileX = floor(xx + chunkTileX);

    var height = floor(perlin_1d_octaves(tileX,100,500,0.6,0.6,10, 0));
    chunk._altitudes[xx] = height;
    // TODO: This is redundant for all vertical tilesets... We only need one "height" array per column of chunks.

    //var height = perlin_1d(tileX,10.4,10.20, 10);

    var dirtHeight = floor(height + 16 + perlin_1d(tileX,6,10,69));

    for(var yy = height - chunkTileY; yy < CHUNK_HEIGHT; yy++) {
        if (yy < 0) {
            yy = 0; // Skip to top of chunk
        }
        var tileY = yy + chunkTileY;

        // The higher we go, the less lively it becomes
        var dirtDensity = min(1, 1 - intpow( -1*tileY / 120, 6));
        // If we're below 0, we want dirt. Otherwise, dirtDensity will wrap around and will go down as we move down
        if (tileY > 0) {
            dirtDensity = 1;
        }

        var grassNoise = 0.5 + perlin_1d(tileX, 0.5,20, 254);
        var spawnGrass = grassNoise < dirtDensity;

        dirtHeight -= 11 * (1 - dirtDensity);

        // Empty space
        if (tileY < height) {
            tilemap_set_tile(chunk, xx, yy, -1);
            continue;
        }
        // Grass
        if (tileY == height) {
            if (spawnGrass) {
                var grassID = 4 + round(random(1));
                tilemap_set_tile(chunk, xx, yy, grassID);
            }
            continue;
        }
        // Dirt
        if (tileY < dirtHeight) {
            tilemap_set_tile(chunk, xx, yy, 0);
        // Stone
        } else {
            tilemap_set_tile(chunk, xx, yy, 1);
        }
    }
}


// Make sure we draw our surface
tilemap_redraw_surface(chunk);

#define world_get_surface_height
///world_get_surface_height(world, x);
// What's the surface height (altitude) at a given X position?

var world = argument0,
    xa    = argument1;

var chunk = -1;
for(var chunkIndex = 0; chunkIndex < ds_list_size(world._chunks); chunkIndex++) {
    var tilemap = world._chunks[| chunkIndex];
    var tx = tilemap_get_position_x(tilemap) / tilemap_get_tile_width(tilemap);

    if (xa >= tx && xa < tx + CHUNK_WIDTH) {
        chunk = tilemap;
    }
}

if (chunk == -1) {
    print("[world_get_surface_height] uh oh chunk not found");
    return -1;
}

var cw = tilemap_get_width(chunk);
var xx = xa - cw * floor(xa/cw);

return chunk._altitudes[xx];

#define world_get_gravity
///world_get_gravity(world);
var world = argument0;

return world._gravity;
