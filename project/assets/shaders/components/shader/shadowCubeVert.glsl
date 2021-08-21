#version 330 core
layout (location = 0) in vec3 aPos;

uniform mat4 model_matrix; // todo move to vertex shader


void main()
{
    gl_Position = model_matrix * vec4(aPos,1);
}