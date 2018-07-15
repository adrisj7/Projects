///perlin_1d_octaves(xx, amplitudeStart, periodStart, amplitudeScale, periodScale, octaves, seedStart);

// Octaves 1d perlin noise

var xa =             argument0,
    amplitudeStart = argument1,
    periodStart =    argument2,
    amplitudeScale = argument3,
    periodScale =    argument4,
    octaves =        argument5,
    seedStart =      argument6;

var result = 0;
var amplitude = amplitudeStart;
var period = periodStart;
var seed = seedStart;
for(var octave = 0; octave < octaves; octave++) {

    result += perlin_1d(xa,amplitude,period,seed);

    amplitude *= amplitudeScale;
    period *= periodScale;

    random_set_seed(seed);
    seed = floor(random(2147483647));
}

return result;
