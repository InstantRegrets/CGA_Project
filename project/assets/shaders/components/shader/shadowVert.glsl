#version 330 core
layout (location = 0) in vec3 aPos;
layout(location = 2) in vec3 normal;
layout(location = 3) in vec3 offset;

out vec3 vFragPos;
out vec3 vNormal;


void main()
{
    vFragPos = aPos+offset;
    vNormal = normal;
}
