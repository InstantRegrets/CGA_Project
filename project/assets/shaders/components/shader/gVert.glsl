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

out vec3 ViewPos;

out struct VertexData
{
    vec3 fragPos;
    vec3 normal;
    vec2 textureCoordinates;

} vertexData;

void main(){
    //worldspace transformations
    //transform our vertexusing the model matrix with a homogeneous coordinate
    vec4 pos = view_matrix *  model_matrix *vec4(position, 1.0f);
    gl_Position = projection_matrix * pos;
    ViewPos = -pos.xyz;
    //normals are weird, that's why we need to transform them using the inverse transposed model matrix
    //also, we don't want to translate them, so the homogeneous coordinate has to be 0
    vec4 norm = transpose(inverse(view_matrix*model_matrix)) * vec4(normal, 0.0f);

    vertexData.fragPos = pos.xyz;
    vertexData.normal = norm.xyz;
    vertexData.textureCoordinates = tcMultiplier * texture;


}
