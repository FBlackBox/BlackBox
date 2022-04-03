#include <jni.h>
#include "StringUtils.h"

StringUtils::StringUtils(jstring j_str) {
    _j_str = j_str;
    _c_str = getEnv()->GetStringUTFChars(j_str, NULL);
}

StringUtils::~StringUtils() {
    getEnv()->ReleaseStringUTFChars(_j_str, _c_str);
}