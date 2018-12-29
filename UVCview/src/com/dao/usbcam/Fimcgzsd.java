package com.dao.usbcam;

public class Fimcgzsd {
	static public native int open(byte[] devname);
	static public native int qbuf(int index);
	static public native int streamon();
	static public native int streamoff();
	static public native int dqbuf(byte[] videodata);
	static public native int release();
	static public native int init(int width, int height,int numbuf);
	static {
		System.loadLibrary("fimcgzsd");
	}
}
