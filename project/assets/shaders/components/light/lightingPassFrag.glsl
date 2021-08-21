#version 330 core

uniform sampler2D inPosition; //Frag Pos
uniform sampler2D inNormal;
uniform sampler2D inDiffuse;
uniform sampler2D inSpecular;
uniform samplerCube shadowMap;

vec2 textureCoordinates;

uniform vec2 screenSize;



struct PointLightData{
    vec3 color;
    vec3 lightPos;
    vec3 attenuation;//Values are constant, linear, quadratic
};


uniform PointLightData plData;


vec3 diffMaterial, specularMaterial;
uniform float shininess;
uniform float farPlane;

void loadMaterial(){
    //emitMaterial = texture(inEmissive, textureCoordinates).rgb;
    diffMaterial = texture(inDiffuse, textureCoordinates).rgb;
    specularMaterial = texture(inSpecular, textureCoordinates).rgb;
}

float ShadowCalculation(vec3 normal, vec3 fragPos) {
    vec3 fragToLight =  fragPos - plData.lightPos; //vec4(fragPos,1)) - (inverse(CameraViewMatrix)* vec4(plData.lightPos,1))).xyz;
    float closestDepth = texture(shadowMap, fragToLight).r;
    closestDepth *= farPlane;
    // get depth of current fragment from light's perspective
    float currentDepth = length(fragToLight);

    // now test for shadows
    float bias = 0.05;
    float shadow = currentDepth -bias > closestDepth ? 1.0 : 0.0;
    return shadow;
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
    vec3 diff = diffuse(N, toLight, plData.color)*attenuation;
    vec3 spec = specularBlinn(N, toLight, toCamera, plData.color)*attenuation;
    o += (diff + spec) * (1.0-ShadowCalculation(N, fragPos));
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

