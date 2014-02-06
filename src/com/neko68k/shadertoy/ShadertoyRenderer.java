package com.neko68k.shadertoy;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class ShadertoyRenderer implements GLSurfaceView.Renderer{

	private int mXres;
	private int mYres;
	private int[] mQuadVBO;
	private String mPrecision;
	private String mHeader;
	private int mProgram;
	private String mSource;
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
		
		
		
		float[] var={-1.0f,  -1.0f,   1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,    1.0f,  1.0f,    -1.0f,  1.0f };
		FloatBuffer vertices = null;
		vertices.put(var);
		GLES20.glGenBuffers(1, mQuadVBO, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mQuadVBO[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.array().length, (Buffer)vertices, GLES20.GL_STATIC_DRAW);
		
		int res = NewShader(fsSource);
		
		DetermineShaderPrecision();
		
		MakeHeader();
	}
	
	private void DetermineShaderPrecision(){
		String h1 = "#ifdef GL_ES\n" +
	             "precision highp float;\n" +
	             "#endif\n";

	    String h2 = "#ifdef GL_ES\n" +
	             "precision mediump float;\n" +
	             "#endif\n";

	    String h3 = "#ifdef GL_ES\n" +
	             "precision lowp float;\n" +
	             "#endif\n";

	    String str = "void main() { gl_FragColor = vec4(0.0,0.0,0.0,1.0); }\n";
	    if(CreateShader(h1+str, false)==1){
	    	mPrecision = h1;
	    	return;
	    }
	    if(CreateShader(h2+str, false)==1){
	    	mPrecision = h2;
	    	return;
	    }
	    if(CreateShader(h3+str, false)==1){
	    	mPrecision = h3;
	    	return;
	    }
	    this.mPrecision = "";
		
	}
	
	private void MakeHeader(){
		String header = mPrecision;
		
		header += "uniform vec3      iResolution;\n" +
	              "uniform float     iGlobalTime;\n" +
	              "uniform float     iChannelTime[4];\n" +
	              "uniform vec4      iMouse;\n" +
	              "uniform vec4      iDate;\n" +
	              "uniform vec3      iChannelResolution[4];\n";
		
		this.mHeader = header;
		
	}
	
	private int GetHeaderSize(){
		int n = 13;
		//if supports derivatives n += 1;
		return n;
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0,  0,  mXres,  mYres);
		GLES20.glUseProgram(mProgram);
		float[] times = { 0.0f,0.0f,0.0f,0.0f};
		int[] units = { 0, 1, 2, 3 };
		float[] mouse = { mousePosX,mousePosY,mouseOriX,mouseOriY}; 
		float[] resos = { 0.0f,0.0f,0.0f, 0.0f,0.0f,0.0f, 0.0f,0.0f,0.0f, 0.0f,0.0f,0.0f};
		
		
		int l2 = GLES20.glGetUniformLocation(0, "iGlobalTime");
		int l3 = GLES20.glGetUniformLocation( this.mProgram, "iResolution"        ); 
		if( l3!=0 ) GLES20.glUniform3f(  l3, this.mXres, this.mYres, 1.0f );
		int l4 = GLES20.glGetUniformLocation( this.mProgram, "iMouse"             ); 
		if( l4!=0 ) GLES20.glUniform4fv( l4, 4, mouse, 0 );
		int l5 = GLES20.glGetUniformLocation( this.mProgram, "iChannelTime"       );
		int l7 = GLES20.glGetUniformLocation( this.mProgram, "iDate"              ); 
		if( l7!=0 ) GLES20.glUniform4fv( l7, 4, dates, 0 );
		int l8 = GLES20.glGetUniformLocation( this.mProgram, "iChannelResolution" );
		
		int l1 = GLES20.glGetAttribLocation( mProgram, "pos");
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mQuadVBO[0]);
		GLES20.glVertexAttribPointer(l1,  2,  GLES20.GL_FLOAT,  false,  0,  0);
		GLES20.glEnableVertexAttribArray(l1);
		
		// port all the input shit later
		
		if(l5!=0){
			GLES20.glUniform1fv(l5,  4,  times);			
		}
		if(l8!=0){
			GLES20.glUniform3fv(l8, 12, resos);
		}
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES,  0,  6);
		GLES20.glDisableVertexAttribArray(l1);
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
	
	public int CreateShader(String tfs, boolean nativeDebug){
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
			return 0;
		}
		GLES20.glGetShaderiv(fs, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if(compiled[0] == 0){
			//fail
			GLES20.glDeleteProgram(program);
			return 0;
		}
		GLES20.glAttachShader(program, vs);
		GLES20.glAttachShader(program, fs);
		
		GLES20.glLinkProgram(program);
		
		GLES20.glGetShaderiv(fs, GLES20.GL_LINK_STATUS, compiled, 0);
		if(compiled[0] == 0){
			//fail
			GLES20.glDeleteProgram(program);
			return 0;
		}
		
		return program;
	}
	
	private int NewShader(String shaderCode){
		int res = CreateShader(mHeader+shaderCode, true);
		if(res==0){
			return 1;
		}
		if(mProgram!=0){
			GLES20.glDeleteProgram(mProgram);
		}
		mProgram = res;
		mSource = shaderCode;
		return 0;
	}
	
	public void SetSize(int xres, int yres){
		mXres = xres;
		mYres = yres;
	}

}
