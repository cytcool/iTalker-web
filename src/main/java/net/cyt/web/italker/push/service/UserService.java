package net.cyt.web.italker.push.service;

import com.google.common.base.Strings;
import net.cyt.web.italker.push.bean.api.base.ResponseModel;
import net.cyt.web.italker.push.bean.api.user.UpdateInfoModel;
import net.cyt.web.italker.push.bean.db.User;
import net.cyt.web.italker.push.card.UserCard;
import net.cyt.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/user")
public class UserService extends BaseService {

    // 用户信息修改接口
    // 返回自己的个人信息
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {

        if (!UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        // 更新用户信息
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        // 构建自己的用户信息
        UserCard card = new UserCard(self, true);
        // 返回
        return ResponseModel.buildOk(card);

    }
}
