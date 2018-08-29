#define editor_get_mouse_x
/// editor_get_mouse_x(static);

var static = argument0;

var mx = device_mouse_x_to_gui(0);

if !static {
    mx -= view_wview/2;    

    var scale = 1;
    if instance_exists(objDialogueEditor) {
        scale = objDialogueEditor._cameraZoom;
    }
    mx *= scale;

    mx += view_xview;

    mx += view_wview/2;    

}


return mx;

#define editor_get_mouse_y
/// editor_get_mouse_y(static);

var static = argument0;

var my = device_mouse_y_to_gui(0);

if !static {
    my -= view_hview/2;    

    var scale = 1;
    if instance_exists(objDialogueEditor) {
        scale = objDialogueEditor._cameraZoom;
    }
    my *= scale;

    my += view_yview;

    my += view_hview/2;    

}


return my;