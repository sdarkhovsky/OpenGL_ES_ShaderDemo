package com.example.openglshaderdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Demo3Activity extends AppCompatActivity {

    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new Demo3SurfaceView(this);
        setContentView(mGLView);
    }
}

class Demo3SurfaceView extends GLSurfaceView {

    private final Demo3Renderer mRenderer;

    public Demo3SurfaceView(Context context){
        super(context);

        setEGLContextClientVersion(3);

        mRenderer = new Demo3Renderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}

class Demo3Renderer implements GLSurfaceView.Renderer {

    private Demo3Triangle mTriangle;

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        mTriangle.draw();
    }

    // EGLConfig could be imported as android.opengl.EGLConfig or as javax.microedition.khronos.egl.EGLConfig.
    // The android.opengl.EGLConfig import does not overwrite the super class abstract method
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a triangle
        mTriangle = new Demo3Triangle();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES31.glViewport(0, 0, width, height);
    }

    public static int loadShader(int type, String shaderCode){

        int shader = GLES31.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES31.glShaderSource(shader, shaderCode);
        GLES31.glCompileShader(shader);

        IntBuffer status = IntBuffer.allocate(1);
        GLES31.glGetShaderiv(shader, GLES31.GL_COMPILE_STATUS, status);
        if (status.get(0) == GLES31.GL_FALSE) {
            IntBuffer logLen = IntBuffer.allocate(1);
            GLES31.glGetShaderiv(shader, GLES31.GL_INFO_LOG_LENGTH, logLen);

            if (logLen.get(0) > 0) {
                String log = GLES31.glGetShaderInfoLog(shader);
                System.out.print(log);
            }
        }


        return shader;
    }
}

class Demo3Triangle {

    private final int mProgram;

    /*
     For a version GLES3.0 it's mandatory to have #version directive ending with \n
     and replace "attribute" qualifier with "in"
     In addition, the program runs correctly on the device, but closes on the emulator
    */
    private final String vertexShaderCode =
            "#version 300 es\n" +
                    "in vec3 VertexPosition;" +
                    "in vec3 VertexColor;" +
                    "out vec3 Color;" +
                    "void main() {" +
                    "  Color = VertexColor;" +
                    "  gl_Position = vec4(VertexPosition,1.0);" +
                    "}";

    private final String fragmentShaderCode =
            "#version 300 es\n" +
                    "in vec3 Color;" +
                    "out vec4 FragColor;" +
                    "void main() {" +
                    "  FragColor = vec4(Color, 1.0);" +
                    "}";

    private FloatBuffer positionBuffer;
    private FloatBuffer colorBuffer;
    static float positionData[] = {
            -0.8f, -0.8f, 0.0f,
            0.8f, -0.8f, 0.0f,
            0.0f, 0.8f, 0.0f };
    static final int positionDataValuesPerVertex = 3;

    static float colorData[] = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f };
    static final int colorDataValuesPerVertex = 3;
    private final int vertexCount = positionData.length / positionDataValuesPerVertex;

    FloatBuffer initializeFloatBuffer(float data[]) {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                data.length * Float.SIZE/8);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        FloatBuffer fb = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        fb.put(data);
        // set the buffer to read the first coordinate
        fb.position(0);

        return fb;
    }

    private void checkForError() {
       int error = GLES31.glGetError();
       if (GLES31.GL_NO_ERROR != error) {
           String log = String.format("Error = %d", error);
           System.out.print(log);
       }
    }

    private IntBuffer vaoHandle  = IntBuffer.allocate(1);
    private IntBuffer vboHandles = IntBuffer.allocate(2);
    private void createAndBindBufferAndArrayObjects() {
        positionBuffer = initializeFloatBuffer(positionData);
        colorBuffer = initializeFloatBuffer(colorData);

        // bind a GPU location (memory) to a generic vertex attribute index (0,1,...)
        GLES31.glBindAttribLocation(mProgram, 0, "VertexPosition");
        GLES31.glBindAttribLocation(mProgram, 1, "VertexColor");

        // Create the buffer objects
        GLES31.glGenBuffers(2, vboHandles);
        int positionBufferHandle = vboHandles.get(0);
        int colorBufferHandle = vboHandles.get(1);

        // map buffer objects to system memory buffers
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, positionBufferHandle);
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, positionBuffer.capacity() * Float.SIZE/8, positionBuffer, GLES31.GL_STATIC_DRAW);
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, colorBufferHandle);
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, colorBuffer.capacity() * Float.SIZE/8, colorBuffer, GLES31.GL_STATIC_DRAW);

        // Create and bind to a vertex array object, which stores the relationship between the buffers and the input attributes.
        // Create and set-up the vertex array object
        GLES31.glGenVertexArrays( 1, vaoHandle );
        GLES31.glBindVertexArray( vaoHandle.get(0));
        // The currently bound vertex array object is used for the commands EnableVertexAttribArray (see 2.10 Vertex Array Objects, glspec33.core.pdf)
        // which link it to the indexes previously mapped to GPU memory locations by glBindAttribLocation
        GLES31.glEnableVertexAttribArray(0); // Vertex position
        GLES31.glEnableVertexAttribArray(1); // Vertex color
        // Map indexes 0 and 1 to corresponding VBO (vertex buffer object) handle
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, positionBufferHandle);
//        checkForError();
        GLES31.glVertexAttribPointer( 0, positionDataValuesPerVertex, GLES31.GL_FLOAT, false, 0, null);

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, colorBufferHandle);
        GLES31.glVertexAttribPointer( 1, colorDataValuesPerVertex, GLES31.GL_FLOAT, false, 0, null);
    }

    public Demo3Triangle() {
        int vertexShader = Demo3Renderer.loadShader(GLES31.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = Demo3Renderer.loadShader(GLES31.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES31.glCreateProgram();

        // add the vertex shader to program
        GLES31.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES31.glAttachShader(mProgram, fragmentShader);

        // prior to linking?
        createAndBindBufferAndArrayObjects();

        // creates OpenGL ES program executables
        GLES31.glLinkProgram(mProgram);
    }

    public void draw() {
        // Add program to OpenGL ES environment
        GLES31.glUseProgram(mProgram);

        GLES31.glBindVertexArray(vaoHandle.get(0));

        // arrays must be enabled when this command executes (see 2.8.3 Drawing Commands of glspec33.core.pdf)
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount);

        /*
          need to disable vertex arrays ????
          GLES31.glDisableVertexAttribArray(0);
          GLES31.glDisableVertexAttribArray(1);
        */
    }
}
