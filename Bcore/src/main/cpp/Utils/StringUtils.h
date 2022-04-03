

JNIEnv *getEnv();

class StringUtils {
public:
    StringUtils(jstring j_str);

    const char *c_str() {
        return _c_str;
    }

    ~StringUtils();

private:
    jstring _j_str;
    const char *_c_str;
};
