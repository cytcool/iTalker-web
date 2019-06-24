package net.cyt.web.italker.push.factory;

import net.cyt.web.italker.push.bean.db.User;
import net.cyt.web.italker.push.utils.Hib;
import net.cyt.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

public class UserFactory {

    public static User findByPhone(String phone){
        return Hib.query(session -> (User) session.createQuery("from User where phone=:inPhone")
                .setParameter("inPhone",phone)
                .uniqueResult());
    }

    public static User findByName(String name){
        return Hib.query(session -> (User) session.createQuery("from User where name=:name")
                .setParameter("name",name)
                .uniqueResult());
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

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        // 账号就是手机号，是用手机号进行注册的
        user.setPhone(account);

        // 进行数据库操作
        // 首先创建一个会话
        Session session = Hib.session();
        // 开启一个事务
        session.beginTransaction();
        try {
            // 保存操作
            session.save(user);
            // 提交事务
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            // 失败情况下需要回滚事务
            session.getTransaction().rollback();
            return null;
        }

    }

    private static String encodePassword(String password) {
        // 去除密码的首尾空格
        password = password.trim();
        // 进行MD5的非对称加密
        password = TextUtil.getMD5(password);
        // 再进行一次对称的Base64加密
        return TextUtil.encodeBase64(password);
    }
}
