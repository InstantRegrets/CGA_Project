#version 330 core

layout (triangles) in;
layout (triangle_strip) out;
layout (max_vertices = 18) out;

uniform mat4 LightProjectionViewMatrix[6];
uniform float pulseStrength;
uniform float beat;

in vec4 vFragPos[];
in vec3 vNormal[];

out vec4 FragPos;

void main(){
        for(int face = 0; face <6; ++face){
            gl_Layer = face;// build in cubemap
            for (int index = 0; index < 3; ++index){
                vec4 pos = gl_in[index].gl_Position;
                FragPos = pos;
                gl_Position = LightProjectionViewMatrix[face] * pos;
                EmitVertex();
            }
            EndPrimitive();
    }
}