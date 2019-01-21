#define YUMEngine_collision_system
/// Collision System!



#define tile_meeting
/// tile_meeting(x, y);

// place_meeting but for collision tiles
// there is only one type of tile collision
// TODO: Figure out a clean way to have different types of tile collisions!

var xa = argument0,
    ya = argument1;
var system = YUMEngine_collision_system_get();

// What offset from our position to check for collisions
var delta_x = xa - x,
    delta_y = ya-  y;

// Tile bbox coordinates
var left   = floor((bbox_left   + delta_x) / TILE_WIDTH),
    right  = floor((bbox_right  + delta_x) / TILE_WIDTH),
    top    = floor((bbox_top    + delta_y) / TILE_HEIGHT),
    bottom = floor((bbox_bottom + delta_y) / TILE_HEIGHT);

// Check for collisions at any point
for(var tile_x = left; tile_x <= right; tile_x++) {
    for(var tile_y = top; tile_y <= bottom; tile_y++) {
        if tile_has_collision(tile_x, tile_y) {
            return true;
        }
    }
}
return false;

#define tile_has_collision
/// tile_has_collision(tile_x, tile_y);
// RETURNS: Does the tile at (tile_x, tile_y) have a collision?

var tx = argument0,
    ty = argument1;
var system = YUMEngine_collision_system_get();

// If we're out of bounds, no collision
if   tx < 0 || ty < 0 
  || tx >= ds_grid_width(system._grid) || ty >= ds_grid_height(system._grid) {
    return false;
}

return system._grid[# tx, ty];

#define YUMEngine_collision_system_get
/// YUMEngine_collision_system_get();
// Gets the collision system that we're using, making a new one if it doesn't exist.
if !instance_exists(YUMEngine_objCollisionSystem)
    instance_create(x, y, YUMEngine_objCollisionSystem);
return YUMEngine_objCollisionSystem;
