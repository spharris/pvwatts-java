package io.github.spharris.ssc.web;

import io.github.spharris.ssc.ExecutionHandler.MessageType;

public final class LogMessage {
	private final MessageType type;
	private final float time;
	private final String message;
	
	public LogMessage(MessageType type, float time, String message) {
		this.type = type;
		this.time = time;
		this.message = message;
	}
	
	public MessageType getType() {
		return type;
	}
	public float getTime() {
		return time;
	}
	public String getMessage() {
		return message;
	}
}
