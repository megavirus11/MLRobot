#version 120
varying vec3 N;
varying vec3 P;

void light( gl_LightSourceParameters light, gl_MaterialParameters material, vec3 position, vec3 norm, out vec3 ambient, out vec3 diffuse, out vec3 spec )
{
	vec3 N = normalize( norm ); //Calculate normal
	vec3 VecTowardsLightSource = normalize( light.position.xyz); // vector towards light source
	vec3 V = normalize( -position.xyz ); // direction towards viewer
	vec3 E = reflect( -VecTowardsLightSource, N ); // position of camera in View space

	ambient = light.ambient.xyz * material.ambient.xyz;
//Ambient light is equal in all forms.
//No need to calculate anything cause ambient light is the same everywhere

	float LDotN = max( dot( VecTowardsLightSource, N ), 0.0 );
//Dot product of the vectors towards light source with the normal from the view point. Dont let this be a negative value;
	diffuse = light.diffuse.xyz * material.diffuse.xyz * LDotN;
 //Calculate diffuse lighting using the diffuse values and the dot product

	spec = light.specular.xyz * material.specular.xyz * pow( max( dot(E,V) , 0.0 ), material.shininess );
//formula for the specular light using the light material and shininess
}

void main()
{
	vec3 ambientSum = vec3(0); //Black if no calculations performed
	vec3 diffuseSum = vec3(0); //Black if no calculations performed
	vec3 specSum = vec3(0); //Black if no calculations performed
	vec3 ambient, diffuse, spec; //Black if no calculations performed
	for( int i=0; i<1; ++i ) //Light numbers are hardcoded for now, sloppy, sorry!
	{
		light(gl_LightSource[i], gl_FrontMaterial, P, N, ambient, diffuse, spec );
		ambientSum += ambient;
		diffuseSum += diffuse;
		specSum += spec;
	}
	//ambientSum /= 1; //Change this to a dynamic light enabled value
	//vec4 texColor = texture(Tex, data.TexCoord);

	gl_FragColor = gl_Color * (vec4( ambientSum + diffuseSum, 1 ) + vec4( specSum, 1 ))*2;

}
