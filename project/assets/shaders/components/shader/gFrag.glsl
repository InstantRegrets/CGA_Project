#version 330 core

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gEmissive;

in struct VertexData
{
    vec3 fragPos;
    vec3 normal;
    vec2 textureCoordinates;

} vertexData;


uniform sampler2D emitMat;
uniform sampler2D specularMat;
uniform sampler2D diffMat;


void main() {
    // store the fragment position vector in the first gbuffer texture
    gPosition = vertexData.fragPos;
    // also store the per-fragment normals into the gbuffer
    gNormal = normalize(vertexData.normal);
    // and the diffuse per-fragment color
    gAlbedoSpec.rgb = texture(diffMat, vertexData.textureCoordinates).rgb;
    // store specular intensity in gAlbedoSpec's alpha component
    gAlbedoSpec.a = texture(specularMat, vertexData.textureCoordinates).r;

    gEmissive = texture(emitMat, vertexData.textureCoordinates);

}
