package io.github.spharris.ssc.web;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.spharris.ssc.excepions.UnknownModuleNameException;

@Provider
public class UnknownModuleHandler implements ExceptionMapper<UnknownModuleNameException> {

	@Override
	public Response toResponse(UnknownModuleNameException exception) {
		Status s = Status.NOT_FOUND;
		Error e = Error.builder().statusCode(s.getStatusCode())
				.errorCode("UnknownModuleName")
				.details("The module with the name " + exception.getModuleName() + " does not exist")
				.build();
		
		return Response.status(Status.NOT_FOUND)
				.entity(e)
				.build();
	}

}
