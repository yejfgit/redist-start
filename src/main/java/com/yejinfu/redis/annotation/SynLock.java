package com.yejinfu.redis.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType; 
import java.lang.annotation.RetentionPolicy; 

/** 
 * <b>同步锁：</b><br/> 
 * 主要作用是在服务器集群环境下保证方法的synchronize；<br/> 
 * 标记在方法上，使该方法的执行具有互斥性，并不保证并发执行方法的先后顺序；<br/> 
 * 如果原有“A任务”获取锁后任务执行时间超过最大允许持锁时间，且锁被“B任务”获取到，在“B任务”成功货物锁会并不会终止“A任务”的执行；<br/> 
 * <br/> 
 * <b>注意：</b><br/> 
 * 使用过程中需要注意keepMills、toWait、sleepMills、maxSleepMills等参数的场景使用；<br/> 
 * 需要安装redis，并使用spring和spring-data-redis等，借助redis NX等方法实现。
 *  
 * @author hzyjinfu 
 * 
 */  
@Target({ ElementType.METHOD })  
@Retention(RetentionPolicy.RUNTIME)  
@Inherited
public @interface SynLock {
	/** 
     * 锁的key<br/> 
     * 如果想增加坑的个数添加非固定锁，可以在参数上添加@P4jSynKey注解，但是本参数是必写选项<br/> 
     * redis key的拼写规则为 "RedisSyn+" + synKey + @P4jSynKey<br/> 
     *  
     */  
    String synKey();  
  
    /** 
     * 持锁时间，超时时间，持锁超过此时间自动丢弃锁<br/> 
     * 单位毫秒,默认60秒<br/>
     * 如果为0表示永远不释放锁，在设置为0的情况下toWait为true是没有意义的<br/> 
     * 但是没有比较强的业务要求下，不建议设置为0 
     */  
    long keepMills() default 60;
  
    /** 
     * 当获取锁失败，是继续等待还是放弃<br/> 
     * 默认为继续等待 
     */  
    boolean toWait() default true;  
  
    /** 
     * 没有获取到锁的情况下且toWait()为继续等待，睡眠指定毫秒数继续获取锁，也就是轮训获取锁的时间<br/> 
     * 默认为20秒
     *  
     * @return 
     */  
    long sleepMills() default 20 * 1000;
  
    /** 
     * 锁获取超时时间：<br/> 
     * 没有获取到锁的情况下且toWait()为true继续等待，最大等待时间，如果超时抛出 
     * {@link java.util.concurrent.TimeoutException.TimeoutException} 
     * ，可捕获此异常做相应业务处理；<br/> 
     * 单位毫秒,默认一分钟，如果设置为0即为没有超时时间，一直获取下去； 
     *  
     * @return 
     */  
    long maxSleepMills() default 60 * 1000;  
}
