/// draw_sprite_part_tiled(sprite, subimage, left, top, width, height, tileswidth, tilesheight, xa, ya);
// Draws a sprite tiled, at (x, y) filling in a rectangle of (width, height).

var sprite = argument0,
    index  = argument1,
    left   = argument2,
    top    = argument3,
    subw   = argument4,
    subh   = argument5,
    width  = argument6,
    height = argument7,
    xa     = argument8,
    ya     = argument9;

var drawto = get_gui_scratchpad_surface(1);


var countx = ceil(width / subw),
    county = ceil(height / subh);


surface_set_target(drawto);
draw_reset();
draw_clear_alpha(c_black, 0);

for(var xx = 0; xx < countx; xx++) {
    for(var yy = 0; yy < county; yy++) {
        draw_sprite_part(sprite, index, left, top, subw, subh, xx*subw, yy*subh);
    }
}

surface_reset_target();
draw_surface_part(drawto, 0, 0, width, height, xa, ya);

//return drawto;
