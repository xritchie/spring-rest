package spittr.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import spittr.web.WebConfig;

/**
 * Created by shawnritchie on 18/04/15.
 * Alternative to the traditional web.xml file
 * Servlet 3.0+ support
 */
public class SpitterWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    //"defined beans for the ContextLoaderListener sre typically the middle-tier and data-tier
    //components that drive the backend of the application"
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class, AmqpConfig.class };
    }

    //“define beans for DispatcherServlet’s application context”
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfig.class };
    }

    //“identifies one or more paths that DispatcherServlet will be mapped to.
    //In this case, it’s mapped to /, indicating that it will be the application’s
    //default servlet. It will handle all requests coming into the application.”
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

}
