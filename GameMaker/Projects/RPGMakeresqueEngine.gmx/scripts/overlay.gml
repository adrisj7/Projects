#define overlay
/***
 * A system for fading the screen and adding blur to it
*/

#define overlay_set_darkness
/// overlay_set_darkness(alpha);
var alpha = argument0;

OVERLAY_SYSTEM._alphaTarget = alpha;

#define overlay_set_blur
/// overlay_set_blur(blur);

var blur = argument0;

OVERLAY_SYSTEM._blurTarget = blur;

#define overlay_create
/// overlay_create();
return instance_create(0, 0, objScreenOverlayEffector);