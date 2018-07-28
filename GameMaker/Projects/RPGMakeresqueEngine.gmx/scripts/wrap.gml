/// wrap(value, min, max);
// Wraps a value around min to max (where max is not included)

var val = argument0,
    mi  = argument1,
    ma  = argument2;

val -= mi;
return mod2(val, ma - mi) + mi;
