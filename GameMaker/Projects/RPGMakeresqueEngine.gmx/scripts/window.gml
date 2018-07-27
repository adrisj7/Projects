#define window
/** WINDOW
 *
 *
 *    Purely visual.
 *
 */

#define draw_window_ext
/// draw_window_ext( sprite, subimg, left, top, spritewidth, spriteheight, corner_size, x, y, windowwidth, windowheight);
// Full control script to draw a window from a sprite.

var sprite = argument0,
    subimg = argument1,
    left   = argument2,
    top    = argument3,
    sprw   = argument4,
    sprh   = argument5,
    corner = argument6,
    xa     = argument7,
    ya     = argument8,
    winw   = argument9,
    winh   = argument10;

var color = draw_get_color();
var alpha = draw_get_alpha();


// Scaling

// Top and Bottom middles
var midscale = (winw - 2*corner) / (sprw - 2*corner);
// Middle left and right
var sidescale = (winh - 2*corner) / (sprh - 2*corner);
    
// Middle Middle
draw_sprite_part_ext(sprite, subimg, left + corner, top + corner, sprw - 2*corner, sprh - 2*corner, xa+corner, ya+corner, midscale, sidescale, color, alpha);

// Top Left Corner
draw_sprite_part_ext(sprite, subimg, left, top, corner, corner, xa, ya, 1, 1, color, alpha);
// Top Right Corner
draw_sprite_part_ext(sprite, subimg, left + sprw - corner, top, corner, corner, xa + winw - corner, ya, 1, 1, color, alpha);
// Bottom Left Corner
draw_sprite_part_ext(sprite, subimg, left, top  + sprh - corner, corner, corner, xa, ya  + winh - corner, 1, 1, color, alpha);
// Bottom Right Corner
draw_sprite_part_ext(sprite, subimg, left + sprw - corner, top + sprh - corner, corner, corner, xa + winw - corner, ya + winh - corner, 1, 1, color, alpha);

// Top Middle
draw_sprite_part_ext(sprite, subimg, left + corner, top, sprw - 2*corner, corner, xa + corner, ya, midscale, 1, color, alpha);
// Middle Left
draw_sprite_part_ext(sprite, subimg, left, top + corner, corner, sprh - 2*corner, xa, ya + corner, 1, sidescale, color, alpha);
// Middle Right
draw_sprite_part_ext(sprite, subimg, left + sprw - corner, top + corner, corner, sprh - 2*corner, xa + winw - corner, ya + corner, 1, sidescale, color, alpha);
// Bottom Middle
draw_sprite_part_ext(sprite, subimg, left + corner, top + sprh - corner, sprw - 2*corner, corner, xa + corner, ya + winh - corner, midscale, 1, color, alpha);


#define draw_window
/// draw_window(x, y, width, height);
// Standard window drawing

var xa = argument0,
    ya = argument1,
    w  = argument2,
    h  = argument3;

draw_window_ext(sprWindow, 0, 0, 0, 32, 32, 8, xa, ya, w, h);