package com.example.spring.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Zhangkh on 2017/3/30.
 */
@WebFilter(urlPatterns = "/test/*")
public class FilterA implements Filter {

    private final Logger logger = LoggerFactory.getLogger(FilterA.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("In FilterA");
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        logger.info("x-auth-token:" + req.getHeader("x-auth-token"));
//        Cookie[] cookies = req.getCookies();
//        for(Cookie c:cookies) {
//            logger.info("cookies:" + c.getName());
//            logger.info("session:" + c.getValue());
//        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
