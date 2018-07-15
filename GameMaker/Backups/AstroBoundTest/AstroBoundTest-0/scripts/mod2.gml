/// @ DEPRICATED @
/// mod2(n, div);
// WARNING: This was ditched



var n = argument0,
    d = argument1;

    
var mod1 = n % d;
if (n >= 0) {
    return mod1; 
}
//print("mod1: ",mod1);
// If we're there, don't add mod1 to d.
if (abs(mod1) == 0) {
    return mod1;
}
return mod1 + d;
