package com.epam.elunev.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.elunev.rest.RestService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * @author evgenii.lunev
 *
 */

public class RestAppInitializer extends GuiceServletContextListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAppInitializer.class);
	private static final String PROPERTIES_FILE =  "WEB-INF/application.properties";

	private Properties getProperties() {
		Properties prop = new Properties();				
		try (FileReader in = new FileReader(PROPERTIES_FILE)) {
			prop.load(in);
		} catch (IOException e) {
			LOGGER.error("Failed to initialize properties from properties");
		}
		return prop;
	}
	
	@Override
	protected Injector getInjector() {
		
		return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                /* bind the REST resources */
            	Names.bindProperties(binder(), getProperties());
            	bind(RestService.class);
                /* bind jackson converters for JAXB/JSON serialization */
                bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
                bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
                
                serve("*").with(GuiceContainer.class);//, initParams);
            }

			
        }
			,	new JpaPersistModule("weatherServiceJpaUnit")
			);
	}

}
