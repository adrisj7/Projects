/// intpow(x, n);
// Raises x to the power of n, assuming n is a non-negative integer (easy to implement negatives tho)
var v = argument0,
    n = argument1;
var result = 1;
for(var i = 0; i < n; i++) {
    result *= v;
}
return result;
