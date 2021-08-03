#version 330 core

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gEmissive;

in vec3 FragPos;
in vec2 TexCoords;
in vec3 Normal;

uniform sampler2D emitMat;
uniform sampler2D specularMat;
uniform sampler2D diffMat;
uniform vec2 tcMultiplier;

void main() {
    // store the fragment position vector in the first gbuffer texture
    gPosition = FragPos;
    // also store the per-fragment normals into the gbuffer
    gNormal = normalize(Normal);
    // and the diffuse per-fragment color
    gAlbedoSpec.rgb = texture(diffMat, TexCoords*tcMultiplier).rgb;
    // store specular intensity in gAlbedoSpec's alpha component
    gAlbedoSpec.a = texture(specularMat, TexCoords*tcMultiplier).r; //todo maybe its .a
    //trying to store emissive as well
    gEmissive = texture(emitMat, TexCoords*tcMultiplier);

}
