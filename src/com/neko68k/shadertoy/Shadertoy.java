package com.neko68k.shadertoy;

import android.opengl.GLSurfaceView.Renderer;

public class Shadertoy extends OpenGLES2WallpaperService{

	

	@Override
	Renderer getNewRenderer() {
		// TODO Auto-generated method stub
		return( new ShadertoyRenderer());
	}

}
