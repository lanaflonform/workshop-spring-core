/**
 * 
 */
package com.zenika.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author acogoluegnes
 *
 */
public class JdkProxyTest {

	@Test public void logWithProxy() {
		// TODO 01 analyser l'utilisation d'un proxy JDK pour logguer systématiquement
		
		// TODO 02 lancer le test et vérifier la présence du log sur la console
		final HelloService targetService = new DefaultHelloService();
		
		InvocationHandler handler = new InvocationHandler() {
			
			private final Logger LOGGER = LoggerFactory.getLogger(JdkProxyTest.class);
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				LOGGER.info("Appel à {}.{}",method.getDeclaringClass().getName(),method.getName());
				Object res = method.invoke(targetService, args);
				return res;
			}
		};
		HelloService decoratedHelloService = (HelloService) Proxy.newProxyInstance(getClass().getClassLoader(),
			new Class<?>[]{HelloService.class},	
			handler
		);
		decoratedHelloService.hello();
	}
	
	public interface HelloService {
		
		void hello();
		
	}
	
	public class DefaultHelloService implements HelloService {
		
		@Override
		public void hello() {
			System.out.println("Hello!");			
		}
		
	}
	
}
