///explode_object(id, explosive_force, chunks_per_side, explosive_force_randomness, explosive_angle_randomness, explosive_angle_vel_randomness);

var ida = argument0,
    force = argument1,
    chunks = argument2,
    randomness_force = argument3,
    randomness_angle = argument4,
    randomness_angle_vel = argument5;

var ida_spr = ida.sprite_index;
var w = sprite_get_width(ida_spr);
var h = sprite_get_height(ida_spr);

for(var xx = 0; xx < chunks; xx++) {
    for(var yy = 0; yy < chunks; yy++) {
        var prop_x = xx / chunks;
        var prop_y = yy / chunks;

        var dx = w * prop_x;
        var dy = h * prop_y;

        var real_x = dx + ida.x - ida.sprite_xoffset;
        var real_y = dy + ida.y - ida.sprite_yoffset;

        var f = force + randomness_force * (random(2) - 1);

        var force_angle = point_direction(w/2,h/2,dx,dy);
        force_angle += randomness_angle * (random(2) - 1);
        var force_x = lengthdir_x(f, force_angle);
        var force_y = lengthdir_y(f, force_angle);            

        var angle_vel = randomness_angle_vel * (random(2) - 1);

        var new_particle = instance_create(real_x, real_y, obj_particle_split);
        new_particle.sprite_index = ida_spr;
        new_particle.image_index = ida.image_index;
        new_particle.split_x_fraction = prop_x;
        new_particle.split_y_fraction = prop_y;
        new_particle.split_width_fraction = 1.0 / chunks;
        new_particle.split_height_fraction = 1.0 / chunks;
        new_particle.split_image_angular_velocity = angle_vel;
        new_particle.velx = force_x;
        new_particle.vely = force_y;
    }
}
with (ida) {
    instance_destroy();
}