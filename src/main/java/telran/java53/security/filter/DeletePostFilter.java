package telran.java53.security.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.model.Role;
import telran.java53.accounting.model.UserAccount;
import telran.java53.post.dao.PostRepository;
import telran.java53.post.model.Post;

@Component
@RequiredArgsConstructor
@Order(60)
public class DeletePostFilter implements Filter {

	final PostRepository postRepository;
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			String principal = request.getUserPrincipal().getName();
			String[] parts = request.getServletPath().split("/");
			String postId = parts[parts.length - 1];
			Post post = postRepository.findById(postId).orElse(null);
			if (post == null) {
				response.sendError(404, "Not found");
				return;
			}
			UserAccount userAccount = userAccountRepository.findById(principal).get();
			if (!(principal.equals(post.getAuthor()) || userAccount.getRoles().contains(Role.MODERATOR))) {
				response.sendError(403, "You are not allowed to access this resource");
				return;
			}

		}
		chain.doFilter(request, response);
	}

	private boolean checkEndpoint(String method, String path) {
		return (HttpMethod.DELETE.matches(method) && path.matches("/forum/post/\\w+"));
	}


}
