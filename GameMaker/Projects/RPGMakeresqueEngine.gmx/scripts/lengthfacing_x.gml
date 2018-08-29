#define lengthfacing_x
/// lengthfacing_x(length, facing);
var length = argument0,
    facing = argument1;
switch facing {
    case DIRECTION.RIGHT:
        return length;
    case DIRECTION.LEFT:
        return -1 * length;
}
return 0;

#define lengthfacing_y
/// lengthfacing_y(length, facing);
var length = argument0,
    facing = argument1;
switch facing {
    case DIRECTION.DOWN:
        return length;
    case DIRECTION.UP:
        return -1 * length;
}
return 0;