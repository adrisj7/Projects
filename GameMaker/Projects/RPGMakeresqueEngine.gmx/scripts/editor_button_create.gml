/// editor_button_create(x, y, button_image_index, [key_shortcut], [shortcut_with_control]);

var xa        = argument[0],
    ya        = argument[1],
    img_index = argument[2];
    
var shortcut = -1;
var control = false;
if argument_count > 3 {
    shortcut  = argument[3];
}
if argument_count > 4 {
    control = argument[4];   
}

var button = instance_create(xa, ya, objDialogueEditorButton);
button.image_index = img_index;
button._shortcut = shortcut;
button._shortcutControl = control;

return button;
