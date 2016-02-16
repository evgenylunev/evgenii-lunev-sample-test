/**
 * 
 */
package com.epam.elunev.providers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.WeakHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.google.inject.Singleton;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;

/**
 * @author evgenii.lunev
 *
 */
@Singleton
@Provider
@Consumes({MediaType.APPLICATION_JSON, "text/json", "application/x-protobuf"})
@Produces({MediaType.APPLICATION_JSON, "text/json", "application/x-protobuf"})

public class ProtobufProvider extends JacksonJsonProvider {

	private Map<Object, byte[]> buffer = new WeakHashMap<Object, byte[]>();
	
	@Override
	 public long getSize(Object m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if(isProtobufType(mediaType)){
			Message message = (Message) m;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				message.writeTo(baos);
			} catch (IOException e) {
				return -1;
			}
			byte[] bytes = baos.toByteArray();
			buffer.put(message, bytes);
			return bytes.length;
		}else{
			return super.getSize(m, type, genericType, annotations, mediaType);
		}
   }

	@Override
	 public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if(isProtobufType(mediaType))			
			return true;
		else
			return super.isWriteable(type, genericType, annotations, mediaType);
	}
	
	private boolean isProtobufType(MediaType mediaType){
		if(mediaType != null){
			if("application/x-protobuf".equals(mediaType.getSubtype()) || mediaType.getSubtype().endsWith("x-protobuf") ){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if(isProtobufType(mediaType))			
				return true;
			else
				return super.isReadable(type, genericType, annotations, mediaType);
	}
	
	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders, 
            InputStream entityStream) throws IOException, WebApplicationException {
		if(isProtobufType(mediaType)){
		try {
			Method newBuilder = type.getMethod("newBuilder");
			GeneratedMessage.Builder<?> builder = (GeneratedMessage.Builder<?>) newBuilder.invoke(type);
			return builder.mergeFrom(entityStream).build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
		}else{
			return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
		}
	}
	
	@Override
	public void writeTo(Object m, Class type, Type genericType, Annotation[] annotations, 
			MediaType mediaType, MultivaluedMap httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		if(isProtobufType(mediaType))
			entityStream.write(buffer.remove((Message)m));
		else
			super.writeTo(m, type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}
	
}
