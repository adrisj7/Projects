#define menu
/** MENU
 *
 *    Inventory, saving, character status, ect.
 *
 *
 *
 */ 

#define menu_system_create
/// menu_system_create();
// Creates our menu gui thing.
return instance_create(0, 0, objMenuMain);

#define menu_open_ext
/// menu_open_ext(menu);
// Opens the menu

var menu_system = argument0;

game_set_state(GAME_STATE.MENU);

menu_system._active = true;
menu_system._finished = false;
menu_system._state = DIALOGUE_STATE.OPENING;
menu_system._transitionCounter = 0;

overlay_set_blur(1);

#define menu_close_ext
/// menu_close_ext(menu);

var menu_system = argument0;

menu_system._state = DIALOGUE_STATE.CLOSING;
menu_system._transitionCounter = 0;

overlay_set_blur(0);

#define menu_open
/// menu_open();

return menu_open_ext(MENU_SYSTEM);

#define menu_close
/// menu_close();

return menu_close_ext(MENU_SYSTEM);
