package com.doubleview.jeebase.system.utils;

import com.doubleview.jeebase.support.utils.DigestUtils;
import com.doubleview.jeebase.support.utils.EncodeUtils;
import com.doubleview.jeebase.system.model.User;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_EXCLUSIONPeer;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

/**
 * Shiro工具类
 */
public class ShiroUtils {


    private static Logger logger = LoggerFactory.getLogger(ShiroUtils.class);

    public static final String HASH_ALGORITHM = "SHA-1";//加密方式,选择sha-1加密
    public static final int HASH_INTERATIONS = 1024;//加密循环次数
    public static final int SALT_SIZE = 8;//盐长度

    /**
     * 得到当前用户
     * @return
     */
    public static User getCurrentUser(){
        User user = null;
        try{
            user = (User) SecurityUtils.getSubject().getPrincipal();
        }catch (Exception e){
            logger.error(e.getMessage() , e);
        }
        return user;
    }

    /**
     * 得到当前会话
     * @return
     */
    public static Session getSession(boolean create){
        Session session = getSubject().getSession(create);
        return session;
    }

    /**
     * 得到当前会话
     * @return
     */
    public static Session getSession(){
        Session session = getSubject().getSession();
        return session;
    }

    /**
     * 得到会话属性
     * @param key
     * @param value
     */
    public static void setSessionAttr(String key , Object value){
        Session session = getSession();
        session.setAttribute(key , value);
    }

    /**
     * 得到会话属性
     * @param key
     * @return
     */
    public static T getSessionAttr(String key){
        Session session = getSession();
        return  session!=null ? (T)session.getAttribute(key) : null;
    }

    /**
     * 移除会话属性
     * @param key
     */
    public static void removeSessionAttr(String key){
        Session session = getSession();
        if(session != null)
            session.removeAttribute(key);
    }


    /**
     * 得到当前用户(subject)
     * @return
     */
    public static Subject getSubject(){
        Subject subject = SecurityUtils.getSubject();
        return subject;
    }


    /**
     * 当前用户是否已认证
     *
     * @return 通过身份验证：true，否则false
     */
    public static boolean isAuthenticated() {
        return getSubject() != null && getSubject().isAuthenticated();
    }

    /**
     * 当前用户是否未认证
     *
     * @return 没有通过身份验证：true，否则false
     */
    public static boolean notAuthenticated() {
        return !isAuthenticated();
    }

    /**
     * 认证通过或已记住的用户
     *
     * @return 用户：true，否则 false
     */
    public static boolean isRemembered() {
        Subject subject = getSubject();
        return subject!=null ? subject.isRemembered() : null;
    }

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     * @param plainPassword 明文密码
     */
    public static String entryptPassword(String plainPassword) {
        String plain = EncodeUtils.unescapeHtml(plainPassword);
        byte[] salt = generateSalt(SALT_SIZE);
        byte[] hashPassword = DigestUtils.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return EncodeUtils.encodeHex(salt)+ EncodeUtils.encodeHex(hashPassword);
    }

    /**
     * 根据指定的saltString，并经过1024次sha-1 hash
     * @param plainPassword
     * @param saltString
     * @return
     */
    public static String entryptPassword(String plainPassword, String saltString) {
        String plain = EncodeUtils.unescapeHtml(plainPassword);
        byte[] salt = EncodeUtils.decodeHex(saltString);
        byte[] hashPassword = DigestUtils.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return EncodeUtils.encodeHex(salt) + EncodeUtils.encodeHex(hashPassword);
    }

    /**
     * 生成随机的Byte[]作为salt.
     *
     * @param length byte数组的大小
     */
    private static byte[] generateSalt(int length) {
        if(length <=0 ){
            throw  new IllegalArgumentException("length argument must be a positive integer (1 or larger) ");
        }
        byte[] bytes = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }


    public static void main(String[] args){
        String plainPassword = "123456";
        System.out.println(entryptPassword(plainPassword));
    }

}
