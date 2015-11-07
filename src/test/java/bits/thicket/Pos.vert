#version 330

uniform mat4 PROJ_VIEW_MAT;

layout( location = 0 ) in vec4 inVert;

smooth out vec4 color;

void main() {
	gl_Position = PROJ_VIEW_MAT * inVert;
}

