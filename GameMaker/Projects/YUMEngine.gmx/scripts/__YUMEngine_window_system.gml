#define __YUMEngine_window_system
/// Window system!

#define window_skin_create
/// window_skin_create();
var skin = ds_map_create();
skin[? "sprite"] = 0;        // The sprite our window uses (ex. EXAMPLE_sprWimdow)
skin[? "edge_width"] = 0;    // The border/edge of our window, defining where the middle goes

return skin;

#define window_skin_set_sprite
/// window_skin_set_sprite(skin, sprite);
var skin = argument0;
skin[? "sprite"] = argument1;

#define window_skin_set_edge_width
/// window_skin_set_edge_width(skin, edge_width);
var skin = argument0;
skin[? "edge_width"] = argument1;

#define window_draw
/// window_draw(window_skin, x, y, width, height);
