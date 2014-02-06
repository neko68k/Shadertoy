package com.neko68k.shadertoy;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class ShadertoyRenderer implements GLSurfaceView.Renderer{

	private int mXres;
	private int mYres;
	private String vsSource = 
	                                      "attribute vec2 pos;"+
	                                      "void main()"+
	                                      "{"+
	                                          "gl_Position = vec4(pos.x,pos.y,0.0,1.0);"+
	                                      "}"+
	                                      "\n";
	
	private String fsSource = 
	                "void main()"+
	                "{"+
	                    "gl_FragColor = vec4(0.0,0.0,0.0,1.0);"+
	                "}"+
	                "\n";
	                
	public ShadertoyRenderer() {
		// TODO Auto-generated constructor stub
		
		float[] vertices= { -1.0f,  -1.0f,   1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,    1.0f,  1.0f,    -1.0f,  1.0f };
		GLES20.glGenBuffers(1, mQuadVBO, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mQuadVBO);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices, GLES20.GL_STATIC_DRAW);
		
		int res = NewShader(fsSource);
		
		DetermineShaderPrecision();
		
		MakeHeader();
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub
		
		
		
		int l2 = GLES20.glGetUniformLocation(0, "iGlobalTime");
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0,0,width,height);
		
		// do any resize related shit here
		mXres = width;
		mYres = height;
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// compile shader in here
		
	}
	
	public int CreateShader(String tfs){
		int[] compiled = new int[1];
		int program = GLES20.glCreateProgram();
		
		int vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		int fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		
		GLES20.glShaderSource(vs, vsSource);
		GLES20.glShaderSource(fs, tfs);
		
		GLES20.glCompileShader(vs);
		GLES20.glCompileShader(fs);
		GLES20.glGetShaderiv(vs, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if(compiled[0] == 0){
			//fail
			GLES20.glDeleteProgram(program);
		}
		GLES20.glGetShaderiv(fs, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if(compiled[0] == 0){
			//fail
			GLES20.glDeleteProgram(program);
		}
		GLES20.glAttachShader(program, vs);
		GLES20.glAttachShader(program, fs);
		
		GLES20.glLinkProgram(program);
		
		GLES20.glGetShaderiv(fs, GLES20.GL_LINK_STATUS, compiled, 0);
		if(compiled[0] == 0){
			//fail
			GLES20.glDeleteProgram(program);
		}
		
		return program;
	}

}
