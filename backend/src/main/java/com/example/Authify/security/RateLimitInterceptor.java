package com.example.Authify.security;


import com.example.Authify.service.RateLimiterService;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitInterceptor extends OncePerRequestFilter {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if(request.getRequestURI().startsWith("/api/auth/generate-otp")){
            String ip = request.getRemoteAddr();

            BucketProxy bucket = rateLimiterService.resolveBucket(ip);

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if(probe.isConsumed()){
                response.addHeader("X-Rate-Limit-Remaining",String.valueOf(probe.getRemainingTokens()));
                filterChain.doFilter(request,response);
            }
            else{
                long waitForRefill = probe.getNanosToWaitForRefill()/1_000_000_000;
                response.setStatus(429);
                response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
                response.getWriter().write("Too many requests. Please wait " + waitForRefill + " seconds.");
                return;
            }
        }
        else{
            filterChain.doFilter(request,response);
        }
    }
}
