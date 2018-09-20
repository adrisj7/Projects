/// draw_recover();
// After calling draw_reset, use this to "recover" the drawing parameters that we had before.

draw_set_alpha(stack_pop(DRAW_PARAMETER_CONTAINER[? "alpha"]));
draw_set_color(stack_pop(DRAW_PARAMETER_CONTAINER[? "color"]));

