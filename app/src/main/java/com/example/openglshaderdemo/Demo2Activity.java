package com.example.openglshaderdemo;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;

public class Demo2Activity extends AppCompatActivity {

    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new Demo2SurfaceView(this);
        setContentView(mGLView);
    }
}

class Demo2SurfaceView extends GLSurfaceView {

    private final Demo2Renderer mRenderer;

    public Demo2SurfaceView(Context context){
        super(context);

        setEGLContextFactory(new ContextFactory());

        setEGLContextClientVersion(3);

        mRenderer = new Demo2Renderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private static double glVersion = 3.1;
    private static class ContextFactory implements GLSurfaceView.EGLContextFactory {

        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(
                EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {

            Log.w(TAG, "creating OpenGL ES " + glVersion + " context");
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, (int) glVersion,
                    EGL10.EGL_NONE };
            // attempt to create a OpenGL ES 3.0 context
            EGLContext context = egl.eglCreateContext(
                    display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            return context; // returns null if 3.0 is not supported;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("ContextFactory", "display:" + display + " context: " + context);
                Log.i("ContextFactory", "tid=" + Thread.currentThread().getId());
                throw new RuntimeException("eglDestroyContext failed: ");
            }
        }
    }
}

class Demo2Renderer implements GLSurfaceView.Renderer {

    private Demo2Triangle mTriangle;

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

        String renderer = GLES31.glGetString( GLES31.GL_RENDERER );
        String vendor = GLES31.glGetString( GLES31.GL_VENDOR );
        String version = GLES31.glGetString( GLES31.GL_VERSION );
        String glslVersion =
                GLES31.glGetString( GLES31.GL_SHADING_LANGUAGE_VERSION );
        String extensions = GLES31.glGetString(GLES31.GL_EXTENSIONS);
/*
        My Nexus 6 phone:
        renderer = "Adreno (TM) 420"
        vendor = "Qualcomm"
        version = "OpenGL ES 3.2 V@145.0 (GIT@If5818605d9)"
        glslVersion = "OpenGL ES GLSL ES 3.20"

        extensions = GL_OES_EGL_image GL_OES_EGL_image_external GL_OES_EGL_sync GL_OES_vertex_half_float GL_OES_framebuffer_object GL_OES_rgb8_rgba8 GL_OES_compressed_ETC1_RGB8_texture GL_AMD_compressed_ATC_texture GL_KHR_texture_compression_astc_ldr GL_OES_texture_npot GL_EXT_texture_filter_anisotropic GL_EXT_texture_format_BGRA8888 GL_OES_texture_3D GL_EXT_color_buffer_float GL_EXT_color_buffer_half_float GL_QCOM_alpha_test GL_OES_depth24 GL_OES_packed_depth_stencil GL_OES_depth_texture GL_OES_depth_texture_cube_map GL_EXT_sRGB GL_OES_texture_float GL_OES_texture_float_linear GL_OES_texture_half_float GL_OES_texture_half_float_linear GL_EXT_texture_type_2_10_10_10_REV GL_EXT_texture_sRGB_decode GL_OES_element_index_uint GL_EXT_copy_image GL_EXT_geometry_shader GL_EXT_tessellation_shader GL_OES_texture_stencil8 GL_EXT_shader_io_blocks GL_OES_shader_image_atomic GL_OES_sample_variables GL_EXT_texture_border_clamp GL_EXT_multisampled_render_to_texture GL_OES_shader_multisample_interpolation GL_EXT_texture_cube_map_array GL_EXT_draw_buffers_indexed GL_EXT_gpu_shader5 GL_EXT_robustness GL_EXT_texture_buffer GL_OES_texture_storage_multisample_2d_array GL_OES_sample_shading GL_OES_get_program_binary GL_EXT_debug_label GL_KHR_blend_equation_advanced GL_KHR_blend_equation_advanced_coherent GL_QCOM_tiled_rendering GL_ANDROID_extension_pack_es31a GL_EXT_primitive_bounding_box GL_OES_standard_derivatives GL_OES_vertex_array_object GL_EXT_disjoint_timer_query GL_KHR_debug GL_EXT_YUV_target GL_EXT_sRGB_write_control GL_EXT_texture_norm16 GL_EXT_discard_framebuffer GL_OES_surfaceless_context GL_OVR_multiview GL_OVR_multiview2 GL_EXT_texture_sRGB_R8 GL_KHR_no_error GL_EXT_debug_marker GL_OES_EGL_image_external_essl3 GL_OVR_multiview_multisampled_render_to_texture GL_EXT_buffer_storage GL_EXT_blit_framebuffer_params
 */

        // initialize a triangle
        mTriangle = new Demo2Triangle();
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


class Demo2Triangle {

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

    public Demo2Triangle() {
        positionBuffer = initializeFloatBuffer(positionData);
        colorBuffer = initializeFloatBuffer(colorData);

        int vertexShader = Demo2Renderer.loadShader(GLES31.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = Demo2Renderer.loadShader(GLES31.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES31.glCreateProgram();

        // add the vertex shader to program
        GLES31.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES31.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES31.glLinkProgram(mProgram);
    }

    int setFloatVertexAttributes(String attrName, int valuesPerVertex, boolean needNormalization, Buffer ptr) {
        int stride = valuesPerVertex* Float.SIZE/8;
        // get handle to vertex shader's vPosition member
        int array_id = GLES31.glGetAttribLocation(mProgram, attrName);

        // Enable a handle to the triangle vertices
        GLES31.glEnableVertexAttribArray(array_id);

        // Prepare the triangle coordinate data
        GLES31.glVertexAttribPointer(array_id, valuesPerVertex,
                GLES31.GL_FLOAT, needNormalization,
                stride, ptr);

        return array_id;
    }

    public void draw() {
        // Add program to OpenGL ES environment
        GLES31.glUseProgram(mProgram);
        int array_ids[] = {
                setFloatVertexAttributes("VertexPosition", positionDataValuesPerVertex, false, positionBuffer),
                setFloatVertexAttributes("VertexColor", colorDataValuesPerVertex, false, colorBuffer)
        };

        // arrays must be enabled when this command executes (see 2.8.3 Drawing Commands of glspec33.core.pdf)
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        for (int i =0; i < array_ids.length; i++) {
            GLES31.glDisableVertexAttribArray(array_ids[i]);
        }
    }
}
