#define __event
/// Event system!

// Autorun and parallel triggers are not needed, since you can do those yourself
// via Create event of any ol' object

enum EventTrigger {
    Action,
    Touch
};

enum Event {
    xtile,      // x tile pos
    ytile,      // y tile pos
    trigger,    // trigger type (see EventTrigger enum)
    script,     // script on trigger
    data,       // extra data
    sizeof
};

#define __get_event_system
/// __get_event_system()
    return singleton_get(__YUMEngineEventSystem);

#define __event_set_current
/// __event_set_current(event);
    var event = argument0;
    // When event scripts are called, we might want to know the event that called it

    var sys = __get_event_system();
    sys._current_event = event;



#define __event_get_current
/// __event_get_current()

    var sys = __get_event_system();
    return sys._current_event;


#define event_create
/// event_create(tile_xpos, tile_ypos, script_index)
    var tile_xpos = argument0, tile_ypos = argument1, script_index = argument2;
    // Creates an event at a certain 

    var e/*:Event*/ = array_create(Event.sizeof);
    e[@Event.xtile]     = tile_xpos;
    e[@Event.ytile]     = tile_ypos;
    e[@Event.trigger]   = EventTrigger.Action;
    e[@Event.script]    = script_index;
    e[@Event.data]      = noone;

    // Add event to the event list
    //var list = __event_room_get_list(room);
    //ds_list_add(list, e);

    // Add to the event grid
    var e_grid = __get_tilemap_event();
    // If we already have something here
    if event_get(tile_xpos, tile_ypos) != noone {
        show_error("Duplicate event created! This event will replace the older one."
                 + "If this is on purpose, then ou should probably remove this message.", false);
    }
    tilemap_set_tile(e_grid, tile_xpos, tile_ypos, e);

    return e;

#define event_set_trigger
/// event_set_trigger(event, trigger_);
    var event/*:Event*/ = argument0, trigger_ = argument1;
    // Sets the trigger type of the event

    event[@Event.trigger] = trigger_;

#define event_set_data
/// event_set_data(event, data_);
    var event/*:Event*/ = argument0, data_ = argument1;
    // Adds some extra data that an event might want in its script

    event[@Event.data] = data_;


#define event_get
/// event_get(tile_xpos, tile_ypos)
    var tile_xpos = argument0, tile_ypos = argument1;
    // Gets the event that's at that tile, or returns noone if no event exists there

    var tmap = __get_tilemap_event();
    return tilemap_get_tile(tmap, tile_xpos, tile_ypos);

#define event_clear
/// event_clear(tile_xpos, tile_ypos)
    var tile_xpos = argument0, tile_ypos = argument1;
    // Removes the event at that tile position

    var tmap = __get_tilemap_event();
    tilemap_set_tile(tmap, tile_xpos, tile_ypos, noone);

#define event_call_script
/// event_call_script(event);
    var event/*:Event*/ = argument0;
    // Runs the script associated with an event

    var scr = event[@Event.script];

    // Run script and set current event as the one that's calling the script
    if script_exists(scr) {
        var prev = __event_get_current(event);
        __event_set_current(event);        
        script_execute(scr);
        __event_set_current(prev);
    }

#define event_get_trigger
/// event_get_trigger(event)
    var event/*:Event*/ = argument0;
    // Gets the trigger type of the event

    return event[@Event.trigger];
#define event_get_data
/// event_get_data(event)
    var event/*:Event*/ = argument0;
    // Gets the trigger type of the event

    return event[@Event.data];
