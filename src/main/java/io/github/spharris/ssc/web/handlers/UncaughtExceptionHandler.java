package io.github.spharris.ssc.web.handlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.spharris.ssc.web.SscWebError;

@Provider
public class UncaughtExceptionHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		exception.printStackTrace();
		Status s = Status.INTERNAL_SERVER_ERROR;
		SscWebError e = SscWebError.builder().statusCode(s.getStatusCode()).errorCode("ServerError")
				.details("An unknown exception occurred")
				.build();
		
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
	}
	
}
