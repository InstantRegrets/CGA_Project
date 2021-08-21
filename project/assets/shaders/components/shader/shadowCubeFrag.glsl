#version 330 core

in vec4 FragPos;

uniform float farPlane;
uniform vec3 lightPos;


void main() {
    // here we need calc our distance manual, since we don't use an orthogonal proj
    // get distance between fragment and light source
    float lightDistance = length(FragPos.xyz - lightPos);
    lightDistance = lightDistance / farPlane;
    gl_FragDepth = lightDistance;
    // if(FragPos.y<0){
    //     gl_FragDepth = 1;
    // }else{
    //     gl_FragDepth = 0;
    // }
}
