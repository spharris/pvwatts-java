package io.github.spharris.ssc.web;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestModuleService {
	@Mock UriInfo uriInfo;
	
	@InjectMocks ModuleService moduleService;
	
	@Test
	public void populatesUri() {
		String path = "thePath";
		//when(uriInfo.getAbsolutePath()).thenReturn("testPath");
	}
}
