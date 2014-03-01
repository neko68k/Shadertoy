package com.neko68k.shadertoy;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class ShadertoyRenderer implements GLSurfaceView.Renderer{

	private int mXres;
	private int mYres;
	private int[] mQuadVBO = new int[1];
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
	
	private String fsSource =//"// Created by inigo quilez - iq/2013\n// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.\n\n//#define FULL_PROCEDURAL\n\n\n#ifdef FULL_PROCEDURAL\n\n// hash based 3d value noise\nfloat hash( float n )\n{\n    return fract(sin(n)*43758.5453);\n}\nfloat noise( in vec3 x )\n{\n    vec3 p = floor(x);\n    vec3 f = fract(x);\n\n    f = f*f*(3.0-2.0*f);\n    float n = p.x + p.y*57.0 + 113.0*p.z;\n    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),\n                   mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y),\n               mix(mix( hash(n+113.0), hash(n+114.0),f.x),\n                   mix( hash(n+170.0), hash(n+171.0),f.x),f.y),f.z);\n}\n#else\n\n// LUT based 3d value noise\nfloat noise( in vec3 x )\n{\n    vec3 p = floor(x);\n    vec3 f = fract(x);\n\tf = f*f*(3.0-2.0*f);\n\t\n\tvec2 uv = (p.xy+vec2(37.0,17.0)*p.z) + f.xy;\n\tvec2 rg = texture2D( iChannel0, (uv+ 0.5)/256.0, -100.0 ).yx;\n\treturn mix( rg.x, rg.y, f.z );\n}\n#endif\n\nvec4 map( in vec3 p )\n{\n\tfloat d = 0.2 - p.y;\n\n\tvec3 q = p - vec3(1.0,0.1,0.0)*iGlobalTime;\n\tfloat f;\n    f  = 0.5000*noise( q ); q = q*2.02;\n    f += 0.2500*noise( q ); q = q*2.03;\n    f += 0.1250*noise( q ); q = q*2.01;\n    f += 0.0625*noise( q );\n\n\td += 3.0 * f;\n\n\td = clamp( d, 0.0, 1.0 );\n\t\n\tvec4 res = vec4( d );\n\n\tres.xyz = mix( 1.15*vec3(1.0,0.95,0.8), vec3(0.7,0.7,0.7), res.x );\n\t\n\treturn res;\n}\n\n\nvec3 sundir = vec3(-1.0,0.0,0.0);\n\n\nvec4 raymarch( in vec3 ro, in vec3 rd )\n{\n\tvec4 sum = vec4(0, 0, 0, 0);\n\n\tfloat t = 0.0;\n\tfor(int i=0; i<64; i++)\n\t{\n\t\tif( sum.a > 0.99 ) continue;\n\n\t\tvec3 pos = ro + t*rd;\n\t\tvec4 col = map( pos );\n\t\t\n\t\t#if 1\n\t\tfloat dif =  clamp((col.w - map(pos+0.3*sundir).w)/0.6, 0.0, 1.0 );\n\n        vec3 lin = vec3(0.65,0.68,0.7)*1.35 + 0.45*vec3(0.7, 0.5, 0.3)*dif;\n\t\tcol.xyz *= lin;\n\t\t#endif\n\t\t\n\t\tcol.a *= 0.35;\n\t\tcol.rgb *= col.a;\n\n\t\tsum = sum + col*(1.0 - sum.a);\t\n\n        #if 0\n\t\tt += 0.1;\n\t\t#else\n\t\tt += max(0.1,0.025*t);\n\t\t#endif\n\t}\n\n\tsum.xyz /= (0.001+sum.w);\n\n\treturn clamp( sum, 0.0, 1.0 );\n}\n\nvoid main(void)\n{\n\tvec2 q = gl_FragCoord.xy / iResolution.xy;\n    vec2 p = -1.0 + 2.0*q;\n    p.x *= iResolution.x/ iResolution.y;\n    vec2 mo = -1.0 + 2.0*iMouse.xy / iResolution.xy;\n    \n    // camera\n    vec3 ro = 4.0*normalize(vec3(cos(2.75-3.0*mo.x), 0.7+(mo.y+1.0), sin(2.75-3.0*mo.x)));\n\tvec3 ta = vec3(0.0, 1.0, 0.0);\n    vec3 ww = normalize( ta - ro);\n    vec3 uu = normalize(cross( vec3(0.0,1.0,0.0), ww ));\n    vec3 vv = normalize(cross(ww,uu));\n    vec3 rd = normalize( p.x*uu + p.y*vv + 1.5*ww );\n\n\t\n    vec4 res = raymarch( ro, rd );\n\n\tfloat sun = clamp( dot(sundir,rd), 0.0, 1.0 );\n\tvec3 col = vec3(0.6,0.71,0.75) - rd.y*0.2*vec3(1.0,0.5,1.0) + 0.15*0.5;\n\tcol += 0.2*vec3(1.0,.6,0.1)*pow( sun, 8.0 );\n\tcol *= 0.95;\n\tcol = mix( col, res.xyz, res.w );\n\tcol += 0.1*vec3(1.0,0.4,0.2)*pow( sun, 3.0 );\n\t    \n    gl_FragColor = vec4( col, 1.0 );\n}\n\0";
			
			
			
			
	                "void main()"+
	                "{"+
	                    "gl_FragColor = vec4(1.0,0.0,0.0,1.0);"+
	                "}"+
	                "\n";
			
			

	                
	public ShadertoyRenderer() {
		// TODO Auto-generated constructor stub
		
		
		
		float[] var={-1.0f,  -1.0f,   1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,    1.0f,  1.0f,    -1.0f,  1.0f };
		FloatBuffer vertices = FloatBuffer.wrap(var);
		//vertices.allocate(12);
		//vertices.put(var);		
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
		Calendar cal = Calendar.getInstance();
		GLES20.glViewport(0,  0,  mXres,  mYres);
		GLES20.glUseProgram(mProgram);
		float[] times = { 0.0f,0.0f,0.0f,0.0f};
		int[] units = { 0, 1, 2, 3 };
		//float[] mouse = { mousePosX,mousePosY,mouseOriX,mouseOriY};
		float[] mouse = {0.0f,0.0f,0.0f,0.0f};
		float[] resos = { 0.0f,0.0f,0.0f, 0.0f,0.0f,0.0f, 0.0f,0.0f,0.0f, 0.0f,0.0f,0.0f};
		float[] dates = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
				 (cal.get(Calendar.HOUR)*60.0f*60+cal.get(Calendar.MINUTE)*60+cal.get(Calendar.SECOND))};
		
		int l2 = GLES20.glGetUniformLocation(0, "iGlobalTime");
		int l3 = GLES20.glGetUniformLocation( this.mProgram, "iResolution"        ); 
		if( l3!=0 ) GLES20.glUniform3f(  l3, this.mXres, this.mYres, 1.0f );
		int l4 = 0;//GLES20.glGetUniformLocation( this.mProgram, "iMouse"             ); 
		if( l4!=0 ) GLES20.glUniform4fv( l4, 4, mouse, 0 );
		int l5 = 0;//GLES20.glGetUniformLocation( this.mProgram, "iChannelTime"       );
		int l7 = 0;//GLES20.glGetUniformLocation( this.mProgram, "iDate"              ); 
		if( l7!=0 ) GLES20.glUniform4fv( l7, 4, dates, 0 );
		int l8 = 0;//GLES20.glGetUniformLocation( this.mProgram, "iChannelResolution" );
		
		int l1 = GLES20.glGetAttribLocation( mProgram, "pos");
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mQuadVBO[0]);
		GLES20.glVertexAttribPointer(l1,  2,  GLES20.GL_FLOAT,  false,  0,  0);
		GLES20.glEnableVertexAttribArray(l1);
		
		// port all the input shit later
		
		if(l5!=0){
			GLES20.glUniform1fv(l5,  4,  FloatBuffer.allocate(4).put(times));			
		}
		if(l8!=0){
			GLES20.glUniform3fv(l8, 12, FloatBuffer.allocate(12).put(resos));
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
	
	public int NewShader(String shaderCode){
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
