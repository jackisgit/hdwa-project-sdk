package com.hdwa.sdk.interceptor;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *
 * @author abao
 * @since 2023/11/24
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public TokenInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        //前端发来的token
        String token = request.getHeader(BaseDecConstant.AUTHORIZATION);

        if (StringUtils.isEmpty(token)) {
            response.getWriter().write(ResultEnum.TOKEN_EXCEPTION.getString());
            return false;
        }

        //调试token，方便测试调用
        if (BaseDecConstant.DEV_TOKEN.equals(token)) {
            return true;
        }

        if (!tokenService.verifyToken(token)) {
            response.getWriter().write(ResultEnum.TOKEN_EXCEPTION_ERROR.getString());
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }


}