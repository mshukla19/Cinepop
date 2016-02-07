package com.example.mshukla.cinepop;

import android.app.Application;
import android.content.Context;

import info.quantumflux.QuantumFlux;

/**
 * Created by manas on 2/7/16.
 */
public class CinepopApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        QuantumFlux.initialize(this);
    }
}
