#version 330 core
layout (location = 0) in vec3 aPos;
layout(location = 3) in vec3 offset;

uniform mat4 model_matrix; // todo move to vertex shader


void main()
{
    gl_Position = model_matrix * vec4(aPos+offset,1);
}
