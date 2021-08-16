#version 330 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;

//uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform float normal_offset;

out vec3 vFragPos;

void main(){
    vec4 offsetPosition = vec4(position + normal*normal_offset,1.0f);
    gl_Position = projection_matrix * view_matrix * model_matrix * offsetPosition;
}
