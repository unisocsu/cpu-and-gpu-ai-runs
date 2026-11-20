package com.example.game;

import android.util.Log;

/**
 * Native library wrapper for the C++ game engine.
 * Provides JNI bindings to the native game engine.
 */
public class GameNativeLib {
    private static final String TAG = "GameNativeLib";

    static {
        System.loadLibrary("native-lib");
        Log.i(TAG, "Native library loaded");
    }

    /**
     * Initialize the game engine with screen dimensions.
     */
    public native void nativeInit(int width, int height);

    /**
     * Update the game engine with delta time.
     */
    public native void nativeUpdate(float deltaTime);

    /**
     * Render the current game state.
     */
    public native void nativeRender();

    /**
     * Resize the game engine viewport.
     */
    public native void nativeResize(int width, int height);

    /**
     * Handle touch down event.
     */
    public native void nativeTouchDown(float x, float y);

    /**
     * Handle touch up event.
     */
    public native void nativeTouchUp(float x, float y);

    /**
     * Get the current score.
     */
    public native int nativeGetScore();

    /**
     * Dispose of the game engine.
     */
    public native void nativeDispose();
}
