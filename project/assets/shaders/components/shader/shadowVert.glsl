#version 330 core
layout (location = 0) in vec3 aPos;

uniform mat4 lightProjection;
uniform mat4 lightView;
uniform mat4 model_matrix;
uniform float pulseStrength;

void main()
{
    gl_Position = lightProjection * lightView * model_matrix * vec4(aPos, 1.0);
}