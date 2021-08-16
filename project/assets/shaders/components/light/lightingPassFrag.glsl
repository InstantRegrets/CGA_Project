#version 330


uniform sampler2D inPosition; //Frag Pos
uniform sampler2D inNormal;
uniform sampler2D inDiffuse;
uniform sampler2D inSpecular;
uniform sampler2D inEmissive;

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

#define plMaxAmount 128
uniform int plAmount;
uniform PointLightData plData[plMaxAmount]; // max 128 lights

#define slMaxAmount 20
uniform int slAmount;
uniform SpotLightData slData[slMaxAmount];  // max 128 lights


vec3 diffMaterial, specularMaterial, emitMaterial;
uniform float shininess;

void loadMaterial(){
    emitMaterial = texture(inEmissive, textureCoordinates).rgb;
    diffMaterial = texture(inDiffuse, textureCoordinates).rgb;
    specularMaterial = texture(inSpecular, textureCoordinates).rgb;
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

//
// POINT LIGHTS
//
float calcAttenuation(in vec3 inAtten,in float distance){
    return 1.0/ (inAtten.x + inAtten.y*distance + inAtten.z *(distance * distance));
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
    o += diff + spec;
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

out vec4 FragColor;

void main(){
    textureCoordinates = gl_FragCoord.xy / screenSize;
    loadMaterial();
    vec3 fragPos = texture(inPosition, textureCoordinates).xyz;
    vec3 toCamera = -fragPos.xyz;
    vec3 N = normalize(texture(inNormal, textureCoordinates).xyz);

    vec3 o = vec3(0, 0, 0);// output color that get's passed through all the functions

    //ambient(o);
    pointLights(toCamera, N, fragPos, o);
    spotLights(toCamera, N, fragPos, o);
    emit(o);

    FragColor = vec4(o,1);
}


