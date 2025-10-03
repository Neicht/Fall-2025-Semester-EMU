"use strict";

var gl;

var theta = 0.0;
var thetaLoc;
var color;
var delay = 100;
var direction = true;
var cR = 1.0;
var cG = 0.0;
var cB = 0.0;
var cRLoc, cGLoc, cBLoc;



window.onload = function init() {
    var canvas = document.getElementById("gl-canvas");

    gl = canvas.getContext('webgl2');
    if (!gl) alert("WebGL 2.0 isn't available");

    //
    //  Configure WebGL
    //
    gl.viewport(0, 0, canvas.width, canvas.height);
    gl.clearColor(1.0, 1.0, 1.0, 1.0);

    //  Load shaders and initialize attribute buffers

    var program = initShaders(gl, "vertex-shader", "fragment-shader");
    gl.useProgram(program);


    var vertices = [
        vec2(0, 1),
        vec2(-1, 0),
        vec2(1, 0),
        vec2(0, -1)
    ];

    // Load the data into the GPU
    var vBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, vBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices), gl.STATIC_DRAW);

    // Associate out shader variables with our data buffer

    var positionLoc = gl.getAttribLocation(program, "aPosition");
    gl.vertexAttribPointer(positionLoc, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(positionLoc);

    thetaLoc = gl.getUniformLocation(program, "uTheta");

    cRLoc = gl.getUniformLocation(program, "cR");
    cGLoc = gl.getUniformLocation(program, "cG");
    cBLoc = gl.getUniformLocation(program, "cB");

    // Initialize event handlers
    document.getElementById("Direction").onclick = function () {
        direction = !direction;
    };
    document.getElementById("Color").onclick = function () {
        cR = Math.random();
        cG = Math.random();
        cB = Math.random();
    };

    render();
}



function render()
{
    gl.clear(gl.COLOR_BUFFER_BIT);
    theta += (direction ? 0.1 : -0.1);
    gl.uniform1f(thetaLoc, theta);
    gl.uniform1f(cRLoc, cR);
    gl.uniform1f(cGLoc, cG);
    gl.uniform1f(cBLoc, cB);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    setTimeout(
        function (){requestAnimationFrame(render);}, delay
    );
}
