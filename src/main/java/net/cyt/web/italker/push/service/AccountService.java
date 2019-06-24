package net.cyt.web.italker.push.service;

import net.cyt.web.italker.push.bean.api.account.RegisterModel;
import net.cyt.web.italker.push.bean.db.User;
import net.cyt.web.italker.push.card.UserCard;
import net.cyt.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

//127.0.0.1/api/account/...
@Path("/account")
public class AccountService {

    @POST
    @Path("/register")
    //指定请求与返回的响应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserCard register(RegisterModel model){

        User user = UserFactory.findByPhone(model.getAccount().trim());

        if (user!=null){
            UserCard card = new UserCard();
            card.setName("已有了Phone");
            return card;
        }

        user = UserFactory.findByName(model.getName().trim());

        if (user!=null){
            UserCard card = new UserCard();
            card.setName("已有了Name");
            return card;
        }

        user = UserFactory.register(model.getAccount(),model.getPassword(),model.getName());
        if (user != null){
            UserCard card = new UserCard();
            card.setName(user.getName());
            card.setPhone(user.getPhone());
            card.setSex(user.getSex());
            card.setModifyAt(user.getUpdateAt());
            card.setFollow(true);
            return card;
        }
        return null;

    }
}
