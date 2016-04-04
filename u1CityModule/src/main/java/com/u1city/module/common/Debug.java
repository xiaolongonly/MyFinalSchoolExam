/*
 * File Name: 		Debug.java
 * 
 * Copyright(c) 2011 Seed Co.,Ltd.
 * 
 * 		 All rights reserved.
 * 					
 */

package com.u1city.module.common;

import android.util.Log;



/**
 * 日志工具
 * @author zhengjb
 * 设置为true时开启
 */
public class Debug {
	private static boolean enableLog = false;

	/**
	 * 设置是否输出日志
	 * @param enable true输出 false 不输出
	 * */
	public static void enableLog(boolean enable) {
		enableLog = enable;
	}

	/**
	 * Send an INFO log message.
	 * 
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs. 
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void i(String tag, String msg) {
		if (enableLog) {
			Log.i(tag, msg);
		}
	}
	
	/**
	 * Send a WARN log message.
	 * 
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void w(String tag, String msg) {
		if (enableLog) {
			Log.w(tag, msg);
		}
	}
	
	/**
	 * Send a WARN log message.
	 * 
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void w(String msg) {
		if (enableLog) {
			Log.w("com.seed.android", msg);
		}
	}

	/**
	 * Send an ERROR log message.
	 * 
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void e(String tag, String msg) {
		if (enableLog) {
			Log.e(tag, msg);
		}
	}

	/**
	 * Send a ERROR log message and log the exception.
	 * 
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 * @param tr  An exception to log
	 * 
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (enableLog) {
			Log.e(tag, msg, tr);
		}
	}
	
	/**
	 * Send an ERROR log message.
	 * 
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void e(String msg) {
		if (enableLog) {
			Log.e("com.seed.android", msg);
		}
	}
	
	/**
	 * Send a ERROR log message and log the exception.
	 * 
	 * @param msg  The message you would like logged.
	 * @param tr  An exception to log
	 * 
	 */
	public static void e(String msg, Throwable tr) {
		if (enableLog) {
			Log.e("com.seed.android", msg, tr);
		}
	}

	/**
	 * Send a VERBOSE log message.
	 * 
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs. 
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void v(String tag, String msg) {
		if (enableLog) {
			Log.v(tag, msg);
		}
	}

	/**
	 * Send a DEBUG log message.
	 * 
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 * 
	 */
	public static void d(String tag, String msg) {
		if (enableLog) {
			Log.d(tag, msg);
		}
	}

	/**
	 * Prints a string to the target stream. The string is converted to an array of bytes using the encoding chosen during the construction of this stream. The bytes are then written to the target stream with write(int).
	 * 
	 * If an I/O error occurs, this stream's error state is set to true.
	 * 
	 * @param msg  the string to print to the target stream.
	 * 
	 */
	public static void print(String msg) {
		if (enableLog) {
			System.out.print(msg);
		}
	}

	/**
	 * Prints a string followed by a newline. The string is converted to an array of bytes using the encoding chosen during the construction of this stream. The bytes are then written to the target stream with write(int).
	 * 
	 * If an I/O error occurs, this stream's error state is set to true.
	 * 
	 * @param msg  the string to print to the target stream.
	 * 
	 */
	public static void println(String msg) {
		if (enableLog) {
			System.out.println(msg);
		}
	}

	/**
	 *  the string to print to the target stream.
	 *  
	 * @param objMsg
	 * 
	 */
	public static void println(Object objMsg) {
		if (enableLog) {
			System.out.println(objMsg);
		}
	}
}
