#define __event_shortcuts
/// Nice pre-packaged events that do some very common things

#define __event_shortcut_note_run
/// __event_shortcut_note_run()
    var event = event_current;

    var msgs = event_get_data(event);

    for (var i = 0; i < array_length_1d(msgs); ++i) {
        dialogue(msgs[i]);
    }


#define event_shortcut_note
/// event_shortcut_note(messages...);
    // Call inside of an object to create a "note"/observational message event here

    var msgs = array_create(argument_count);

    // Add our messages
    for (var i = 0; i < argument_count; ++i) {
        msgs[i] = argument[i];
    }

    var event = event_create(tile_x, tile_y, __event_shortcut_note_run);
    event_set_trigger(event, trigger_action);
    event_set_data(event, msgs);

    // Event is placed, we just gotta wait for it to be activated.
