/// door_set_locked(<door>, locked);

var door,
    locked;

if argument_count > 1 {
    door = argument[0];
    locked = argument[1];
} else {
    door = id;
    locked = argument[0];
}

door._locked = locked;

if locked {
    if door._lockedNote == noone {
        var n = note_create_empty(door.x, door.y);
        with n note_locked_init();
        
        switch door._direction {
            case DIRECTION.LEFT:
                n.y -= TILE_HEIGHT;
                break;
            case DIRECTION.RIGHT:
                n.y -= TILE_HEIGHT;
                n.x -= TILE_WIDTH;
                break;
        }

        note_set_direction(n, door._direction);
        door._lockedNote = n;
    }
} else {
    if door._lockedNote != noone {
        instance_destroy(door._lockedNote);
        door._lockedNote = noone;
    }
}

// Adjust collision tiles around door.
// If locked, make it solid. Otherwise, not.

/*
var left = floor(door.x / tileset_get_collision_width());
var top =  floor(door.y / tileset_get_collision_height()) - 1;
var width = 2;
for(var xa = 0; xa < width; xa++) {
    var xtile = left + xa;
    var ytile = top;
    tileset_set_collision(xtile, ytile, locked);
}
*/

