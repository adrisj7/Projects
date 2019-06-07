#define __misc
/// __misc()

    // Miscellaneous functions and variables


#define singleton_get
/// singleton_get(obj_index)
    // Gets a singleton object, creating one if it doesn't exist.
    var obj_index = argument0;
    if !instance_exists(obj_index) {
        var obj = instance_create(0, 0, obj_index);
        event_perform_object(obj, ev_create, 0);
    }
    return obj_index;


#define osc
/// osc(minval, maxval, wavelength, time)
    // returns a sine wave with specific properties
    var minval = argument0, maxval = argument1, wavelength = argument2, time = argument3;
    var amp = maxval - minval;
    var val = sin(time * 2*pi / wavelength);
    return minval + amp * (val + 1) * 0.5;


#define script_execute_args_array
/// script_execute_args_array(script, array)
    // Executes a script with args as an array
    // There is no automatic way to do this in game maker. This is annoying.
    var script = argument0, array = argument1;
    var a = array;
    switch array_length_1d(a) {
        case 0:
            return script_execute(script);
        case 1:
            return script_execute(script, a[0]);
        case 2:
            return script_execute(script, a[0], a[1]);
        case 3:
            return script_execute(script, a[0], a[1], a[2]);
        case 4:
            return script_execute(script, a[0], a[1], a[2], a[3]);
        case 5:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4]);
        case 6:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5]);
        case 7:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
        case 8:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
        case 9:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
        case 10:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9]);
        case 11:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10]);
        case 12:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11]);
        case 13:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12]);
        case 14:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13]);
        case 15:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14]);
        case 16:
            return script_execute(script, a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]);
        default:
            show_error("I think there are too many arguments here: " + string(array_length_1d(a)) + ".", true);
    }

#define draw_sprite_part_tiled_size
/// draw_sprite_part_tiled_size(sprite, subimg, left, top, width, height, xpos, ypos, draw_width, draw_height)
    // Draws part of a sprite tiled, but not infinitely
    var sprite = argument0, subimg = argument1, left = argument2, top = argument3, width = argument4, height = argument5, xpos = argument6, ypos = argument7, draw_width = argument8, draw_height = argument9;

    var w = width,
        h = height;
    var xcount = draw_width  / w,
        ycount = draw_height / h;

    var remainder_width = (xcount % 1) * w,
        remainder_height = (ycount % 1) * h;
    xcount = floor(xcount);
    ycount = floor(ycount);

    for(var yy = 0; yy < ycount; ++yy) {
        for(var xx = 0; xx < xcount; ++xx) {
            draw_sprite_part(sprite, subimg, left, top, width, height, xpos+xx*w, ypos+yy*h);
        }
        // While we're at it, draw the right edge
        draw_sprite_part(sprite, subimg,
                         left, top,
                         remainder_width, h,
                         xpos + xcount*w, ypos + yy*h
        );
    }

    // Draw the bottom edge
    for (var xx = 0; xx < xcount; ++xx) {
        draw_sprite_part(sprite, subimg,
                         left, top,
                         w, remainder_height,
                         xpos + xx*w, ypos + ycount*h
        );                
    }


    // Draw the bottom right corner
    draw_sprite_part(sprite, subimg,
                     left, top,
                     remainder_width, remainder_height,
                     xpos + xcount*w, ypos + ycount*h
    );

