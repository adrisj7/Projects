/// editor_draw_window(x, y, width, height, borderColor, fillColor);
var xa = argument0,
    ya = argument1,
    w  = argument2,
    h  = argument3,
    bc = argument4,
    fc = argument5;

/// Draw window with border
draw_set_color(fc); // Fillcolor
draw_rectangle(xa, ya, xa+w, ya+h,false);
draw_set_color(bc); // Border color
draw_rectangle(xa, ya, xa+w, ya+h,true);
