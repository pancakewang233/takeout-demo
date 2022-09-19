package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
* 检查用户是否已经完成登录
* */
@WebFilter(filterName = "loginCheckFilter")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER= new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)  servletResponse;

        // * 1,获取本次请求的url
        String requestURI = request.getRequestURI();

        //* 2，判断本次请求是否需要处理
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        boolean check = check(urls, requestURI);

        //* 3，不需要处理，直接放行
        if(check){
            filterChain.doFilter(request, response);
            return;
        }

        // * 4，判断登录状态，已登录直接放行
        if(request.getSession().getAttribute("employee") != null){
            filterChain.doFilter(request, response);
            return;
        };

        //* 5，如未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /*
    * 路径匹配，检查本次请求是否需要放行
    * @params urls
    * @params requestURI
    * */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
