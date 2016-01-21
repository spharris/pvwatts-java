package io.github.spharris.ssc.web;

import static com.google.common.base.Preconditions.*;

public class SscWebError {
	
	private final int statusCode;
	private final String errorCode;
	private final String details;
	
	private SscWebError(int statusCode, String errorCode, String details) {
		checkNotNull(errorCode);
		
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.details = details;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getDetails() {
		return details;
	}
	
	public static class Builder {
		
		private int statusCode;
		private String errorCode;
		private String details;
		
		private Builder() {}
		
		public Builder statusCode(int code) {
			statusCode = code;
			return this;
		}
		
		public Builder errorCode(String code) {
			checkNotNull(code);
			
			errorCode = code;
			return this;
		}
		
		public Builder details(String details) {
			checkNotNull(details);
			
			this.details = details;
			return this;
		}
		
		public SscWebError build() {
			return new SscWebError(statusCode, errorCode, details);
		}
	}
}
