#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_openglshaderdemo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "OpenGL Demo with Native Support";
    return env->NewStringUTF(hello.c_str());
}
