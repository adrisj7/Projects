#define editor_draw_dotted_vertical_line
/// editor_draw_dotted_vertical_line(x, y1, y2, phase);

var xa = argument0,
    y1 = argument1,
    y2 = argument2,
    t  = argument3;

var sep = 4;

for (var dy = mod2(t, 2*sep); dy < y2 - y1; dy += sep*2) {
    draw_line(xa, y1+dy, xa, y1+dy+sep);
}

#define editor_draw_dotted_horizontal_line
/// editor_draw_dotted_horizontal_line(x1, x2, y, phase);

var x1 = argument0,
    x2 = argument1,
    ya = argument2,
    t  = argument3;

var sep = 4;

for (var dx = mod2(t, 2*sep); dx < x2 - x1; dx += sep*2) {
    draw_line(x1+dx, ya, x1+dx+sep, ya);
}