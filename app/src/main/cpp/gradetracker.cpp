#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_trios_gradetracker_ProgressActivity_stringFromJNI(
        JNIEnv* env,
        jobject/* this */) {
    std::string hello = "Google Sucks!";
    return env->NewStringUTF(hello.c_str());
}