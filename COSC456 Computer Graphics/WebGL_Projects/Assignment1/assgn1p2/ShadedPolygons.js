
// NOTE: "FIX" INDICATES GEMINI AI CREATED CODE.
// Google Gemini: https://gemini.google.com/app
"use strict";
let canvas;
let program;
let gl;
let shapes = [];
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
    setupEventListeners();
    render();
};

function setupEventListeners() {
    let xValButton = document.getElementById("xValue");
    let yValButton = document.getElementById("yValue");

    document.getElementById("triangle_button").addEventListener("click", function() {
        // FIX: Add a new shape object to the 'shapes' array instead of drawing directly.
        shapes.push({
            type: 'triangle',
            x: parseFloat(xValButton.value),
            y: parseFloat(yValButton.value),
            xScale: 0.5,
            yScale: 0.5,
            color: [COLORS.RED, COLORS.GREEN, COLORS.BLUE]
        });
    });

    document.getElementById("square_button").addEventListener("click", function() {
        shapes.push({
            type: 'square',
            x: parseFloat(xValButton.value),
            y: parseFloat(yValButton.value),
            xScale: 0.5,
            yScale: 0.5,
            color: [COLORS.BLUE, COLORS.RED, COLORS.GREEN, COLORS.BLUE]
        });
    });
    document.getElementById("circle_button").addEventListener("click", function() {
        shapes.push({
            type: 'circle',
            x: parseFloat(xValButton.value),
            y: parseFloat(yValButton.value),
            xScale: 0.5,
            yScale: 0.5,
            radius: 0.5,
            color: COLORS.GREEN
        });
    });
    document.getElementById("fancy_squares_button").addEventListener("click", function() {
        shapes.push({
            type: 'fancy',
            x: parseFloat(xValButton.value),
            y: parseFloat(yValButton.value),
            baseScale: 0.5,
            step: 0.01
        })
    })
    document.getElementById("clear_button").addEventListener("click", function() {
        shapes = [];
    });
}

// RENDER SCENE
function render() {
    gl.clear(gl.COLOR_BUFFER_BIT);
    shapes.forEach(shape => {
        if (shape.type === 'triangle') {
            drawTriangle(shape.x, shape.y, shape.xScale, shape.yScale, shape.color);
        } else if (shape.type === 'square') {
            drawSquare(shape.x, shape.y, shape.xScale, shape.yScale, shape.color);
        } else if (shape.type === 'circle') {
            drawSpecialCircle(shape.x, shape.y, shape.xScale, shape.yScale, shape.radius, shape.color);
        } else if (shape.type === 'fancy') {
            drawLayeredSquares(shape.x, shape.y, shape.baseScale, shape.step);
        }
    });


    requestAnimationFrame(render);


    // drawTriangle(0, 0.75, 0.5, 0.5, [COLORS.RED, COLORS.GREEN, COLORS.BLUE]);
    // drawCircle(-0.6, 0.75, 1, 0.6, 0.3, COLORS.RED);
    // drawSpecialCircle(0.6, 0.75, 0.75, 0.75, 0.3, COLORS.RED)
    // drawLayeredSquares(0, -0.4, 1, 0.1);
}

// DRAW SHAPES
function drawLayeredSquares(xPosition, yPosition, baseScale, step) {

    for (let i = 0; i < baseScale/step; i++) {
        const currentScale = baseScale - (i * step);
        const currentColor = (i % 2 === 0) ? COLORS.RED : (i%3 === 0 ? COLORS.GREEN : COLORS.BLUE);
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
        color[1] += i/255
        color[2] += i/255;
        color[3] += i/255;
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
    let circleVertices = [xPosition, yPosition];
    let circleColors = [COLORS.RED];

    // FIX: Generate vertices and colors in a loop from angle 0 to 2*PI.
    for (let i = 0; i <= precision; i++) {
        const angle = (i / precision) * 2.0 * Math.PI;
        const vertexColor = vec4(
            color[0] + angle/i,
            color[1] + angle/3,
            color[2] + angle/4,
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