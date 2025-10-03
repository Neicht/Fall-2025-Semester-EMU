"use strict";

var gl;
var delay = 1;

const SHAPES = {
    SQUARE: new Float32Array([
        -0.5, -0.5, // Bottom-left
        0.5, -0.5, // Bottom-right
        -0.5,  0.5, // Top-left
        0.5,  0.5, // Top-right
    ]),
    TRIANGLE: new Float32Array([
        0.0,  0.5,
        -0.5, -0.5,
        0.5, -0.5
    ])
};

let player;
let scene = [];
let frameContents = [];
const keysPressed = {};




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




    //scene.push(SHAPES.SQUARE);
    // Load the data into the GPU
    var vBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, vBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(scene), gl.STATIC_DRAW);

    // var cBuffer = gl.createBuffer();
    // gl.bindBuffer(gl.ARRAY_BUFFER, cBuffer);
    // gl.bufferData(gl.ARRAY_BUFFER, color, gl.STATIC_DRAW);

    // Associate out shader variables with our data buffer

    var positionLoc = gl.getAttribLocation(program, "aPosition");
    gl.vertexAttribPointer(positionLoc, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(positionLoc);

    // var colorLoc = gl.getAttribLocation(program, "aColor");
    // gl.vertexAttribPointer(colorLoc, 4, gl.FLOAT, false, 0, 0);
    // gl.enableVertexAttribArray(colorLoc);



    // Initialize event handlers
    // document.getElementById("Direction").onclick = function () {
    //     direction = !direction;
    // };
    document.getElementById("Color").onclick = function () {
        let player = {
            type: 'player',
            shape: SHAPES.TRIANGLE
        };
        scene.push(player);
    };
        // 1. Create an object to store the state of our keys


        // 2. Add a 'keydown' event listener to the window
        window.addEventListener('keydown', (event) => {
        // Set the corresponding key in our object to true
        keysPressed[event.key] = true;
    });

        // 3. Add a 'keyup' event listener to the window
        window.addEventListener('keyup', (event) => {
        // Set the corresponding key in our object to false
        delete keysPressed[event.key];
    });
    render();
}



function render()
{


    gl.clear(gl.COLOR_BUFFER_BIT);
    if(keysPressed['w'] || keysPressed['W']){
        scene.push(SHAPES.SQUARE);
    }
    gl.drawArrays(gl.TRIANGLE_STRIP, 0 , 4);
    // scene.forEach(object => {
    //     if (object.type === 'player') {
    //
    //
    //     } else {
    //
    //     }
    // });



    setTimeout(
        function (){requestAnimationFrame(render);}, delay
    );
}

function drawObject(shape){


}
function translateXY(vec2Matrix, xValue, yValue) {
    for (let i = 0; i < vec2Matrix.length; i += 2) {
        vec2Matrix[i] = vec2Matrix[i] + xValue;
    }
    for (let i = 1; i < vec2Matrix.length; i += 2) {
        vec2Matrix[i] = vec2Matrix[i] + yValue;
    }
    return vec2Matrix;
}

function scaleXY(vec2Matrix, xValue, yValue) {
    for (let i = 0; i < vec2Matrix.length; i += 2) {
        vec2Matrix[i] = vec2Matrix[i] * xValue;
    }
    for (let i = 1; i < vec2Matrix.length; i += 2) {
        vec2Matrix[i] = vec2Matrix[i] * yValue;
    }
    return vec2Matrix;
}
