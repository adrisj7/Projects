/// editor_is_button_pressed(button);

var button = argument0;

if ! instance_exists(button) return false;

return button._pressed;
