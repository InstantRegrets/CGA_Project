#version 330 core

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec3 gDiffuse;
layout (location = 3) out vec3 gSpecular;
layout (location = 4) out vec3 gEmissive;

in vec3 FragPos;
in vec2 TexCoords;
in vec3 Normal;

uniform sampler2D emitMat;
uniform sampler2D specularMat;
uniform sampler2D diffMat;
uniform vec3 emitColor;
uniform vec2 tcMultiplier;

void main() {
    // store the fragment position vector in the first gbuffer texture
    gPosition = FragPos;
    // also store the per-fragment normals into the gbuffer
    gNormal = normalize(Normal);
    // and the diffuse per-fragment color
    gDiffuse.rgb = texture(diffMat, TexCoords).rgb;
    // and the specular
    gSpecular = texture(specularMat, TexCoords).rgb;
    // and the emmisive
    gEmissive = texture(emitMat, TexCoords).rgb * emitColor;

}
