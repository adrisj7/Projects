/// draw_reset();
// Resets color, alpha, ect... to defaults.

stack_push(DRAW_PARAMETER_CONTAINER[? "alpha"], draw_get_alpha());
stack_push(DRAW_PARAMETER_CONTAINER[? "color"], draw_get_color());

draw_set_alpha(1);
draw_set_color(c_white);

