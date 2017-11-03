# redist-start
spring boot redis strart

stept1 spring boot project application.yml add:
jedis :
  host : 127.0.0.1
  port : 6379
  password : 000000
  timeout : 5000
  maxTotal: 100
  maxIdle: 10
  maxWaitMillis : 100000
  
  
sync lock(集群同步锁)
spring boot start java add:
@ComponentScan(basePackages="com.*")

method add:
@SynLock(synKey = "12345")
public void add(@SynLockKey(index = 1) String key, @SynLockKey(index = 0) int key1) {
  i = i + 1;
  System.out.println("i=-===========" + i);
}
