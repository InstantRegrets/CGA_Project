#version 330
out vec4 color;


uniform sampler2D inPosition; //Frag Pos
uniform sampler2D inNormal;
uniform sampler2D inDiffuse;
uniform sampler2D inSpecular;
uniform sampler2D inEmissive;
uniform sampler2D inShadow;

uniform sampler2D shadowMap;

in vec2 textureCoordinates;



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

#define plMaxAmount 128
uniform int plAmount;
uniform PointLightData plData[plMaxAmount]; // max 128 lights

#define slMaxAmount 20
uniform int slAmount;
uniform SpotLightData slData[slMaxAmount];  // max 128 lights


vec3 diffMaterial, specularMaterial, emitMaterial;
vec4 fragPosLightSpace;
uniform float shininess;

void loadMaterial(){
    emitMaterial = texture(inEmissive, textureCoordinates).rgb;
    diffMaterial = texture(inDiffuse, textureCoordinates).rgb;
    specularMaterial = texture(inSpecular, textureCoordinates).rgb;
    fragPosLightSpace = texture(inShadow, textureCoordinates);
}


float ShadowCalculation(vec3 normal, vec3 lightDir) {
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    float bias = min(0.05 * (1.0 - dot(normal, normalize(lightDir))), 0.005);

    // output value
    float shadow = 0.0;

    // check whether current frag pos is in shadow
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += (currentDepth - bias) > pcfDepth ? 1.0 : 0.0;
        }
    }

    shadow /= 9.0; // nine, since we loop through 9 pixels
    return shadow;
}


// Lightning functions
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

void pointLight(in vec3 pToCamera, in vec3 N, in PointLightData ld,in vec3 fragPos, inout vec3 o){
    vec3 toLight = normalize(ld.lightPos - fragPos);
    vec3 toCamera = normalize(pToCamera);

    float distance = length(ld.lightPos - fragPos);
    float attenuation = calcAttenuation(ld.attenuation, distance);
    o += diffuse(N, toLight, ld.color)*attenuation;
    o += specularBlinn(N, toLight, toCamera, ld.color)*attenuation;
}

void pointLights(in vec3 toCamera, in vec3 N, in vec3 fragPos, inout vec3 o){
    int amount = min(plAmount, plMaxAmount);
    for (int i = 0; i < amount; i++){
        pointLight(toCamera, N, plData[i],fragPos, o);
    }
}

void spotLight(in vec3 pToCamera, in vec3 N, in SpotLightData ld,in vec3 fragPos , inout vec3 o){
    vec3 toLight = normalize(ld.lightPos - fragPos);
    vec3 toCamera = normalize(pToCamera);
    vec3 dir = normalize(ld.direction);
    float theta = dot(toLight, - dir);
    float distance = length(ld.lightPos - fragPos);
    float attenuation = calcAttenuation(ld.attenuation, distance);

    float epsilon = ld.innerCone- ld.outerCone;
    float intensity = clamp((theta - ld.outerCone) / epsilon, 0.0, 1.0);
    vec3 diff = intensity * diffuse(N, toLight, ld.color)*attenuation;
    vec3 spec = intensity * specularBlinn(N, toLight, toCamera, ld.color)*attenuation;
    o += (diff + spec) * (1.0-ShadowCalculation(N, ld.direction));
}

void spotLights(in vec3 toCamera, in vec3 N,in vec3 fragPos, inout vec3 o){
    int amount = min(slAmount, plMaxAmount);
    for (int i = 0; i < amount; i++){
        spotLight(toCamera, N, slData[i],fragPos , o);
    }
}

void emit(inout vec3 o){
    o += emitMaterial.xyz; // *emitColor;
}

void ambient(inout vec3 o){
    o += ambientStrength * ambientColor * diffMaterial.xyz;
}

void main(){
    loadMaterial();
    vec3 fragPos = texture(inPosition, textureCoordinates).xyz;
    vec3 toCamera = -fragPos.xyz;
    vec3 N = normalize(texture(inNormal, textureCoordinates).xyz);

    vec3 o = vec3(0, 0, 0);// output color that get's passed through all the functions

    ambient(o);
    pointLights(toCamera, N,fragPos, o);
    spotLights(toCamera, N,fragPos, o);
    emit(o);

    color = vec4(o,1);
}

