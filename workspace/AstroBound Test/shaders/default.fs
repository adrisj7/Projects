#version 330

uniform sampler2D sampler; // Location for texture

in vec2 tex_coords;

void main() {
	gl_FragColor = texture2D(sampler, tex_coords);
}