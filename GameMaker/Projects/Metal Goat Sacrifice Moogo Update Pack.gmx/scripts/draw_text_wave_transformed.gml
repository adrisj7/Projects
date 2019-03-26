///draw_text_wave_transformed(x, y, text, xscale, yscale, wave_height, wavelength, theta);
var xa      = argument0,
    ya      = argument1,
    text    = argument2,
    xscale  = argument3,
    yscale  = argument4,
    height  = argument5,
    alpha   = argument6,
    theta   = argument7;

var temp = "";
for(var i = 1; i <= string_length(text); ++i) {
    var char = string_char_at(text, i);
    // dist before
    var dist = string_width(temp) * xscale;
    temp += char;
    var dist_plus = dist + theta;
    var h = height * sin(2*pi*(dist_plus / alpha));
    draw_text_transformed(xa + dist,ya + h, char, xscale, yscale, 0);
}
