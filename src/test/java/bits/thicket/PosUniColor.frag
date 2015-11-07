#version 330

uniform vec4 COLOR;

out vec4 fragColor;

void main() {
	fragColor = COLOR;
	if( fragColor.a <= 0.0 ) {
		discard;
	}
}

