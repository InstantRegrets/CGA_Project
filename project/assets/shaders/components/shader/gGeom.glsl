#version 430 core

layout (triangles) in;
layout (triangle_strip) out;
layout (max_vertices = 9) out;

uniform mat4 projection_matrix;
uniform float beat;
uniform float pulseStrength;

in vec3 vFragPos[];
in vec2 vTexCoords[];
in vec3 vNormal[];

out vec3 FragPos;
out vec2 TexCoords;
out vec3 Normal;

void makeVertex(int index){
    vec3 pos = vFragPos[index];
    FragPos = pos;
    TexCoords = vTexCoords[index];
    Normal = vNormal[index];
    gl_Position = projection_matrix * vec4(pos,1);

    EmitVertex();
}

vec3 middlePos;
vec3 middleNorm;
vec2 middleTex;

void setupMiddle(){
    middlePos = mix(mix(vFragPos[0], vFragPos[1], 0.5), vFragPos[2], 0.5);
    middleNorm = mix(mix(vNormal[0], vNormal[1], 0.5), vNormal[2], 0.5);
    middleTex = mix(mix(vTexCoords[0], vTexCoords[1], 0.5), vTexCoords[2], 0.5);
}

float pulse(float x){
    float a =x*2-1;
    return pow(a,2);
}
void makeMiddle(){
    vec3 pos = middlePos + middleNorm * (pulseStrength * pulse(mod(beat, 1)));
    FragPos = pos;
    Normal = middleNorm;
    TexCoords = middleTex;
    gl_Position = projection_matrix * vec4(pos,1);
    EmitVertex();
}


void makeTriangle(int index1, int index2){
    makeVertex(index1);
    makeVertex(index2);
    makeMiddle();

    EndPrimitive();
}


void main(){
    setupMiddle();
    makeTriangle(0,1);
    makeTriangle(1,2);
    makeTriangle(2,0);
}