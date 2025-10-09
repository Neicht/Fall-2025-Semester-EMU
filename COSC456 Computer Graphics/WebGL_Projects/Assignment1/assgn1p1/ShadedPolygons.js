
// NOTE: "FIX" INDICATES GEMINI AI CREATED CODE.
// Google Gemini: https://gemini.google.com/app
"use strict";
let canvas;
let program;
let gl;
let buffer_Vertices;
let buffer_VertexColors;
let attributeLocation_VertexPosition;
let attributeLocation_ColorPosition;

const SHAPE_SQUARE = new Float32Array([
    -0.5, -0.5, // Bottom-left
    0.5, -0.5, // Bottom-right
    -0.5,  0.5, // Top-left
    0.5,  0.5, // Top-right
]);


// FIX: Changed to a simple 2D triangle, centered for consistent transformations.
const SHAPE_TRIANGLE = new Float32Array([
    0.0,  0.5,
    -0.5, -0.5,
    0.5, -0.5
]);

const COLORS = {
    RED:   vec4(1.0, 0.0, 0.0, 1.0),
    GREEN: vec4(0.0, 1.0, 0.0, 1.0),
    BLUE:  vec4(0.0, 0.0, 1.0, 1.0),
    WHITE: vec4(1.0, 1.0, 1.0, 1.0),
    BLACK: vec4(0.0, 0.0, 0.0, 1.0),
};

// INITIALIZE
window.onload = function init() {
    canvas = document.getElementById("gl-canvas");
    gl = canvas.getContext('webgl2');
    if (!gl) {
        throw "WebGL 2 not supported";
    }

    gl.viewport(0, 0, canvas.width, canvas.height);
    gl.clearColor(0.0, 0.0, 0.0, 1.0);
    gl.enable(gl.BLEND);
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);

    program = initShaders(gl, "vertex-shader", "fragment-shader");
    gl.useProgram(program);

    render();
};

// RENDER SCENE
function render() {
    gl.clear(gl.COLOR_BUFFER_BIT);

    drawTriangle(0, 0.75, 0.5, 0.5, [COLORS.RED, COLORS.GREEN, COLORS.BLUE]);
    drawCircle(-0.6, 0.75, 1, 0.6, 0.3, COLORS.RED);
    drawSpecialCircle(0.6, 0.75, 0.75, 0.75, 0.3, COLORS.RED)
    // FIX: This will now translate correctly without distortion.
    drawLayeredSquares(0, -0.4, 1, 0.1);
}

// DRAW SHAPES
function drawLayeredSquares(xPosition, yPosition, baseScale, step) {

    for (let i = 0; i < baseScale/step; i++) {
        const currentScale = baseScale - (i * step);
        const currentColor = (i % 2 === 0) ? COLORS.WHITE : COLORS.BLACK;
        const squareColorArray = [currentColor, currentColor, currentColor, currentColor];
        drawSquare(xPosition, yPosition, currentScale, currentScale, squareColorArray);
    }
}

function drawTriangle(xPosition, yPosition, xScale, yScale, color) {
    // FIX: Work on a COPY of the shape, not the original.
    let triangle = new Float32Array(SHAPE_TRIANGLE);

    // FIX: The transformation order must be SCALE first, then TRANSLATE.
    triangle = scaleXY(triangle, xScale, yScale);
    triangle = translateXY(triangle, xPosition, yPosition);

    buffer_Vertices = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_Vertices);
    gl.bufferData(gl.ARRAY_BUFFER, triangle, gl.STATIC_DRAW);

    attributeLocation_VertexPosition = gl.getAttribLocation(program, "aPosition");
    gl.vertexAttribPointer(attributeLocation_VertexPosition, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_VertexPosition);

    buffer_VertexColors = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_VertexColors);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(color), gl.STATIC_DRAW);

    attributeLocation_ColorPosition = gl.getAttribLocation(program, "aColor");
    gl.vertexAttribPointer(attributeLocation_ColorPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_ColorPosition);

    gl.drawArrays(gl.TRIANGLES, 0, 3);
}

