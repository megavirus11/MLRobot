// simple vertex shader


#version 120
varying vec3 N;
varying vec3 P;
void main()
{
	N = normalize(gl_NormalMatrix * gl_Normal); //Normal Eye space Normal
	P = vec3(gl_ModelViewMatrix * gl_Vertex); // Position eye space position
	gl_Position    = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_FrontColor  = gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}