#define draw_sprite_part_corners
/// draw_sprite_part_corners(sprite, subimg, left, top, width, height, corner_size, xpos, ypos, draw_width, draw_height)
    // Draws a border with a corner size. Used for window border and window cursor drawing
    var sprite = argument0, subimg = argument1, left = argument2, top = argument3, width = argument4, height = argument5, corner_size = argument6, xpos = argument7, ypos = argument8, draw_width = argument9, draw_height = argument10;

    // small and big sizes
    /*
        sbbbbs
        b    b
        b    b
        b    b
        sbbbbs
    */
    var sw = corner_size,
        sh = corner_size,
        bw = width  - 2*sw,
        bh = height - 2*sh;

    // bw:  big width, the width on the sprite subimg
    // bdw: big draw width, what the width of the edges actually is
    var bdw = draw_width  - 2*sw,
        bdh = draw_height - 2*sh;

    var edge_scale_x = bdw / bw,
        edge_scale_y = bdh / bh;

    var color = c_white;
    var alpha = draw_get_alpha();

    // Top left corner
    draw_sprite_part(sprite, subimg,
                         left, top,
                         sw, sh,
                         xpos, ypos
    );
    // Top right corner
    draw_sprite_part(sprite, subimg,
                         left + (width - sw), top,
                         sw, sh,
                         xpos + (draw_width - sw), ypos
    );
    // Bottom left corner
    draw_sprite_part(sprite, subimg,
                         left, top + (height - sh),
                         sw, sh,
                         xpos, ypos + (draw_height - sh)
    );
    // Bottom right corner
    draw_sprite_part(sprite, subimg,
                         left + (width - sw), top + (height - sh),
                         sw, sh,
                         xpos + (draw_width - sw), ypos + (draw_height - sh)
    );

    // Top middle
    draw_sprite_part_ext(sprite, subimg, 
                         left+sw, top,
                         bw, sh,
                         xpos + sw, ypos,
                         edge_scale_x, 1,
                         color, alpha
    );
    // Bottom middle
    draw_sprite_part_ext(sprite, subimg, 
                         left + sw, top + (height - sh),
                         bw, sh,
                         xpos + sw, ypos + (draw_height - sh),
                         edge_scale_x, 1,
                         color, alpha
    );
    // Left middle
    draw_sprite_part_ext(sprite, subimg, 
                         left, top + sh,
                         sw, bh,
                         xpos, ypos + sh,
                         1, edge_scale_y,
                         color, alpha
    );
    // Right middle
    draw_sprite_part_ext(sprite, subimg, 
                         left + (width - sw), top + sh,
                         sw, bh,
                         xpos + (draw_width - sw), ypos + sh,
                         1, edge_scale_y,
                         color, alpha
    );
    // I think that about covers it!

#define get_orientation
/// get_orientation(delta_x, delta_y)
    var delta_x = argument0, delta_y = argument1;

    // Standard, x or y are zero cases
    if (delta_x == 0) {
        if delta_y < 0
            return dir_up;
        return dir_down;
    }

    if (delta_y == 0) {
        if delta_x < 0
            return dir_left;
        return dir_right;
    }

    // If we have some weird case where we're at an angle, handle that too
    if abs(delta_x) >= abs(delta_y) {
        // x is more powerful
        if delta_x < 0
            return dir_left;
        return dir_right;
    } else {
        // y is more powerful
        if delta_y < 0
            return dir_up;
        return dir_down;
    }

    // We shouldn't get here
    return dir_right;


#define tiledir_x
/// tiledir_x(distance, orientation)
    var distance = argument0, orientation = argument1;

    switch orientation {
        case dir_up:
        case dir_down:
            return 0;
        case dir_left:
            return -1;
        case dir_right:
            return 1;
    }

    // Shouldn't get here
    return 0;


#define tiledir_y
/// tiledir_y(distance, orientation)
    var distance = argument0, orientation = argument1;

    switch orientation {
        case dir_left:
        case dir_right:
            return 0;
        case dir_up:
            return -1;
        case dir_down:
            return 1;
    }

    // Shouldn't get here
    return 0;


#define view_follow_point
/// view_follow_point(xpos, ypos, respect_border)
    var xpos = argument0, ypos = argument1, respect_border = argument2;

    var w = view_wview[0],
        h = view_hview[0];

    // If we want to respect our border, our target may be bounded
    if respect_border {
        if room_width <= w {
            // Our room is too small
            xpos = room_width / 2;
        } else {
            // Our view fits with some boundaries
            xpos = clamp(xpos, w/2, room_width - w/2);
        }
        if room_height <= h {
            // Our room is too small
            ypos = room_height / 2;
        } else {
            // Our view fits with some boundaries
            ypos = clamp(ypos, h/2, room_height - h/2);
        }
    }

    view_xview[0] = xpos - w/2;
    view_yview[0] = ypos - h/2;

    //view_xview[0] = round(view_xview[0]);
    //view_yview[0] = round(view_yview[0]);


#define view_follow_character
/// view_follow_character(character, respect_border)
    var character = argument0, respect_border = argument1;

    // Remember to add half a tile, since we want to center it to
    // the CENTER of the tile. 

    //Not sure why one is half and the other isn't,
    // I experimentally determined that.
    var cx = character_get_apparent_x(character) + tile_width,
        cy = character_get_apparent_y(character) + tile_height/2;

    view_follow_point(cx, cy, respect_border)


#define screen_set_fade
/// screen_set_fade(fade, frames)
    var fade = argument0, frames = argument1;
    var g = __get_gamestate();

    if frames == 0 {
        g._fade_value = fade;
        g._fade_target = fade;
        g._fade_delta = 0;
    } else {
        g._fade_target = fade;
        var delta = fade - g._fade_value;
        g._fade_delta = delta / frames;
    }


#define screen_get_fade_percent
/// screen_get_fade_percent()
    var g = __get_gamestate();
    return g._fade_value;