#version 330 core
layout (location = 0) in vec3 aPos;
//layout (location = 1) in vec2 aTexCoords;

//out vec2 textureCoordinates;

uniform mat4 view1;
uniform mat4 proj1;
uniform mat4 model1;

void main()
{
    //textureCoordinates = aTexCoords;
    gl_Position = proj1 * view1 * model1 * vec4(aPos, 1.0);
}
