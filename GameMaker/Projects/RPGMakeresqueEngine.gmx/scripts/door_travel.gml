/// door_travel(targetRoom, targetID)

var targetRoom = argument0,
    targetID = argument1;

if targetRoom == -1 {
    show_error("Door has no defined room!", false);
    return 0;
}
room_goto(targetRoom);

// This traveler automatically sets the player position after one frame and destroys itself
var traveler = instance_create(0, 0, objDoorTraveler);

traveler._targetID = targetID;
