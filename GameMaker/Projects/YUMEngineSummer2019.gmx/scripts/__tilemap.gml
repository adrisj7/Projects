#define __tilemap
/// Tile map!
// All scripts related to tile maps

// Make sure our tilemap system is initialized
gml_pragma("global", "__tilemap_system_init()");


#define __tilemap_system_init
/// __tilemap_system_init()
// Inits the tilemap system. Should be called before anything else

// Map of maps
global.tilemap_system = ds_map_create();

// Initialize variables
var ts = __get_tilemap_system();
ts[? "tilemap_count"] = 0;          // How many tilemaps?
ts[? "tilemaps"] = array_create(0); // The tilemap array

#define __get_tilemap_system
/// __get_tilemap_system()
// Returns the global tilemap system object

return global.tilemap_system;

#define __get_tilemap
/// __get_tilemap(tilemap_id)
// Returns the tilemap object of a given tilemap_id

var tmap_id = argument0;

var s = __get_tilemap_system();

if tilemap_id >= s[? "tilemap_count"] {
    // Invalid tilemap
}

#define script0

#define tilemap_get_tile
/// tilemap_get_tile(tilemap, tile_x, tile_y)

var tmap =  __get_tilemap(argument0);
var tx =    argument1;
var ty =    argument2;


#define tilemap_get_pos_x
/// tilemap_get_pos_x(tilemap, tile_x)


var tmap = __get_tilemap(argument0);
