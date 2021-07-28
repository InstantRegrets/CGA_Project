#version 330 core

out vec4 color;

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 textureCoordinates;

} vertexData;

uniform int lightMode;

struct LightData
{
    vec3 toLight;
    vec3 toCamera;
};

struct PointLightStaticData{
    vec3 color;
    vec3 attenuation;//Values are constant, linear, quadratic
};

struct SpotLightStaticData{
    vec3 color;
    vec3 direction;
    float innerCone;
    float outerCone;
    vec3 attenuation;//Values are constant, linear, quadratic
};

#define NR_POINT_LIGHTS 8
uniform PointLightStaticData plStaticData[NR_POINT_LIGHTS];
in LightData plDynamicData[NR_POINT_LIGHTS];

#define NR_SPOT_LIGHTS 1
uniform SpotLightStaticData slStaticData[NR_SPOT_LIGHTS];
in LightData slDynamicData[NR_SPOT_LIGHTS];

uniform vec3 ambLight;
uniform float ambientStrength;

// Material
uniform float shininess;
uniform sampler2D emitMat, diffMat, specularMat;
uniform vec3 emitColor;
vec4 diffMaterial, specularMaterial, emitMaterial;

void loadMaterial(){
    emitMaterial = texture(emitMat, vertexData.textureCoordinates);
    diffMaterial = texture(diffMat, vertexData.textureCoordinates);
    specularMaterial = texture(specularMat, vertexData.textureCoordinates);
}

// Lightning functions
vec3 diffuse(in vec3 N, in vec3 toLight, in vec3 color){
    float diffAngle = max(dot(N, toLight), 0.0);
    vec3 diffuse =  color * diffAngle * diffMaterial.xyz;
    return diffuse;
}

vec3 specularPhong(in vec3 N, in vec3 toLight, in vec3 toCamera, in vec3 color){
    vec3 reflectDir = reflect(-toLight, N);
    float specAngle = pow(max(dot(toCamera, reflectDir), 0.0), shininess);
    vec3 specular = color * specAngle * specularMaterial.xyz;
    return specular;
}

// Blinn-Phong shading model https://learnopengl.com/Advanced-Lighting/Advanced-Lighting
vec3 specularBlinn(in vec3 N, in vec3 toLight, in vec3 toCamera, in vec3 color){
    vec3 halfWaydir = normalize(toLight + toCamera);
    float spec = pow(max(dot(N, halfWaydir), 0.0), shininess);
    vec3 specular = color * spec;
    return specular;
}

vec3 specular(in vec3 N, in vec3 toLight, in vec3 toCamera, in vec3 color){
    if(lightMode == 0){
        return specularPhong(N, toLight, toCamera, color);
    } else if (lightMode == 1){
        return specularBlinn(N, toLight, toCamera, color);
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

void pointLight(in PointLightStaticData sd, in LightData ld, in vec3 N, inout vec3 o){
    vec3 toLight = normalize(ld.toLight);
    vec3 toCamera = normalize(ld.toCamera);

    float distance = length(ld.toLight - ld.toCamera);
    float attenuation = calcAttenuation(sd.attenuation, distance);
    o += diffuse(N, toLight, sd.color)*attenuation;
    o += specular(N, toLight, toCamera, sd.color)*attenuation;
}

void pointLights(in vec3 N, inout vec3 o){
    for (int i = 0; i < NR_POINT_LIGHTS; i++){
        pointLight(plStaticData[i], plDynamicData[i], N, o);
    }
}

void spotLight(in SpotLightStaticData sd, in LightData ld, in vec3 N, inout vec3 o){
    vec3 toLight = normalize(ld.toLight);
    vec3 toCamera = normalize(ld.toCamera);
    vec3 dir = normalize(sd.direction);
    float theta = dot(toLight, - dir);
    float distance = length(ld.toLight - ld.toCamera);
    float attenuation = calcAttenuation(sd.attenuation, distance);

    if (theta > sd.innerCone){ // when fragment lies in the inner cone
        vec3 diff = diffuse(N, toLight, sd.color)*attenuation;
        vec3 spec = specular(N, toLight, toCamera, sd.color)*attenuation;
        o += diff + spec;// todo can this be lifted out?
    } else if (theta > sd.outerCone){ // when fragment lies between the inner and the outer cone
        float epsilon = sd.innerCone- sd.outerCone;
        float intensity = clamp((theta - sd.outerCone) / epsilon, 0.0, 1.0);
        vec3 diff = intensity * diffuse(N, toLight, sd.color)*attenuation;
        vec3 spec = intensity * specular(N, toLight, toCamera, sd.color)*attenuation;
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
    o += ambientStrength * ambLight * diffMaterial.xyz;
}

void main(){
    loadMaterial();
    vec3 N = normalize(vertexData.normal);
    vec3 o = vec3(0, 0, 0);// output color that get's passed through all the functions

    ambient(o);
    pointLights(N, o);
    spotLights(N, o);
    emit(o);

    color = vec4(o, 1);// output as vec4
}