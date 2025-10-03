// This variable will store the WebGL rendering context
let gl;
//WebGL State Management
////////////////////////
let mvIndex; //Shader Positioning Input
let projIndex; //Shader Projection Input
let mv; //Local Positioning Matrix
let p; //Local Projection Matrix

let colors = {
    'red':     new vec4(1, 0, 0, 1),
    'blue':    new vec4(0, 0, 1, 1),
    'green':   new vec4(0, 1, 0, 1),
    'yellow':  new vec4(1, 1, 0, 1),
    'cyan':    new vec4(0, 1, 1, 1),
    'magenta': new vec4(1, 0, 1, 1),
};

let objectDistance = 0.6;

//Model Control Variables
/////////////////////////
let objectColor = colors['red']; //current color of sphere
let rotAngle = 0; //current rotation angle of scene
let rotChange = -0.5; //speed and direction of scene rotation

window.onload = function init() {
    // Set up a WebGL Rendering Context in an HTML5 Canvas
    let canvas = document.getElementById("gl-canvas");
    gl = canvas.getContext('webgl2');
    if (!gl) alert("WebGL 2.0 isn't available");

//  Configure WebGL
//  eg. - set a clear color
//      - turn on depth testing
    gl.clearColor(1, 1, 1, 1.0);
   // gl.enable(gl.DEPTH_TEST);

//  Load shaders and initialize attribute buffers
    let program = initShaders(gl, "vertex-shader", "fragment-shader");
    gl.useProgram(program);

// Get locations of transformation matrices from shader
    mvIndex = gl.getUniformLocation(program, "mv");
    projIndex = gl.getUniformLocation(program, "p");

// Send a perspective transformation to the shader
    let p = perspective(50.0, canvas.width/canvas.height, 0.5, 50.0);
    gl.uniformMatrix4fv(projIndex, gl.FALSE, flatten4x4(p));

// Get locations of lighting uniforms from shader
    let uLightPosition = gl.getUniformLocation(program, "lightPosition");

// Set default light direction in shader.
    gl.uniform4f(uLightPosition, 0.0, 0.0, 0.0, 0.0);

// Configure uofrGraphics object
    urgl = new uofrGraphics(gl);
    urgl.connectShader(program, "vPosition", "vNormal", "vColor");

// Begin an animation sequence
    render();
    // You can't work with HTML elements until the page is loaded,
// So init is a good place to set up event listeners
    setupEventListeners();
};

function setupEventListeners()
{
    //Request an HTML element
    let m = document.getElementById("colorMenu");

    //Setup a listener - notice we are defining a function from inside a function
    //The textbook recommends click, but I find that change works better
    m.addEventListener("change", function(event) {
        //acquire the menu entry number
        let index = m.selectedIndex;

        //learn the value of the selected option. This need not match the text...
        let colorName = m.options[index].value;

        //change the object color by looking up the value in our associate array
        objectColor = colors[colorName];
    });
    let d = document.getElementById("distanceSlider");
    d.addEventListener("click", function(event) {
        let distanceSelection = d.valueAsNumber;

        objectDistance = distanceSelection;
    });
}
function render() {
    // Clear the canvas with the clear color instead of plain white,
// and also clear the depth buffer
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

// Draw previous model state
// Notice we modularized the work a bit...
    PreRenderScene();
    RenderStockScene();
    RenderScene();

// Update the model and request a new animation frame
    rotAngle += rotChange;
    requestAnimationFrame(render);
}
// Use this to perform view transforms or other tasks
// that will affect both stock scene and detail scene
function PreRenderScene() {
    // select a default viewing transformation
    // of a 20 degree rotation about the X axis
    // then a -5 unit transformation along Z
    mv = mat4();
    mv = mult(mv, translate(0.0, 0.0, -10.0));
    mv = mult(mv, rotate(-90.0, vec3(1, 0, 0)));

    //Allow variable controlled rotation around local y axis.
    // mv = mult(mv, rotate(rotAngle, vec3(0, 1, 0)));
}

// Function: RenderStockScene
// Purpose:
//     Draw a stock scene that looks like a
//     black and white checkerboard
function RenderStockScene() {
    let delta = 0.5;

    // define four vertices that make up a square.
    let v1 = vec4(0.0, 0.0, 0.0, 1.0);
    let v2 = vec4(0.0, 0.0, delta, 1.0);
    let v3 = vec4(delta, 0.0, delta, 1.0);
    let v4 = vec4(delta, 0.0, 0.0, 1.0);


    let color = 0;

    // define the two colors
    let color1 = vec4(0.9, 0.9, 0.9, 1);
    let color2 = vec4(0,0,0, 1);

    //Make a checkerboard
    let placementX = mv;
    let placementZ;
    placementX = mult(placementX, translate(-10.0 * delta, 0.0, -10.0 * delta));
    for (let x = -10; x <= 10; x++)
    {
        placementZ = placementX;
        for (let z = -10; z <= 10; z++)
        {
            urgl.setDrawColour((color++) % 2 ? color2 : color2);
            gl.uniformMatrix4fv(mvIndex, gl.FALSE, flatten4x4(placementZ));
            urgl.drawQuad(v1, v2, v3, v4);
            placementZ = mult(placementZ, translate(0.0, 0.0, delta));
        }
        placementX = mult(placementX, translate(delta, 0.0, 0.0));

    }
}

