#define curves
/** CURVES
 *
 *    Different math curves
 *
 *
 * /

#define curve_sine
/// curve_sine(t, period, amplitude);
// Sine wave

var t   = argument0,
    per = argument1,
    amp = argument2;

return amp * sin( (t / per) * 2*pi );

#define curve_triangle
/// curve_triangle(t, period, amplitude);
// Triangle wave

var t   = argument0,
    per = argument1,
    amp = argument2;


var modded = mod2(t, (per/2));
var periodmodded = mod2(t, per);

if (periodmodded < per/2) {
    return 4*amp * (modded / per) - amp;
} else {
    return 4*amp * (0.5 - (modded / per)) - amp;
}

#define curve_up_poly
/// curve_up_poly(t, duration, power);
// From t = 0 to t = duration, curve up from 0 o 1 with a polynomial.

var t = argument0,
    d = argument1,
    p = argument2;

return power(t / d, p);

#define curve_down_poly
/// curve_down_poly(t, duration, power);
// From t = 0 to t = duration, curve down from 1 to 0 with a polynomial.

var t = argument0,
    d = argument1,
    p = argument2;

return 1 - curve_up_poly(t, d, 1.0 / p);