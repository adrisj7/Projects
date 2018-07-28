#define window
/** WINDOW
 *
 *
 *    Purely visual.
 *
 */

#define draw_window_ext
/// draw_window_ext( sprite, subimg, left, top, spritewidth, spriteheight, midsurface, corner_size, x, y, windowwidth, windowheight, alpha);
// Full control script to draw a window from a sprite.

var sprite  = argument0,
    subimg  = argument1,
    left    = argument2,
    top     = argument3,
    sprw    = argument4,
    sprh    = argument5,
    midsurf = argument6,
    corner  = argument7,
    xa      = argument8,
    ya      = argument9,
    winw    = argument10,
    winh    = argument11,
    alpha   = argument12;


//if true || !surface_exists(surface) {

    //SURFACE_WINDOW = surface_create(winw, winh);

surface_set_target(get_gui_scratchpad_surface(0));

d3d_transform_stack_push();
d3d_transform_set_identity();

draw_reset();
draw_clear_alpha(c_black, 0);

var a     = draw_get_alpha();
var color = draw_get_color();

// Scaling

// Top and Bottom middles
var midscale = (winw - 2*corner) / (sprw - 2*corner);
// Middle left and right
var sidescale = (winh - 2*corner) / (sprh - 2*corner);

// Top Left Corner
draw_sprite_part_ext(sprite, subimg, left, top, corner, corner, 0, 0, 1, 1, color, a);
// Top Right Corner
draw_sprite_part_ext(sprite, subimg, left + sprw - corner, top, corner, corner, winw - corner, 0, 1, 1, color, a);
// Bottom Left Corner
draw_sprite_part_ext(sprite, subimg, left, top  + sprh - corner, corner, corner, 0, winh - corner, 1, 1, color, a);
// Bottom Right Corner
draw_sprite_part_ext(sprite, subimg, left + sprw - corner, top + sprh - corner, corner, corner, winw - corner, winh - corner, 1, 1, color, a);

// Top Middle
draw_sprite_part_tiled(sprite, subimg, left + corner, top, sprw - 2*corner, corner, winw - 2*corner, corner, corner, 0);
//draw_sprite_part_ext(sprite, subimg, left + corner, top, sprw - 2*corner, corner, corner, 0, midscale, 1, color, a);
// Middle Left
draw_sprite_part_tiled(sprite, subimg, left, top  + corner, corner, sprh - 2*corner, corner, winh - corner*2, 0, corner);
//draw_sprite_part_ext(sprite, subimg, left, top + corner, corner, sprh - 2*corner, 0, corner, 1, sidescale, color, a);
// Middle Right
draw_sprite_part_tiled(sprite, subimg, left + sprw - corner, top + corner, corner, sprh - 2*corner, corner, winh - corner*2, winw - corner, corner);
//draw_sprite_part_ext(sprite, subimg, left + sprw - corner, top + corner, corner, sprh - 2*corner, winw - corner, corner, 1, sidescale, color, a);
// Bottom Middle
draw_sprite_part_tiled(sprite, subimg, left + corner, top + sprh - corner, sprw - 2*corner, corner, winw - corner*2, corner, corner, winh - corner);
//draw_sprite_part_ext(sprite, subimg, left + corner, top + sprh - corner, sprw - 2*corner, corner, corner, winh - corner, midscale, 1, color, a);

// Middle Middle
if surface_exists(midsurf) {
    draw_surface_part(midsurf, corner, corner, winw - corner*2, winh - corner*2, corner, corner);
}

surface_reset_target();
d3d_transform_stack_pop();
//}

draw_surface_part_ext(get_gui_scratchpad_surface(0), 0, 0, winw, winh, xa, ya, 1, 1, c_white, alpha);

#define draw_window
/// draw_window(x, y, width, height, alpha);
// Standard window drawing

var xa = argument0,
    ya = argument1,
    w  = argument2,
    h  = argument3,
    a  = argument4;

draw_window_ext(sprWindow, 0, 0, 0, 32, 32, SURFACE_WINDOW_MIDDLE, 8, xa, ya, w, h, a);

#define draw_window_floaty_next
/// draw_window_floaty_next(x, y);
// Draws the floaty "next" icon, indicating that the dialogue is over and the next
// dialogue can be called.

var xa = argument0,
    ya = argument1;

draw_sprite_part(sprWindow, 0, 32, 0, 16, 16, xa- 8, ya - 8);
#define draw_window_selector
/// draw_window_selector(x, y, width, height, alpha);
// Draw window selector thing (menus, choosers, ect.)

var xa = argument0,
    ya = argument1,
    w  = argument2,
    h  = argument3,
    a  = argument4;

draw_window_ext(sprWindow, 0, 32, 32, 16, 16, -1, 5, xa, ya, w, h, a);