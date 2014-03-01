package com.neko68k.shadertoy;

import android.opengl.GLSurfaceView.Renderer;

public class Shadertoy extends OpenGLES2WallpaperService{

	private String test = "void main(void)"+
"{"+
	"vec2 uv = gl_FragCoord.xy / iResolution.xy;"+
	"gl_FragColor = vec4(uv,0.5+0.5*sin(iGlobalTime),1.0);"+
"}";

	@Override
	Renderer getNewRenderer() {
		android.os.Debug.waitForDebugger();
		// TODO Auto-generated method stub
		ShadertoyRenderer sr = new ShadertoyRenderer();
		sr.NewShader(test);
		return( sr);
	}

}
