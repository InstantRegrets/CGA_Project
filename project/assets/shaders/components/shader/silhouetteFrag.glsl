#version 330

uniform vec3 outlineColor;
out vec4 color;

void main(){
    color = vec4(outlineColor, 1);
}
