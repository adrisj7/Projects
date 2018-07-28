#define game_state
/** game_state
 *
 * Controls the game.
 *
 */

#define game_get_state
/// game_get_state()
// Returns the state of the game, from the following states:
/*
 * GAME: In game with the character moving around (most common state!)
 * TITLE: At the title screen
 * MENU: In the game's menu (inventory/saving)
 * DIALOGUE: In a dialogue or cutscene
 */

return Gamestate._state;

#define game_set_state
/// game_set_state(state)
// Sets the state of our gamestate.

var state = argument0;

Gamestate._state = state;

#define game_is_debug
/// game_is_debug()
// Are we in our own debug mode?

return Gamestate._debug;

#define game_get_time
/// game_get_time()
// Returns current game time

return Gamestate._time;

#define game_get_dtime
/// game_get_dtime()
// Returns how fast the game dtime is

return Gamestate._dtime;

#define game_set_dtime
/// game_set_dtime(dtime)

var dtime = argument0;

Gamestate._dtime = dtime;

#define game_is_debug_overlay
/// game_is_debug_overlay()
// Should we draw the debug overlay?
return game_is_debug() && Gamestate._debug_overlay;