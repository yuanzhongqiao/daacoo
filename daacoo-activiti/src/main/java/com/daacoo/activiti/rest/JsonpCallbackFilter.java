package com.daacoo.activiti.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author daacoo
 * @version 2019/10/18
 */
@WebFilter("/service/*")
public class JsonpCallbackFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(JsonpCallbackFilter.class);

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Map<String, String[]> parms = httpRequest.getParameterMap();

        if (parms.containsKey("callback")) {
            if (log.isDebugEnabled()) {
                log.debug("Wrapping response with JSONP callback '" + parms.get("callback")[0] + "'");
            }

            OutputStream out = httpResponse.getOutputStream();

            GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);

            chain.doFilter(request, wrapper);

            //handles the content-size truncation
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(new String(parms.get("callback")[0] + "(").getBytes());
            outputStream.write(wrapper.getData());
            outputStream.write(new String(");").getBytes());
            byte[] jsonpResponse = outputStream.toByteArray();

            wrapper.setContentType("text/javascript;charset=UTF-8");
            wrapper.setContentLength(jsonpResponse.length);

            out.write(jsonpResponse);

            out.close();

        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
