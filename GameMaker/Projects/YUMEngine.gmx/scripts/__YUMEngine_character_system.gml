#define __YUMEngine_character_system
/// Character system!
/*  Controls characters: movement, interaction, extra sprite animations, ect.
 *
 */
enum DIRECTION {
    LEFT, RIGHT, UP, DOWN
};

// EXAMPLE CHARACTER CREATE CODE
var char = instance_create(32, 32, __YUMEngine_objCharacter_Base);
character_init(char);
character_set_sprites(char, 
        EXAMPLE_sprPlayerTest_Left, 
        EXAMPLE_sprPlayerTest_Right,
        EXAMPLE_sprPlayerTest_Up,
        EXAMPLE_sprPlayerTest_Down
        );

#define character_init
/// character_init(character);
var char = argument0;

// Init tile pos and target to current position
char._tile_x_pos = floor(char.x / TILE_WIDTH);
char._tile_y_pos = floor(char.y / TILE_HEIGHT);
char._tile_x_target = char._tile_x_pos;
char._tile_y_target = char._tile_y_pos;


#define character_set_sprites
/// character_set_sprites(character, sprite_left, sprite_right, sprite_up, sprite_down);
// Sets the directional/facing sprites of a character

var char = argument0;
char._sprite_left =     argument1;
char._sprite_right =    argument2;
char._sprite_up =       argument3;
char._sprite_down =     argument4;

#define character_move
/// character_move(character, move_x, move_y);
// Sets our character's movement target
var char = argument0,
    dx   = argument1,
    dy   = argument2;

// Only move if we're not moving. No canceling.
if !character_is_moving(char) {
    if !tile_has_collision(char._tile_x_pos + dx, char._tile_y_pos)
        char._tile_x_target = char._tile_x_pos + dx;
    else if (dy != 0)
        dx = 0; // So we don't confuse the sprite thing

    if !tile_has_collision(char._tile_x_pos, char._tile_y_pos + dy)
        char._tile_y_target = char._tile_y_pos + dy;
    else if (dx != 0)
        dy = 0; // So we don't confuse the sprite thing

    // Set our new direction. Make sure we don't change if we're already moving
    if  (dx > 0 && character_get_direction(char) == DIRECTION.RIGHT)
     || (dx < 0 && character_get_direction(char) == DIRECTION.LEFT)
     || (dy > 0 && character_get_direction(char) == DIRECTION.DOWN)
     || (dy < 0 && character_get_direction(char) == DIRECTION.UP) {
        // Do nothing, we're moving in the same direction.
     } else {
        // Continue/repeat cases handled, now assume we're ready to change
        if      (dx > 0) char._direction = DIRECTION.RIGHT;
        else if (dx < 0) char._direction = DIRECTION.LEFT;
        else if (dy > 0) char._direction = DIRECTION.DOWN;
        else if (dy < 0) char._direction = DIRECTION.UP;
    }
}

#define character_is_moving
/// character_is_moving(character);
var char = argument0;

return (char._tile_x_pos != char._tile_x_target 
     || char._tile_y_pos != char._tile_y_target);

#define character_get_direction
/// character_get_direction(character);
return argument0._direction;