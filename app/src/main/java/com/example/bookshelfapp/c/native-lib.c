#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_bookshelfapp_MainActivity_getApiKey(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "ea27d0b9a26f3cb9f7baf3f50aae5bcd");
}