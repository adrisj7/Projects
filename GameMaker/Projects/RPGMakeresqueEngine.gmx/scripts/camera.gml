#define camera
/**
 * Camera
 *    Camera controls how stuff is drawn yo
 */

#define camera_system_create
/// camera_system_create();
return instance_create(0, 0, objCameraSystem);

#define camera_set_position
/// camera_set_position(x, y);
var xa = argument0,
    ya = argument1;

CAMERA_SYSTEM.x = xa;
CAMERA_SYSTEM.y = ya;

#define camera_set_room_clamp
/// camera_set_room_clamp(turnOnOffClamp);
// Should the camera clamp to the room / not move outside of it?

var clampEnable = argument0;

CAMERA_SYSTEM._roomClamp = clampEnable;