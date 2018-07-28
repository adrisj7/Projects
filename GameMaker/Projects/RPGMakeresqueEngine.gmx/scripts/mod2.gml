/// mod2(val, modder);
// Correct mod2.
// GMS for some reason flips the mod around when you go negative. Doesn't make any sense.

var val = argument0,
    moo = argument1; // Can't call it mod.

val -= floor(val / moo) * moo;

return val;
