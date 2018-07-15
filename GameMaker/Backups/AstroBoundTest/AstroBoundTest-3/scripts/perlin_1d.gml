///perlin_1d(x, amplitude, period, seed)

var xa =        argument0,
    amplitude = argument1,
    period =    argument2,
    seed =      argument3;


var periodIndex = floor(xa / period);
var progress = (xa - (period * periodIndex)) / period
//mod2(xa, period) / period;


random_set_seed(seed);
var randomValue1 = random(2147483647*2) - 2147483647;
var randomValue2 = random(21474836);

random_set_seed(randomValue2*periodIndex + randomValue1);
var leftHeight =  random(2) - 1;
random_set_seed(randomValue2*(periodIndex + 1) + randomValue1);
var rightHeight = random(2) - 1;

// 0 to 1 progress of cosine
var cosineProgress = (1 - cos(pi*progress)) / 2;

// -1 to 1 perlin noise, without applied amplitude
var unampedHeight = leftHeight + (rightHeight - leftHeight) * cosineProgress;

return amplitude * unampedHeight;
