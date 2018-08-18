/// editor_apply_view_transformation();

d3d_transform_set_translation(-view_xview - view_wview/2, -view_yview - view_hview/2, 0);
var scale = 1 / objDialogueEditor._cameraZoom;
d3d_transform_add_scaling(scale, scale, 1);
d3d_transform_add_translation(view_wview/2, view_hview/2, 0);
