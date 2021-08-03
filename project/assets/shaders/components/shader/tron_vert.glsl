#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texture;
layout(location = 2) in vec3 normal;

//uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

// textures
uniform vec2 tcMultiplier;

out struct VertexData
{
    vec3 position;
    vec3 normal;
    vec2 textureCoordinates;

} vertexData;


struct LightData{
    vec3 toLight;
    vec3 toCamera;
};

#define NR_POINT_LIGHTS 8
uniform vec3 plPosition[NR_POINT_LIGHTS];
out LightData plDynamicData[NR_POINT_LIGHTS];
#define NR_SPOT_LIGHTS 1
uniform vec3 slPosition[NR_SPOT_LIGHTS];
out LightData slDynamicData[NR_SPOT_LIGHTS];


void main(){
    //worldspace transformations
    //transform our vertexusing the model matrix with a homogeneous coordinate
    vec4 pos = view_matrix *  model_matrix *vec4(position, 1.0f);
    gl_Position = projection_matrix * pos;

    //normals are weird, that's why we need to transform them using the inverse transposed model matrix
    //also, we don't want to translate them, so the homogeneous coordinate has to be 0
    vec4 norm = transpose(inverse(view_matrix*model_matrix)) * vec4(normal, 0.0f);

    vertexData.position = pos.xyz;
    vertexData.normal = norm.xyz;
    vertexData.textureCoordinates = tcMultiplier * texture;

    // Lightning
    for(int i = 0; i < NR_POINT_LIGHTS; i++){
        vec4 lightPos = view_matrix * vec4(plPosition[i], 1.0);
        plDynamicData[i].toLight = (lightPos - pos).xyz;
        plDynamicData[i].toCamera = -pos.xyz; // todo we know this is horrible, but we ignore it for now
    }
    for(int i = 0; i < NR_SPOT_LIGHTS; i++){
        vec4 lightPos = view_matrix * vec4(slPosition[i], 1.0);
        slDynamicData[i].toLight = (lightPos - pos).xyz;
        slDynamicData[i].toCamera = -pos.xyz; // todo we know this is horrible, but we ignore it for now
    }
}
