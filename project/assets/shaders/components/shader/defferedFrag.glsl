#version 330
out vec4 color;


uniform sampler2D gPosition, gNormal, gAlbedoSpec, gEmissive;

in vec4 testPos;
in vec2 textureCoordinates;

uniform int lightMode;

struct PointLightData{
    vec3 color;
    vec3 toLight;
    vec3 attenuation;//Values are constant, linear, quadratic
};

struct SpotLightData{
    vec3 color;
    vec3 toLight;
    vec3 direction;
    float innerCone;
    float outerCone;
    vec3 attenuation;//Values are constant, linear, quadratic
};

#define ambientStrength 0.1
#define ambientColor vec3(0.5, 1, 1)

#define NR_POINT_LIGHTS 8
uniform PointLightData plData[NR_POINT_LIGHTS];

#define NR_SPOT_LIGHTS 1
uniform SpotLightData slData[NR_SPOT_LIGHTS];


vec4 diffMaterial, specularMaterial, emitMaterial;
// Material todo do those 2 need to be here?
uniform float shininess;
uniform vec3 emitColor;

// todo don't do this vec4 it's horrendous
void loadMaterial(){
    emitMaterial = texture(gEmissive, textureCoordinates);
    diffMaterial = vec4(texture(gAlbedoSpec, textureCoordinates).rgb,0);
    specularMaterial = vec4(texture(gAlbedoSpec, textureCoordinates).a);
}

// Lightning functions
vec3 diffuse(in vec3 N, in vec3 toLight, in vec3 color){
    float diffAngle = max(dot(N, toLight), 0.0);
    vec3 diffuse =  color * diffAngle * diffMaterial.xyz;
    return diffuse;
}

vec3 specularPhong(in vec3 N, in vec3 toLight, in vec3 fragPos, in vec3 color){
    vec3 reflectDir = reflect(-toLight, N);
    float specAngle = pow(max(dot(fragPos, reflectDir), 0.0), shininess);
    vec3 specular = color * specAngle * specularMaterial.xyz;
    return specular;
}

// Blinn-Phong shading model https://learnopengl.com/Advanced-Lighting/Advanced-Lighting
vec3 specularBlinn(in vec3 N, in vec3 toLight, in vec3 fragPos, in vec3 color){
    vec3 halfWaydir = normalize(toLight + fragPos);
    float spec = pow(max(dot(N, halfWaydir), 0.0), shininess);
    vec3 specular = color * spec;
    return specular;
}

vec3 specular(in vec3 N, in vec3 toLight, in vec3 fragPos, in vec3 color){
    if(lightMode == 0){
        return specularPhong(N, toLight, fragPos, color);
    } else if (lightMode == 1){
        return specularBlinn(N, toLight, fragPos, color);
    } else {
        return vec3(0,0,0);
    }
}

//
// POINT LIGHTS
//
float calcAttenuation(in vec3 inAtten,in float distance){
    return 1.0/ (inAtten.x + inAtten.y*distance + inAtten.z *(distance * distance));
}

void pointLight(in vec3 fragPos, in vec3 N, in PointLightData lightData, inout vec3 o){
    vec3 toLight = normalize(lightData.toLight);
    vec3 toCamera = normalize(fragPos);

    float distance = length(lightData.toLight - fragPos);
    float attenuation = calcAttenuation(lightData.attenuation, distance);
    o += diffuse(N, toLight, lightData.color)*attenuation;
    o += specular(N, toLight, toCamera, lightData.color)*attenuation;
}

void pointLights(in vec3 fragPos, in vec3 N, inout vec3 o){
    for (int i = 0; i < NR_POINT_LIGHTS; i++){
        pointLight(fragPos, N, plData[i], o);
    }
}

void spotLight(in vec3 fragPos, in vec3 N, in SpotLightData lightData, inout vec3 o){
    vec3 toLight = normalize(lightData.toLight);
    vec3 toCamera = normalize(fragPos);
    vec3 dir = normalize(lightData.direction);
    float theta = dot(toLight, - dir);
    float distance = length(lightData.toLight - fragPos);
    float attenuation = calcAttenuation(lightData.attenuation, distance);

    if (theta > lightData.innerCone){ // when fragment lies in the inner cone
        vec3 diff = diffuse(N, toLight, lightData.color)*attenuation;
        vec3 spec = specular(N, toLight, toCamera, lightData.color)*attenuation;
        o += diff + spec;// todo can this be lifted out?
    } else if (theta > lightData.outerCone){ // when fragment lies between the inner and the outer cone
        float epsilon = lightData.innerCone- lightData.outerCone;
        float intensity = clamp((theta - lightData.outerCone) / epsilon, 0.0, 1.0);
        vec3 diff = intensity * diffuse(N, toLight, lightData.color)*attenuation;
        vec3 spec = intensity * specular(N, toLight, toCamera, lightData.color)*attenuation;
        o += diff + spec;
    }
}

void spotLights(in vec3 N, inout vec3 o){
    for (int i = 0; i < NR_SPOT_LIGHTS; i++){
        spotLight(slStaticData[i], slDynamicData[i], N, o);
    }
}

void emit(inout vec3 o){
    o += emitMaterial.xyz*emitColor;
}

void ambient(inout vec3 o){
    o += ambientStrength * ambientColor * diffMaterial.xyz;
}

void main(){
    loadMaterial();
    vec3 N = normalize(texture(gNormal, textureCoordinates).xyz);
    vec3 fragPosition = normalize(texture(gPosition, textureCoordinates).xyz);

    vec3 o = vec3(0, 0, 0);// output color that get's passed through all the functions

    ambient(o);
    pointLights(fragPos, N, o);
    spotLights(fragPos, N, o);
    emit(o);

    color = vec4(o, 1);// output as vec4
}