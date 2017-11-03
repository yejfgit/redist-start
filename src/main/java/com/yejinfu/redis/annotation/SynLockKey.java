package com.yejinfu.redis.annotation;


import java.lang.annotation.ElementType;  
import java.lang.annotation.Inherited;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  

/** 
* <b>同步锁 key</b><br/> 
* 加在方法的参数上，指定的参数会作为锁的key的一部分 
*  
* @author hzyejifu 
* 
*/  
@Target({ ElementType.PARAMETER })  
@Retention(RetentionPolicy.RUNTIME)  
@Inherited 
public @interface SynLockKey {
	/** 
     * key的拼接顺序 
     *  
     * @return 
     */  
    int index() default 0;  

}
