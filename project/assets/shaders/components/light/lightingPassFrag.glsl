#version 330 core

uniform sampler2D inPosition; //Frag Pos
uniform sampler2D inNormal;
uniform sampler2D inDiffuse;
uniform sampler2D inSpecular;
uniform sampler2D inShadow;

uniform sampler2D shadowMap;

vec2 textureCoordinates;

uniform vec2 screenSize;



struct PointLightData{
    vec3 color;
    vec3 lightPos;
    vec3 attenuation;//Values are constant, linear, quadratic
};

struct SpotLightData{
    vec3 color;
    vec3 lightPos;
    vec3 direction;
    float innerCone;
    float outerCone;
    vec3 attenuation;//Values are constant, linear, quadratic
};

#define ambientStrength 0.1
#define ambientColor vec3(0.5, 1, 1)

uniform PointLightData plData;


vec3 diffMaterial, specularMaterial, emitMaterial;
vec4 fragPosLightSpace;
uniform float shininess;

void loadMaterial(){
    //emitMaterial = texture(inEmissive, textureCoordinates).rgb;
    diffMaterial = texture(inDiffuse, textureCoordinates).rgb;
    specularMaterial = texture(inSpecular, textureCoordinates).rgb;
    fragPosLightSpace = texture(inShadow, textureCoordinates);
}

// Lighting functions
vec3 diffuse(in vec3 N, in vec3 toLight, in vec3 color){
    float diffAngle = max(dot(N, toLight), 0.0);
    vec3 diffuse =  color * diffAngle * diffMaterial.xyz;
    return diffuse;
}

// Blinn-Phong shading model https://learnopengl.com/Advanced-Lighting/Advanced-Lighting
vec3 specularBlinn(in vec3 N, in vec3 toLight, in vec3 toCamera, in vec3 color){
    vec3 halfWaydir = normalize(toLight + toCamera);
    float spec = pow(max(dot(N, halfWaydir), 0.0), shininess);
    vec3 specular = color * spec * specularMaterial.xyz;
    return specular;
}

float calcAttenuation(in vec3 inAtten,in float distance){
    float att = 1/(inAtten.x + inAtten.y*distance + inAtten.z *(distance * distance));
    return clamp(att,0,1); // make sure we don't go above 1
}

void pointLight(in vec3 pToCamera, in vec3 N, in vec3 fragPos, inout vec3 o){
    vec3 toLight = normalize(plData.lightPos - fragPos);
    vec3 toCamera = normalize(pToCamera);

    float distance = length(plData.lightPos - fragPos);
    float attenuation = calcAttenuation(plData.attenuation, distance);
    o += diffuse(N, toLight, plData.color)*attenuation;
    o += specularBlinn(N, toLight, toCamera, plData.color)*attenuation;
}

out vec4 FragColor;

void main(){
    textureCoordinates = gl_FragCoord.xy / screenSize;
    loadMaterial();
    vec3 fragPos = texture(inPosition, textureCoordinates).xyz;
    vec3 toCamera = -fragPos.xyz;
    vec3 N = normalize(texture(inNormal, textureCoordinates).xyz);

    vec3 o = vec3(0, 0, 0);// output color that get's passed through all the functions

    pointLight(toCamera, N, fragPos, o);

    FragColor = vec4(o,1);
}

