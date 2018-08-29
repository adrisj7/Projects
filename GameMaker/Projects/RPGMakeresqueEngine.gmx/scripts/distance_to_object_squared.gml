#define distance_to_object_squared
/// distance_to_object_squared(object);
var obj = argument0;

return distance_to_point_squared(obj.x, obj.y);

#define distance_to_point_squared
/// distance_to_point_squared(x, y);

var xa = argument0,
    ya = argument1;

return sqr(xa - x) + sqr(ya - y);

#define point_distance_squared
/// point_distance_squared(x1, y1, x2, y2);

var x1 = argument0,
    y1 = argument1,
    x2 = argument2,
    y2 = argument3;
    
return sqr(x1 - x2) + sqr(y1 - y2);