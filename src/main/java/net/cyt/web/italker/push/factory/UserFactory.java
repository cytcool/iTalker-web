package net.cyt.web.italker.push.factory;

import com.google.common.base.Strings;
import net.cyt.web.italker.push.bean.db.User;
import net.cyt.web.italker.push.utils.Hib;
import net.cyt.web.italker.push.utils.TextUtil;

import java.util.List;
import java.util.UUID;

public class UserFactory {

    // 通过Phone找到User
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session.createQuery("from User where phone=:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    // 通过Name找到User
    public static User findByName(String name) {
        return Hib.query(session -> (User) session.createQuery("from User where name=:name")
                .setParameter("name", name)
                .uniqueResult());
    }

    // 通过Token找到User
    public static User findByToken(String token) {
        return Hib.query(session -> (User) session.createQuery("from User where token=:token")
                .setParameter("token", token)
                .uniqueResult());
    }

    /**
     * 给当前的账户绑定PushId
     * @param user 自己的User
     * @param pushId 自己设备的PushId
     * @return
     */
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId))
            return null;
        // 第一步，查询是否有其他账户绑定了当前设备
        // 取消绑定，避免推送混乱
        // 查询的列表不能包括自己
        Hib.queryOnly(session -> {
            @SuppressWarnings("unchecked")
            List<User> userList = session.createQuery("from User where lower(pushId)=:pushId and id!=:userId ")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId())
                    .list();

            for (User u : userList) {
                // 更新为null
                u.setPushId(null);
                session.saveOrUpdate(u);
            }

        });

        if (pushId.equalsIgnoreCase(user.getPushId())){
            // 如果当前需要绑定的设备Id，之前已经绑定过了
            // 那么不需要额外绑定
            return user;
        }else {
            if (Strings.isNullOrEmpty(user.getPushId())){
                // TODO 推送一个消息
            }
            // 更新新的设备Id
            user.setPushId(pushId);
            return Hib.query(session -> {
                session.saveOrUpdate(user);
                return user;
            });
        }
    }

    public static User login(String account, String password) {
        final String accountStr = account.trim();
        final String encodePassword = encodePassword(password);

        // 寻找
        User user = Hib.query(session -> (User) session.createQuery("from User where phone=:phone and password=:password")
                .setParameter("phone", accountStr)
                .setParameter("password", encodePassword)
                .uniqueResult());
        if (user != null) {
            // 对User进行登录操作，更新Token
            user = login(user);
        }
        return user;
    }


    /**
     * 用户注册
     * 注册的操作需要写入数据库，并返回数据库中的User信息
     *
     * @param account  账号
     * @param password 密码
     * @param name     用户名
     * @return User
     */
    public static User register(String account, String password, String name) {

        // 去除密码的首尾空格
        password = password.trim();
        // 处理密码
        password = encodePassword(password);

        User user = createUser(account, password, name);
        if (user != null) {
            user = login(user);

        }
        return user;
    }

    /**
     * 注册部分中新建用户的逻辑
     *
     * @param account  手机号
     * @param password 加密后的密码
     * @param name     用户名
     * @return 返回一个用户
     */
    public static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        // 账号就是手机号，是用手机号进行注册的
        user.setPhone(account);

        return Hib.query(session -> (User) session.save(user));
    }

    /**
     * 登录操作
     *
     * @param user User
     * @return User
     */
    public static User login(User user) {
        String newToken = UUID.randomUUID().toString();
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);

        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    // 对密码进行加密操作
    public static String encodePassword(String password) {
        // 去除密码的首尾空格
        password = password.trim();
        // 进行MD5的非对称加密
        password = TextUtil.getMD5(password);
        // 再进行一次对称的Base64加密
        return TextUtil.encodeBase64(password);
    }
}
