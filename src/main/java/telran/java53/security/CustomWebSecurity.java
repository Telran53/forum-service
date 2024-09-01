package telran.java53.security;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java53.post.dao.PostRepository;
import telran.java53.post.model.Post;

@Service
@RequiredArgsConstructor
public class CustomWebSecurity {
	
	final PostRepository repository;
	
	public boolean checkPostAuthor(String postId, String userName) {
		Post post = repository.findById(postId).orElse(null);
		return post != null && post.getAuthor().equalsIgnoreCase(userName);
	}

}
