package milkstgo.backend.authentication.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ReferrerPolicyFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        filterChain.doFilter(request, response);
    }
}
