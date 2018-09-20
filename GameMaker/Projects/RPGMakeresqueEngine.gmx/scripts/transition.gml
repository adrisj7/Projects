#define transition
/** transition
 *
 *    Handles transitions (mainly between opening and closing something)
 *
 */

#define transition_create
/// transition_create(openDuration, closeDuration);

var openDuration  = argument0,
    closeDuration = argument1;

var t = ds_map_create();
t[? "counter"] = 0;
//t[? "active"] = true;
t[? "openDuration"] = openDuration;
t[? "closeDuration"] = closeDuration;
t[? "state"] = TRANSITION_STATE.CLOSED;

return t;

#define transition_destroy
/// transition_destroy(transition);
var t = argument0;

ds_map_destroy(t);

#define transition_update
/// transition_update(transition);
var t = argument0;

// update counter
switch t[? "state"] {
    case TRANSITION_STATE.OPENING:
        t[? "counter"]++;
        if transition_get_progress(t) >= 1 {
            t[? "state"] = TRANSITION_STATE.OPEN;
        }
        break;
    case TRANSITION_STATE.CLOSING:
        t[? "counter"]++;
        if transition_get_progress(t) >= 1 {
            t[? "state"] = TRANSITION_STATE.CLOSED;
        }
        break;
}


#define transition_open
/// transition_open(transition);
var t = argument0;

t[? "state"] = TRANSITION_STATE.OPENING;
t[? "counter"] = 0;

#define transition_open_instant
/// transition_open_instant(transition);
// Instantly opens a transition (skipping the opening transition)
var t = argument0;

t[? "state"] = TRANSITION_STATE.OPEN;

#define transition_close
/// transition_close(transition);
var t = argument0;

t[? "state"] = TRANSITION_STATE.CLOSING;
t[? "counter"] = 0;

#define transition_get_progress
/// transition_get_progress(transition);
var t = argument0;

switch t[? "state"] {
    case TRANSITION_STATE.OPENING:
        if t[? "openDuration"] == 0
            return 1;
        return t[? "counter"] / t[? "openDuration"];
    case TRANSITION_STATE.OPEN:
        return 1;
    case TRANSITION_STATE.CLOSING:
        if t[? "closeDuration"] == 0
            return 1;
        return t[? "counter"] / t[? "closeDuration"];
    case TRANSITION_STATE.CLOSED:
        return 1;
}
return 0;

#define transition_get_progress_updown
/// transition_get_progress_updown(transition);
// Progress that goes up (0-1 while opening), then down (1-0 while closing).

var t = argument0;

var progress = transition_get_progress(t);

if transition_is_closing(t) || transition_is_closed(t) {
    return 1 - progress;
}

return progress;

#define transition_get_state
/// transition_get_state(transition);
return argument0[? "state"];

#define transition_set_duration
/// transition_set_duration(transition, openDuration, closeDuration);

var t             = argument0,
    openDuration  = argument1,
    closeDuration = argument2;

t[? "openDuration"] = openDuration;
t[? "closeDuration"] = closeDuration;

#define transition_is_open
/// transition_is_open(t);
return argument0[? "state"] == TRANSITION_STATE.OPEN;

#define transition_is_closed
/// transition_is_closed(t);
return argument0[? "state"] == TRANSITION_STATE.CLOSED;

#define transition_is_opening
/// transition_is_opening(t);
return argument0[? "state"] == TRANSITION_STATE.OPENING;

#define transition_is_closing
/// transition_is_closing(t);
return argument0[? "state"] == TRANSITION_STATE.CLOSING;