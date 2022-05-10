package dinnertime.controller;

import dinnertime.db.DinnerTimeDbAccessor;
import dinnertime.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author aaron.mitchell
 */
public class IdentifiedContextInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    private DinnerTimeDbAccessor dbAccessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(bearerToken)){
            String accessToken = resolveBearerToken(bearerToken);
            try {
                User user = dbAccessor.validateToken(accessToken);
                if (user.isLoggedIn())
                    IdentifiedContext.set(user);
            }
            catch(Exception e){

            }
        }
        return super.preHandle(request, response, handler);
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception{
        IdentifiedContext.remove();
        super.afterCompletion(request, response, handler, ex);
    }

    private String resolveBearerToken(String bearerToken){
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
