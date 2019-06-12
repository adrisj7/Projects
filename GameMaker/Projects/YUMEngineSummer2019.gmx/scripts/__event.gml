#define __event
/// Event system!

// Autorun and parallel triggers are not needed, since you can do those yourself
// via Create event of any ol' object

enum EventTrigger {
    Action,
    Touch
};

enum Event {
    xtile,
    ytile,
    trigger,
    script,
    sizeof
};

#define __get_event_system
/// __get_event_system()
    return singleton_get(__YUMEngineEventSystem);

#define event_create
/// event_create(tile_xpos, tile_ypos, script_index)
    var tile_xpos = argument0, tile_ypos = argument1, script_index = argument2;
    // Creates an event at a certain 

    var e/*:Event*/ = array_create(Event.sizeof);
    e[@Event.xtile] = tile_xpos;
    e[@Event.ytile] = tile_ypos;
    e[@Event.trigger] = EventTrigger.Action;
    e[@Event.script] = script_index;

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
    
    if script_exists(scr) {
        script_execute(scr);
    }

#define event_get_trigger
/// event_get_trigger(event)
    var event/*:Event*/ = argument0;
    // Gets the trigger type of the event

    return event[@Event.trigger];
