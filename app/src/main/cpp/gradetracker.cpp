#include <jni.h>
#include <string>
using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_com_trios_gradetracker_ProgressActivity_CalcGrades(
        JNIEnv* env,
        jobject obj,/* this */
        jdouble goal,
        jint takenCount,
        jint takenTotal,
        jint toGoCount) {

    double offset = (goal * takenCount - takenTotal) / toGoCount;
    offset =  round( offset * 100.0 ) / 100.0;
    string newTarget = to_string(goal + offset);
    return env->NewStringUTF(newTarget.c_str());
}