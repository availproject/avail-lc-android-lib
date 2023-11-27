# Avail Android Light client lib
This is a sample implementation for using light client with an android app. <br/>

To use this follow the given steps- 
1. Take your package name (here it is, Java.com.example.availlibrary.AvailLightClientLib) and compile light client for your package id using the given [steps](https://github.com/availproject/light-client-lib).
2. Place compiled .so files in src/main/jni/arm64-v8a directory in the library.
3. Place header and libc++_shared.so in the same path.
4. Use this library and all its exposed function in your app.
