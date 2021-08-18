#version 330 core

uniform sampler2D inEmissive;
uniform sampler2D inDiffuse;

vec2 textureCoordinates;
uniform vec2 screenSize;

vec3 emitMaterial, diffMaterial;
void loadMaterial(){
    emitMaterial = texture(inEmissive, textureCoordinates).rgb;
    diffMaterial = texture(inDiffuse, textureCoordinates).rgb;
}
    #define ambientStrength 0.05
    #define ambientColor vec3(0.5, 1, 1)

out vec4 FragColor;
void main() {
    textureCoordinates = gl_FragCoord.xy / screenSize;
    loadMaterial();
    vec3 o = vec3(0);
    o+= emitMaterial.xyz;
    o+= ambientColor * ambientStrength* diffMaterial.xyz;
    FragColor = vec4(o,1);
}
