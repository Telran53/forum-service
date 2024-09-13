package telran.java53.post.service.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Aspect
@Slf4j(topic = "Post service")
public class PostServiceLogger {
	
	@Pointcut("execution(* telran.java53.post.service.PostServiceImpl.findPostById(String)) && args(id)")
	public void findById(String id) {}
	
	@Pointcut("execution(public Iterable<telran.java53.post.dto.PostDto> telran.java53.post.service.PostServiceImpl.findPosts*(..))")
	public void bulkFindPosts() {}
	
	@Pointcut("@annotation(PostLogger)")
	public void annotatedPostLogger() {}
	
	@Before("findById(id)")
	public void getPostLogging(String id) {
		log.info("post with id {}, requested", id);
	}
	
	@Around("bulkFindPosts()")
	public Object bulkFindPostsLogging(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		long start = System.currentTimeMillis();
		Object result = pjp.proceed(args);
		long end = System.currentTimeMillis();
		log.info("method {} took {} ms", pjp.getSignature().getName(), end - start);
		return result;
	}
	
	@AfterReturning("annotatedPostLogger()")
	public void annotatedPostLoggerLogging() {
		log.info("annotated by PostLogger method done");
	}
}
