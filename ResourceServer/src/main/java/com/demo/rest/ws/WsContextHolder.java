package com.demo.rest.ws;

public class WsContextHolder {

	private static final ThreadLocal<WsContext> threadLocal = new ThreadLocal<WsContext>();

	public static void setContext(WsContext context) {
		threadLocal.set(context);
	}

	public static WsContext getContext() {
		return threadLocal.get();
	}

	public static void unset() {
		threadLocal.remove();
	}

}
