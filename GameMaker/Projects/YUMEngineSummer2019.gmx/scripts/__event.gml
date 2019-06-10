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
    // Creates an event at a certain 
    var tile_xpos = argument0, tile_ypos = argument1, script_index = argument2;

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
    tilemap_set_tile(e_grid, tile_xpos, tile_ypos, e);

    return e;

#define event_get
/// event_get(tile_xpos, tile_ypos)
    // Gets the event that's at that tile, or returns noone if no event exists there
    var tile_xpos = argument0, tile_ypos = argument1;

    var tmap = __get_tilemap_event();
    return tilemap_get_tile(tmap, tile_xpos, tile_ypos);
