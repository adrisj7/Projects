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
return instance_create(0, 0, objMenu);

#define menu_open_ext
/// menu_open_ext(menu);
// Opens the menu

var menu_system = argument0;

game_set_state(GAME_STATE.MENU);

menu_system._active = true;
menu_system._finished = false;
transition_open(menu_system._transition);

// Open the Main sub menu (eh kinda jank)
menu_set_current_submenu_ext(menu_system, menu_system._menuMain);
transition_open(menu_system._menuMain._transition);
chooser_open(menu_system._menuMain._chooser);

audio_play_sound(soundMenuSelectSoft, AUDIO_PRIORITY_MENU, false);

overlay_set_blur(1);

#define menu_close_ext
/// menu_close_ext(menu);

var menu_system = argument0;

game_set_state(GAME_STATE.GAME);

transition_close(menu_system._transition);

overlay_set_blur(0);

#define menu_open
/// menu_open();

return menu_open_ext(MENU_SYSTEM);

#define menu_close
/// menu_close();

return menu_close_ext(MENU_SYSTEM);
#define menu_sub_open_next
/// menu_sub_open_next(next);
// MUST BE CALLED FROM WITHIN AN objMenuParent INSTANCE

var next = argument0;

menu_set_current_submenu(next);

transition_close(_transition);
transition_open(next._transition);



#define menu_sub_close
///menu_sub_close();
// MUST BE CALLED FROM WITHIN AN objMenuParent INSTANCE

transition_close(_transition);
#define menu_get_current_submenu_ext
/// menu_get_current_submenu_ext(system);
return argument0._currentMenu;


#define menu_get_current_submenu
/// menu_get_current_submenu();
return menu_get_current_submenu_ext(MENU_SYSTEM);

#define menu_set_current_submenu_ext
/// menu_set_current_submenu_ext(system, submenu);
argument0._currentMenu = argument1;

#define menu_set_current_submenu
/// menu_set_current_submenu(submenu);

menu_set_current_submenu_ext(MENU_SYSTEM, argument0);