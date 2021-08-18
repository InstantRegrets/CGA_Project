#version 330 core

layout (triangles) in;
layout (triangle_strip) out;
layout (max_vertices = 9) out;

uniform mat4 projection_matrix;
uniform float beat;
uniform float pulseStrength;
uniform float vibeStrength;

in vec3 vFragPos[];
in vec2 vTexCoords[];
in vec3 vNormal[];
in vec4 vFragPosLightSpace[];

out vec3 FragPos;
out vec2 TexCoords;
out vec3 Normal;
out vec4 FragPosLightSpace;

#define pi 3.14159265359

vec3 calcPos(int index){
    vec3 pos = vFragPos[index];
    float distance = length(pos);

    float strength = smoothstep(20,80,distance) * vibeStrength;
    pos.y += strength * sin(distance + mod(beat,2)*2*pi);
    return pos;
}

void makeVertex(int index){
    vec3 pos = calcPos(index);
    FragPos = pos;
    TexCoords = vTexCoords[index];
    Normal = vNormal[index];
    FragPosLightSpace = vFragPosLightSpace[index];
    gl_Position = projection_matrix * vec4(pos,1);

    EmitVertex();
}

vec3 middlePos;
vec3 middleNorm;
vec2 middleTex;
vec4 middleLightPos;

void setupMiddle(){
    middlePos = mix(mix(calcPos(0), calcPos(1), 0.5), calcPos(2), 0.5);
    middleLightPos = mix(mix(vFragPosLightSpace[0], vFragPosLightSpace[1], 0.5), vFragPosLightSpace[2], 0.5);
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
    FragPosLightSpace = middleLightPos;
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

    // makeVertex(0);
    // makeVertex(1);
    // makeVertex(2);
}
