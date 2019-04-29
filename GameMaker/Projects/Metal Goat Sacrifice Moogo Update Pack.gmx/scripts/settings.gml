#define settings

#define _settings_get
/// _settings_get()

// First ensure it exists
if !instance_exists(objSettings) {
    instance_create(0, 0, objSettings);
}
return objSettings;

#define save_settings
/// save_settings()

var s = _settings_get();

var file = ini_open("settings.ini");

ini_write_real("Controls", "control_mode_wasd", s._control_mode_wasd);
ini_write_real("Sound", "music_volume", s._music_volume);
ini_write_real("Sound", "sfx_volume", s._sfx_volume);

ini_close();

#define load_settings
/// load_settings

var s = _settings_get();

// If our settings file doesn't exist, do nothing
if !file_exists("settings.ini") {
    return 0;
}

ini_open("settings.ini");

s._control_mode_wasd = ini_read_real("Controls", "control_mode_wasd", 1);
s._music_volume = ini_read_real("Sound", "music_volume", 1);
s._sfx_volume = ini_read_real("Sound", "sfx_volume", 1);


#define settings_get_control_mode_wasd
/// settings_get_control_mode_wasd();
return _settings_get()._control_mode_wasd;

#define settings_get_music_volume
/// settings_get_music_volume()
return _settings_get()._music_volume;

#define settings_get_sfx_volume
/// settings_get_sfx_volume()
return _settings_get()._sfx_volume;

#define settings_toggle_control_mode_wasd
/// settings_toggle_control_mode_wasd();
var s = _settings_get();
s._control_mode_wasd = !s._control_mode_wasd;

#define settings_change_music_volume
/// settings_change_music_volume(delta);
var s = _settings_get();
s._music_volume += argument0;
s._music_volume = clamp(s._music_volume, 0, 1);

#define settings_change_sfx_volume
/// settings_change_sfx_volume(delta);
var s = _settings_get();
s._sfx_volume += argument0;
s._sfx_volume = clamp(s._sfx_volume, 0, 1);