#version 330 core

layout (triangles) in;
layout (triangle_strip) out;
layout (max_vertices = 9) out;

uniform mat4 lightProjection;
uniform mat4 lightView;
uniform mat4 model_matrix;
uniform float pulseStrength;
uniform float beat;

in vec3 vFragPos[];
in vec3 vNormal[];



void makeVertex(int index){
    vec3 pos = vFragPos[index];
    gl_Position = lightProjection * lightView * model_matrix * vec4(pos, 1.0);
    EmitVertex();
}

vec3 middlePos;
vec3 middleNorm;

void setupMiddle(){
    middlePos = mix(mix(vFragPos[0], vFragPos[1], 0.5), vFragPos[2], 0.5);
    middleNorm = mix(mix(vNormal[0], vNormal[1], 0.5), vNormal[2], 0.5);
}

float pulse(float x){
    float a =x*2-1;
    return pow(a,2);
}
void makeMiddle(){
    vec3 pos = middlePos + middleNorm * (pulseStrength * pulse(mod(beat, 1)));
    gl_Position = lightProjection * lightView * model_matrix * vec4(pos, 1.0);
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