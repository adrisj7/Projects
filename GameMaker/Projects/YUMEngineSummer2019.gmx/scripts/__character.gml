#define __character
/// Character!
    // All scripts related to characters
    
    // Characters are persistant game maker objects that the engine creates.
    // Despite being game maker objects, the characters should almost always
    // be used with the engine and should be treated like you would a
    // ds_list, map or grid.


#define __character_update_delta_tile
/// __character_update_delta_tile(delta_tile_x, delta_tile_y)
    var delta_tile_x = argument0, delta_tile_y = argument1;

    // If we care about collisions, check for em
    if !_ignore_collisions {
        var mx = character_get_tile_x(id),
            my = character_get_tile_y(id);
        // show_debug_message("[DEBUG] (" + string(mx) + ", " + string(my) 
        //         + ") + (" + string(delta_tile_x) + ", " + string(delta_tile_y) + ")");
        if tilemap_collision_get(mx + delta_tile_x, my)
            delta_tile_x = 0;
        if tilemap_collision_get(mx, my + delta_tile_y)
            delta_tile_y = 0;
        if _diagonal_movement {
            if tilemap_collision_get(mx + delta_tile_x, my + delta_tile_y) {
                delta_tile_x = 0;
                delta_tile_y = 0;
                _input_x = 0;
                _input_y = 0;
            }
        }
    }

    _last_tile_input_x = _input_x;
    _last_tile_input_y = _input_y;

    _delta_tile_x = delta_tile_x;
    _delta_tile_y = delta_tile_y;


#define __character_handle_input
/// __character_handle_input()
    // Character responds from its inputs.
    // "User is telling me to do this, what do I REALLY do?"

    // If we're dealing with dialogue and we don't ignore, stop.
    if !__character_can_move() {
        __character_update_delta_tile(0, 0);
        return -1;
    }

    // For diagonal movement, don't move while we're waiting
    if _diagonal_movement && alarm[0] != -1 {
        return -1;
    }

    if !_diagonal_movement {
        // If we don't allow diagonal movement, be picky
        if _input_x != 0 && _input_y != 0 {
            // We are diagonal, trim!

            // If we last didn't move in the x direction, move there!
            if _last_tile_input_x == 0 {
                __character_update_delta_tile(_input_x, 0);
            }
            // If we last didn't move in the y direction, move there!
            if _last_tile_input_y == 0 {
                __character_update_delta_tile(0, _input_y);
            }

        } else {
            // We're not diagonal, so accept it flatly
            __character_update_delta_tile(_input_x, _input_y);
        }
    } else {
        // If we do allow diagonal movement, anything goes!
        __character_update_delta_tile(_input_x, _input_y);
    }


#define __character_reset_sprite_resting
/// __character_reset_sprite_resting()
    // Makes the character sprite reset to the nearest "resting" position
    // Reset to the nearest rest index
    // Cycles of 4 (0 1 2 1) four indices repeating
    _movement_image_index = floor(_movement_image_index);
    var cycle = _movement_image_index % 4;
    if (cycle == 0 || cycle == 2) {
        _movement_image_index += 1;
    }


#define __character_bump_sprite
/// __character_bump_sprite()
    // Bumps the character sprite up one so that we start with a pep in our step
    _movement_image_index += 1;


#define character_set_player
/// character_set_player(character)
    var character = argument0;
    var sys = __get_gamestate();
    sys._player_character = character;

    // Our player is persistent
    character.persistent = true;

#define character_create
/// character_create(tile_xpos, tile_ypos)
    var tile_xpos = argument0, tile_ypos = argument1;
    
    var char = instance_create(0, 0, __YUMEngineCharacter);
    char._current_tile_x = tile_xpos;
    char._current_tile_y = tile_ypos;
    char._delta_tile_x = 0;
    char._delta_tile_y = 0;

    // By default, set the origin to that of the sprite creating it
    character_set_move_origin(char, sprite_xoffset, sprite_yoffset);

    return char;


#define character_set_move_spritesheet
/// character_set_move_spritesheet(character, sprite, subimg)
    var character = argument0, sprite = argument1, subimg = argument2;
    
    var w = sprite_get_width(sprite),
        h = sprite_get_height(sprite);
    character_set_move_spritesheet_part(character, sprite, subimg, 0, 0, w, h);


#define character_set_move_spritesheet_part
/// character_set_move_spritesheet_part(character, sprite, subimg, left, top, width, height)
    var character = argument0, sprite = argument1, subimg = argument2, left = argument3, top = argument4, width = argument5, height = argument6;

    character._movement_sprite = sprite;
    character._movement_sprite_subimg = subimg;
    character._movement_sprite_left = left;
    character._movement_sprite_top = top;
    character._movement_sprite_width = width;
    character._movement_sprite_height = height;


#define character_set_move_spritesheet_frames_per_tile
/// character_set_move_spritesheet_frames_per_tile(character, frames_per_tile)
    var character = argument0, frames_per_tile = argument1;
    
    character._movement_frames_per_tile = frames_per_tile;


#define character_set_move_origin
/// character_set_move_origin(character, origin_x, origin_y)
    var character = argument0, origin_x = argument1, origin_y = argument2;

    character._movement_origin_x = origin_x;
    character._movement_origin_y = origin_y;


