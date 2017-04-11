#include <jni.h>
#include <string.h>
#include <stdbool.h>

// String.contains has better performance, no need native method. unused
JNIEXPORT jboolean JNICALL
Java_ru_euphoria_messenger_util_AndroidUtils_stringContains(JNIEnv *env, jclass type, jstring str_,
                                                                               jstring word_) {
    const char *str = (*env)->GetStringUTFChars(env, str_, 0);
    const char *word = (*env)->GetStringUTFChars(env, word_, 0);

    char *ptr = strstr(str, word);

    (*env)->ReleaseStringUTFChars(env, str_, str);
    (*env)->ReleaseStringUTFChars(env, word_, word);

    return (jboolean) (ptr != NULL);
}