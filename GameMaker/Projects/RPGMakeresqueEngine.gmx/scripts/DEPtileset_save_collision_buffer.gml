/// tileset_save_collision_buffer(tileset, collision_buffer);

var tileset   = argument0,
    colbuffer = argument1;

var fname = DEPtileset_get_collision_buffer_name(tileset);

var colWidth = DEPtileset_get_collision_buffer_width(tileset);
var colHeight = DEPtileset_get_collision_buffer_height(tileset);

var saveSurface = surface_create(colWidth, colHeight);

// Draw to the surface
surface_set_target(saveSurface);
for(var xx = 0; xx < colWidth; xx++) {
    for(var yy = 0; yy < colHeight; yy++) {
        var value = DEPtileset_collision_buffer_get(colbuffer, tileset, xx, yy);
        draw_set_color(make_color_rgb(value, value, value));
        draw_point(xx, yy);
    }
}

/*for(var i = 0; i < colWidth*colHeight; i++) {    

    var value = buffer_read(colbuffer, buffer_u8);
    var xa = value % colWidth;
    var ya = floor(value / colWidth);

    draw_point_colour(xa, ya, make_color_rgb(value, value, value));
}*/
surface_reset_target();

// Un-sandbox
fname = get_save_filename("Png file|*.png", DEPtileset_get_collision_buffer_name(tileset, false));

surface_save(saveSurface, fname);

// Cleanup
surface_free(saveSurface);
