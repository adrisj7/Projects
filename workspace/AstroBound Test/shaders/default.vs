#version 330

attribute vec3 vertices;
attribute vec2 textures;

out vec2 tex_coords;

uniform mat4 projection;
uniform mat4 texModifier;

void main() {
	tex_coords = (texModifier * vec4(textures, 0, 1)).xy;
	gl_Position = projection * vec4(vertices, 1);
}