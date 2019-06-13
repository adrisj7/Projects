#define __warping
/// Warping!

#define __get_warp_system
/// __get_warp_system()

    return singleton_get(__YUMEngineWarpSystem);

#define warp_room
/// warp_room(room_, tile_x_, tile_y_, orientation);
    var room_ = argument0, tile_x_ = argument1, tile_y_ = argument2, orientation = argument3;

    var sys = __get_warp_system();
    var duration = sys._warp_default_duration;
    warp_room_ext(room_, tile_x_, tile_y_, orientation, duration);


#define warp_room_ext
/// warp_room_ext(room_, tile_x_, tile_y_, orientation, duration);
    var room_ = argument0, tile_x_ = argument1, tile_y_ = argument2, orientation = argument3, duration = argument4;

    var sys = __get_warp_system();
    sys._target_tile_x = tile_x_;
    sys._target_tile_y = tile_y_;
    sys._target_room = room_;
    sys._target_orientation = orientation;

    sys._target_object_index = noone;

    sys._warping = true;
    sys._warp_duration = duration;
    screen_set_fade(1, duration);


#define warp_room_object
/// warp_room_object(room_, object_index_, orientation);
    var room_ = argument0, object_index_ = argument1, orientation = argument1;

    warp_room(room_, -1, -1, orientation);

    var sys = __get_warp_system();
    sys._target_object_index = object_index_;


#define warp_is_warping
/// warp_is_warping()
    // Are we warping to another room?
    var sys = __get_warp_system();
    return sys._warping;

