/// get_gui_scratchpad_surface(index);
// Returns a surface that we can draw to without making a new one

var index = argument0;

if !surface_exists(SURFACE_WINDOW[index])
    SURFACE_WINDOW[index] = surface_create(1024, 1024);

return SURFACE_WINDOW[index];
