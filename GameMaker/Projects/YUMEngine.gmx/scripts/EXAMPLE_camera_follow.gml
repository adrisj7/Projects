/// EXAMPLE_camera_follow(target_x, target_y)
// Simple camera script that you can rewrite yourself
var tx = argument0 + view_wview/2,
    ty = argument1 + view_hview/2;

var coeff = 0.4;
view_xview += (tx - view_xview) * coeff;
view_yview += (ty - view_yview) * coeff;
