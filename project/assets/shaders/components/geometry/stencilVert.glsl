#version 330 core

layout (location = 0) in vec3 aPos;

//world view projection
uniform mat4 wvp;

void main()
{
    gl_Position = wvp * vec4(aPos, 1.0);
}