// Function: RenderScene
// Purpose:
//     Your playground. Code additional scene details here.
function RenderScene() {

    //////

    //Draw a cube "base"
    //    TODO 1: give the cube a side length of .5
    //            and put it exactly on the checkerboard

    // Create the a translation transformation.
    // Translate's 3 arguments are x, y and z.
    // At this point, positive y points up from checkerboard

    //    TODO 1b: adjust the translation so the cube is on top of the checkerboard
    //             HINT: what's half the cube's side length?
    //mv = mult(mv, translate(0.0, -1.49, 1.0));

    //Send the transformation matrix to the shader
    gl.uniformMatrix4fv(mvIndex, gl.FALSE, flatten(mv));
    //
    // // set the drawing color to light blue
    // // arguments to vec4 are red, green, blue and alpha (transparancy)
    urgl.setDrawColour(vec4(0.5, 0.5, 1.0, 1.0));
    //
    // // Draw the cube with urgl.drawSolidCube
    // // Argument "size" refers to the side length of the cube
    // // Cube is centered around current origin
    //
    // //    TODO 1a: change the cube's side length to .5
    for (let i = 0; i < 5; i++) {
        if(i%2 === 0){
            urgl.setDrawColour(vec4(1,1,0, 1.0));
        }else{
            urgl.setDrawColour(vec4(0, 0, 0, 1.0));
        }
        mv = mult(mv, translate(0.0, 1, 1.0));
        gl.uniformMatrix4fv(mvIndex, gl.FALSE, flatten(mv));
        urgl.drawQuad(i);
    }


    // urgl.drawSolidCube(5);
    // urgl.drawSolidCube(4);
    // urgl.drawSolidCube(3);
    // urgl.drawSolidCube(2);
    // urgl.drawSolidCube(1);
    //
    //
    // ///////
    // // Draw a Sphere:
    // //    TODO 2: place sphere exactly on top of the box
    // //            change the sphere to a wire sphere to see the poles
    // //            uncomment the rotate to see the effect
    //
    // // Set the drawing color to the one from the menu
    // urgl.setDrawColour(objectColor);
    //
    // // Move the "drawing space" up by the sphere's radius
    // // so the sphere is on top of the checkerboard
    // // mv is a transformation matrix. It accumulates transformations through
    // // right side matrix multiplication.
    //
    // //    TODO 2a: use the translation to put the sphere on top of the box
    // //             HINT: radius of sphere + half side length of the cube...
    // mv = mult(mv, translate(0.0, 0.75, 0.0));
    //
    // // Rotate drawing space by 90 degrees around X so the sphere's poles
    // // are vertical. Arguments are angle in degrees,
    // // and a three part rotation axis with x, y and z components.
    // // after this rotation the Z axis is up instead of the Y axis
    //
    // //    TODO 2c: uncomment this rotate
    // mv = mult(mv, rotate(90.0, vec3(1, 0, 0)));
    //
    // //Send the transformation matrix to the shader
    // gl.uniformMatrix4fv(mvIndex, gl.FALSE, flatten(mv));
    //
    // // Draw a sphere with urgl.drawSolidSphere
    // // Arguments are Radius, Slices, Stacks
    // // Sphere is centered around current origin.
    // //    TODO 2b: Change to drawWireSphere so you can see the sphere's poles
    // urgl.drawWireSphere(0.5, 40, 40);
    //
    //
    // ///////
    // // Draw a Torus
    // //    TODO 3: tilt the torus by 10 degrees and make it fit the sphere snugly
    //
    // // Change the draw color to cyan (see the colors object at start of code)
    // urgl.setDrawColour(colors.cyan);
    //
    // //    TODO 3a: add a rotation here like the one for the sphere
    // //             and change the angle to 10 degrees
    // mv = mult(mv, rotate(10.0, vec3(1, 0, 0)));
    //
    // //    TODO 3b: send the transformation matrix to the shader
    //
    // gl.uniformMatrix4fv(mvIndex, gl.FALSE, flatten(mv));
    // // Draw a torus with urgl.drawSolidTorus
    // //
    // // Arguments are:
    // //    - iradius: Inner Radius (radius of the torus "tube")
    // //    - oradius: Outer Radius (radius of the center of torus "ring")
    // //    - nsides: sides around one segment or ring of the torus
    // //    - nrings: number of rings around the torus
    //
    // //    TODO 3c: adjust the oradius so the ring fits the sphere exactly
    // //             HINT: how much bigger than the sphere does oradius need to be
    // //                   so that iradius will just touch it?
    // urgl.drawSolidTorus(0.1, objectDistance, 40, 40);
}