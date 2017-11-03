package com.yejinfu.redis.aop;

import com.yejinfu.redis.annotation.SynLock;
import com.yejinfu.redis.annotation.SynLockKey;
import com.yejinfu.redis.client.RedisClient;
import com.yejinfu.redis.common.LoggerUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;



/**
 * Created by hzyejinfu on 2017/11/2.
 * 锁的切面编程<br/>
 * 针对添加@RedisLock 注解的方法进行加锁
 */
@Aspect
@Component
public class RedisLockAspect {


    /**
     * 拦截同步方法，拿到lock继续执行，否则终止
     * */
    @Around("execution(* com..*..task..*(..)) && @annotation(com.netease.hrs.jedis.annotation.SynLock)")
    public Object lock(ProceedingJoinPoint pjp) throws Throwable {
        SynLock lockInfo = getLockInfo(pjp);
        if (lockInfo == null) {
            throw new IllegalArgumentException("配置参数错误");
        }

        String synKey = getSynKey(pjp, lockInfo.synKey());
        if (synKey == null || "".equals(synKey)) {
            throw new IllegalArgumentException("配置参数synKey错误");
        }

        boolean lock = false;
        Object obj = null;
        try {
            Long ifGetLock = RedisClient.setnx(synKey,synKey);
            lock = ifGetLock==1?true:false;
            LoggerUtil.info(synKey+":ifGetLock="+ifGetLock+";lock="+lock);

            // 得到锁，没有人加过相同的锁
            if (lock) {
            	//社招key失效时间
                RedisClient.expire(synKey,(int)lockInfo.keepMills());
                //等待防止过早结束删除key达不到同步锁效果
                TimeUnit.MILLISECONDS.sleep(lockInfo.sleepMills());
                //通过
                obj = pjp.proceed();
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            // 如果获取到了锁，释放锁
            if (lock) {
                RedisClient.del(synKey);
                LoggerUtil.info("delete lockKey="+synKey);
            }
        }
        return obj;
    }


    /**
     * 获取RedisLock注解信息
     */
    private SynLock getLockInfo(ProceedingJoinPoint pjp) {
        try {
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            Method method = methodSignature.getMethod();
            SynLock lockInfo = method.getAnnotation(SynLock.class);
            return lockInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取包括方法参数上的key<br/>
     * redis key的拼写规则为 "RedisSyn+" + synKey + @P4jSynKey
     *
     */
    private String getSynKey(ProceedingJoinPoint pjp, String synKey) {
        try {
            synKey = "RedisSyn_" + synKey;
            Object[] args = pjp.getArgs();
            if (args != null && args.length > 0) {
                MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                Annotation[][] paramAnnotationArrays = methodSignature.getMethod().getParameterAnnotations();

                SortedMap<Integer, String> keys = new TreeMap<Integer, String>();

                for (int ix = 0; ix < paramAnnotationArrays.length; ix++) {
                    SynLockKey p4jSynKey = getAnnotation(SynLockKey.class, paramAnnotationArrays[ix]);
                    if (p4jSynKey != null) {
                        Object arg = args[ix];
                        if (arg != null) {
                            keys.put(p4jSynKey.index(), arg.toString());
                        }
                    }
                }

                if (keys != null && keys.size() > 0) {
                    for (String key : keys.values()) {
                        synKey = synKey + key;
                    }
                }
            }

            return synKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotation(final Class<T> annotationClass, final Annotation[] annotations) {
        if (annotations != null && annotations.length > 0) {
            for (final Annotation annotation : annotations) {
                if (annotationClass.equals(annotation.annotationType())) {
                    return (T) annotation;
                }
            }
        }

        return null;
    }

}