function drawSquare(xPosition, yPosition, xScale, yScale, color) {

    let square = new Float32Array(SHAPE_SQUARE);

    square = scaleXY(square, xScale, yScale);
    square = translateXY(square, xPosition, yPosition);

    buffer_Vertices = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_Vertices);
    gl.bufferData(gl.ARRAY_BUFFER, square, gl.STATIC_DRAW);

    attributeLocation_VertexPosition = gl.getAttribLocation(program, "aPosition");
    gl.vertexAttribPointer(attributeLocation_VertexPosition, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_VertexPosition);

    buffer_VertexColors = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_VertexColors);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(color), gl.STATIC_DRAW);

    attributeLocation_ColorPosition = gl.getAttribLocation(program, "aColor");
    gl.vertexAttribPointer(attributeLocation_ColorPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_ColorPosition);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
}

function drawCircle(xPosition, yPosition, xScale, yScale, radius, color) {
    const precision = 100;
    let circleVertices = [];
    let circleColors = [];

    // center point
    circleVertices.push(xPosition, yPosition);
    // center color
    circleColors.push(color);

    for (let i = 0; i <= precision; i++) {
        const angle = (i / precision) * 2.0 * Math.PI;
        const x = (radius * Math.cos(angle) * xScale) + xPosition;
        const y = (radius * Math.sin(angle) * yScale) + yPosition;

        circleVertices.push(x, y);
        circleColors.push(color);
    }

    buffer_Vertices = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_Vertices);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(circleVertices), gl.STATIC_DRAW);

    attributeLocation_VertexPosition = gl.getAttribLocation(program, "aPosition");
    gl.vertexAttribPointer(attributeLocation_VertexPosition, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_VertexPosition);

    buffer_VertexColors = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_VertexColors);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(circleColors), gl.STATIC_DRAW);

    attributeLocation_ColorPosition = gl.getAttribLocation(program, "aColor");
    gl.vertexAttribPointer(attributeLocation_ColorPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_ColorPosition);

    gl.drawArrays(gl.TRIANGLE_FAN, 0, circleVertices.length / 2);
}

function drawSpecialCircle(xPosition, yPosition, xScale, yScale, radius, color) {
    const precision = 100;
    let circleVertices = [];
    let circleColors = [];

    // Generate vertices and colors in a loop from angle 0 to 2*PI.
    for (let i = 0; i <= precision; i++) {
        const angle = (i / precision) * 2.0 * Math.PI;
        // FIX:
        // --- 1. Calculate the Color based on the Angle ---
        // This formula maps the angle to a brightness value (0.0 to 1.0).
        // At angle = 0 (3 o'clock), brightness is 0 (black).
        // At angle = PI (9 o'clock), brightness is 1 (full color).
        const brightness = (1.0 - Math.cos(angle)) / 2.0;

        const vertexColor = vec4(
            color[0]- (i*0.01),
            color[1]- (i*0.01),
            color[2]- (i*0.01),
            color[3]
        );
        circleColors.push(vertexColor);


        const x = (radius * Math.cos(angle) * xScale) + xPosition;
        const y = (radius * Math.sin(angle) * yScale) + yPosition;
        circleVertices.push(x, y);
    }

    buffer_Vertices = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_Vertices);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(circleVertices), gl.STATIC_DRAW);

    attributeLocation_VertexPosition = gl.getAttribLocation(program, "aPosition");
    gl.vertexAttribPointer(attributeLocation_VertexPosition, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_VertexPosition);

    buffer_VertexColors = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer_VertexColors);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(circleColors), gl.STATIC_DRAW);

    attributeLocation_ColorPosition = gl.getAttribLocation(program, "aColor");
    gl.vertexAttribPointer(attributeLocation_ColorPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(attributeLocation_ColorPosition);

    gl.drawArrays(gl.TRIANGLE_FAN, 0, circleVertices.length / 2);
}

// HELPER FUNCTIONS
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