package com.example.karat.memoryleak.singleton;

import android.content.Context;

public class SampleClass {

    private static SampleClass sampleClassInstance;

    private Context context;

    private SampleClass(Context context) {
        this.context = context;
    }

    public static SampleClass getSampleClassInstance(Context context) {
        if (sampleClassInstance == null) {
            sampleClassInstance = new SampleClass(context);
        }
        return sampleClassInstance;
    }
}
