package net.cyt.web.italker.push.provider;

import com.google.common.base.Strings;
import net.cyt.web.italker.push.bean.api.base.ResponseModel;
import net.cyt.web.italker.push.bean.db.User;
import net.cyt.web.italker.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * 用于所有的请求接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {

    // 实现接口的过滤方法
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 检查是否是登录注册接口
        String relationPath = ((ContainerRequest)requestContext).getPath(false);
        if (relationPath.startsWith("account/login")||relationPath.startsWith("account/register")){
            // 直接走正常逻辑，不拦截
            return;
        }

        // 从Headers中去找到第一个token节点
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)){

            // 查询自己的信息
            final User self = UserFactory.findByToken(token);
            if (self!=null){
                // 给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return self;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return false;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return null;
                    }
                });
                return;
            }
        }

        // 直接返回一个账户需要的登录的Model
        ResponseModel model = ResponseModel.buildAccountError();
        // 构建一个返回
        Response response = Response.status(Response.Status.OK)
                .entity(model)
                .build();

        // 拦截，停止y一个请求的继续下发，调用该方法之后返回请求
        // 不会走到Service中
        requestContext.abortWith(response);

    }
}
