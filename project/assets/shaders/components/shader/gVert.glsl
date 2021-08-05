#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texture;
layout(location = 2) in vec3 normal;

//uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;

// textures
uniform vec2 tcMultiplier;

out vec3 vFragPos;
out vec2 vTexCoords;
out vec3 vNormal;

void main(){
    //worldspace transformations
    //transform our vertexusing the model matrix with a homogeneous coordinate
    vec4 cameraSpace = view_matrix *  model_matrix *vec4(position, 1.0f);
    //normals are weird, that's why we need to transform them using the inverse transposed model matrix
    //also, we don't want to translate them, so the homogeneous coordinate has to be 0
    vec4 norm = transpose(inverse(view_matrix*model_matrix)) * vec4(normal, 0.0f);

    vFragPos = cameraSpace.xyz;
    vNormal = norm.xyz;
    vTexCoords = tcMultiplier * texture; //todo is this actually how this works?
}