#define character_get_image_index
/// character_get_image_index(character)
    var character = argument0;

    return character._movement_image_index;


#define character_move
/// character_move(character, tile_dx, tile_dy)
    var character = argument0, tile_dx = argument1, tile_dy = argument2;

    // Cancel on dialogue/menu
    if !__character_can_move() {
        return -1;
    }

    // if we're starting and moving diagonally, wait a few frames
    if (character._diagonal_movement && !character_is_moving(character)) {
        if character.alarm[0] == -1 {
            character.alarm[0] = character._diagonal_delay;
        }
        // While we're waiting, only accept positive inputs
        if tile_dx != 0 {
            character._input_x = tile_dx;
        }
        if tile_dy != 0 {
            character._input_y = tile_dy;
        }
    } else {
        // If we don't allow diagonals or if we're already moving, just accept
        character._input_x = tile_dx;
        character._input_y = tile_dy;
    }

    // Special case: We're colliding, but orient anyway
    var mx = character_get_tile_x(character),
        my = character_get_tile_y(character);
    var colliding = tilemap_collision_get(mx + tile_dx, my)
                 || tilemap_collision_get(mx, my + tile_dy);

    if character_is_moving(character) {
        colliding = false;
    }

    // Handle Orientation if automatic and we're moving
    var dx = character._delta_tile_x,
        dy = character._delta_tile_y;
    if character._auto_orient && (colliding || character_is_moving(character)) {
        if character._diagonal_movement && (colliding || (dx != 0 && dy != 0)) {
            // If we're colliding, set our input to our movement
            if colliding {
                character._target_orientation = get_orientation(tile_dx, tile_dy);
            } else {
                // If we're moving diagonally and we have a direction conflict,
                // (as in, our current orientation doesn't make sense)
                // pick a direction that works
    
                var conflict = false;
                // If we have a conflict
                switch character._orientation {
                    case dir_up:
                        if (dy > 0) conflict = true;
                        break;
                    case dir_down:
                        if (dy < 0) conflict = true;
                        break;
                    case dir_left:
                        if (dx > 0) conflict = true;
                        break;
                    case dir_right:
                        if (dx < 0) conflict = true;
                        break;
                }
                if conflict {
                    character._target_orientation = get_orientation(dx,dy);
                }
            }
        } else {
            // If we're not, it's pretty simple
            if colliding {
                character._target_orientation = get_orientation(tile_dx, tile_dy);
            } else {
                character._target_orientation = get_orientation(dx, dy);
            }
        }
    }


#define character_look
/// character_look(character, orientation)
    // Makes a character look at one of four cardinal directions
    var character = argument0, orientation = argument1;

    character._target_orientation = orientation;


#define character_set_diagonal_movement
/// character_set_diagonal_movement(character, allow_diagonal)
    var character = argument0, allow_diagonal = argument1;
    
    character._diagonal_movement = allow_diagonal;


#define character_set_move_speed
/// character_set_move_speed(character, move_speed)
    var character = argument0, move_speed = argument1;
    
    character._move_speed = move_speed;


#define character_set_orientation_by_movement
/// character_set_orientation_by_movement(character, set)
    var character = argument0, set = argument1;
    
    character._auto_orient = set;


#define character_set_position
/// character_set_position(character, tile_x_, tile_y_);
    var character = argument0, tile_x_ = argument1, tile_y_ = argument2;

    character._current_tile_x = tile_x_;
    character._current_tile_y = tile_y_;


#define character_set_orientation
/// character_set_orientation(character, orientation);
    var character = argument0, orientation = argument1;
    character._orientation = orientation;


#define character_is_moving
/// character_is_moving(character)
    var character = argument0;

    // If we're still waiting on diagonal input, nah
    if character._diagonal_movement && character.alarm[0] != -1
        return false;

    return character._moving_this_frame;


#define character_took_step
/// character_took_step(character)
    // Did the character step on a tile this frame?
    var character = argument0;
    return character._took_step;


#define character_get_orientation
/// character_get_orientation(character)
    var character = argument0;
    
    return character._orientation;


#define character_get_tile_x
/// character_get_tile_x(character)
    var character = argument0;
    
    return character._current_tile_x;


#define character_get_tile_y
/// character_get_tile_y(character)
    var character = argument0;
    
    return character._current_tile_y;


#define character_get_apparent_x
/// character_get_apparent_x(character)
    var character = argument0;

    return character._draw_x;


#define character_get_apparent_y
/// character_get_apparent_y(character)
    var character = argument0;

    return character._draw_y;


#define character_get_player
/// character_get_player()
    var sys = __get_gamestate();
    return sys._player_character;

#define character_get_facing_tile_x
/// character_get_facing_tile_x(character)
    var character = argument0;
    // Get the x coordinate of the tile the character is facing

    var dx = tiledir_x(1, character_get_orientation(character));
    return character_get_tile_x(character) + dx;

#define character_get_facing_tile_y
/// character_get_facing_tile_y(character)
    var character = argument0;
    // Get the y coordinate of the tile the character is facing

    var dy = tiledir_y(1, character_get_orientation(character));
    return character_get_tile_y(character) + dy;

#define __character_can_move
/// __character_can_move()
    // TODO: Move me back
return (_ignore_dialogue || !dialogue_is_open()) && !menu_is_open() && !warp_is_warping();
