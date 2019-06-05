#define __menu
/// __menu()
    // Menu system!
    enum MenuType {
        None,       // No menu is open
        Main,       // Main Menu
        QuitConfirm,// Are you sure?
        Player,     // Pressing X in game
        Inventory,  // Inventory/Effects Menu
        Save        // Save Game
    }


#define __get_menu_system
/// __get_menu_system()
    return singleton_get(__YUMEngineMenuSystem);


#define __get_menu_stack
/// __get_menu_stack()
    var sys = __get_menu_system();
    return sys._menu_stack;


#define __menu_get_system_renderer
/// __menu_get_system_renderer(type)
    var type = argument0;
    // TODO: Fill these in while you make em
    switch type {
        case MenuType.None:
            return noone;
        case MenuType.Main:
            return __YUMEngineMenuMain;
        case MenuType.QuitConfirm:
            return __YUMEngineMenuQuitCheck;
        case MenuType.Player:
            return __YUMEngineMenuPlayer;
        case MenuType.Inventory:
            return noone;
        case MenuType.Save:
            return noone;
    }


#define __menu_stack_empty
/// __menu_stack_empty()
    var stack = __get_menu_stack();
    return ds_list_empty(stack);


#define __menu_stack_pop
/// __menu_stack_pop()
    var stack = __get_menu_stack();
    // if __menu_stack_empty() {
    //     // If we're empty, release control
    //     var sys = __get_menu_system();
    //     sys._menu_open = false;
    // } else {
    ds_list_stack_pop(stack);
    // __menu_start_next();
    //}

#define __menu_get_current_type
/// __menu_get_current_type()
    // returns the current menu type
    var sys = __get_menu_system();
    return sys._menu_current;


#define __menu_get_next_type
/// __menu_get_next_type()
    if __menu_stack_empty() {
        return MenuType.None;
    }
    var st = __get_menu_stack();
    return ds_list_stack_top(st);


#define __menu_get_current_renderer
/// __menu_get_current_renderer()
    var type = __menu_get_current_type();
    return __menu_get_system_renderer(type);


#define __menu_get_next_renderer
/// __menu_get_next_renderer()
    var type = __menu_get_next_type();
    return __menu_get_system_renderer(type);


#define __menu_start_next
/// __menu_start_next()
    // Starts the next menu
    var sys = __get_menu_system();
    __menu_start_next_ext(sys._fade_duration_default);


#define __menu_start_next_ext
/// __menu_start_next_ext(duration)
    var duration = argument0;
    // Starts the next menu, with a certain duration transition
    var sys = __get_menu_system();
    sys._fading = true;
    sys._fade_closing = true;
    sys._menu_open = true;
    sys._fade_duration = duration;
    screen_set_fade(1, duration);


#define __menu_force_close
/// _menu_force_close()
    // Clears the menu and force closes it
    var sys = __get_menu_system();
    sys._menu_open = false;


#define __menu_is_fading
/// __menu_is_fading()
    // Is the menu transitioning?
    var sys = __get_menu_system();
    return sys._fading;


#define __menu_sys_is_just_opened
/// __menu_sys_is_just_opened()
    // Has this menu system just been opened?
    return _active && !_last_active;


#define __menu_is_waiting_to_open
/// __menu_is_waiting_to_open()
    // Returns whether we're waiting for the menu to open
    var dsys = __get_dialogue_system();
    return dsys._menu_input_buffer;


#define menu_is_open
/// menu_is_open()
    var sys = __get_menu_system();
    return sys._menu_open;


#define menu_open
/// menu_open(type)
    var type = argument0;
    var sys = __get_menu_system();
    menu_open_ext(type, sys._fade_duration_default);

#define menu_open_ext
/// menu_open_ext(type, duration)
    // Adds a menu to our menu stack
    var type = argument0, duration = argument1;
    var st = __get_menu_stack();
    ds_list_stack_push(st, type);
    // New menu! This menu is always the next one
    __menu_start_next_ext(duration);


#define menu_close
/// menu_close()
    __menu_start_next();


#define menu_close_ext
/// menu_close_ext(duration)
    var duration = argument0;
    // Closes our current menu
    // var st = __get_menu_stack();
    //__menu_stack_pop();
    // ds_list_stack_pop(st);
    __menu_start_next_ext(duration);


#define main_menu_set_background
/// main_menu_set_background(background)
    // Sets the background sprite that's loaded at the beginning of the game
    var background = argument0;
    var main = __menu_get_system_renderer(MenuType.Main);
    main._background = background;