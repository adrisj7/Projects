/// door_init(target_room, target_id);
// MUST be called within a door "create" event.

var targetRoom = argument0,
    targetID   = argument1;

event_inherited();

_targetRoom = targetRoom;
_targetID = targetID;
