package net.cyt.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.cyt.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {

    public Application(){
        //注册逻辑处理的包名
        packages(AccountService.class.getPackage().getName());

        //注册Json解析器
        register(JacksonJsonProvider.class);

        //注册日志打印输出
        register(Logger.class);

    }
}
