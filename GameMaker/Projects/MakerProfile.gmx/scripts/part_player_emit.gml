///part_player_emit(part_obj, x,y, direction);
var part_obj = argument0,
    px = argument1,
    py = argument2,
    dir = argument3;

// maybe instead of ps_shape_line, ps_shape_rectangle?
part_type_direction(part_obj.pt,dir + 90,dir + 270,0,0.01);
part_emitter_region(part_obj.ps,part_obj.pe,px - lengthdir_x(10,dir), px + lengthdir_x(10,dir),y - lengthdir_y(10,dir),y + lengthdir_y(10,dir),ps_shape_line,ps_distr_gaussian);
part_emitter_burst(part_obj.ps,part_obj.pe,part_obj.pt,10 + random(10));
