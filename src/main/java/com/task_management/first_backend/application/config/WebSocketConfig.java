package com.task_management.first_backend.application.config;

import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.utils.JwtUtils;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtUtils jwtUtils;
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("[WS AUTH] CONNECT attempt received");

                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader == null) {
                        System.out.println("[WS AUTH] ❌ No Authorization header found");
                        return message;
                    }

                    if (!authHeader.startsWith("Bearer ")) {
                        System.out.println("[WS AUTH] ❌ Invalid Authorization format: " + authHeader);
                        return message;
                    }

                    String authToken = authHeader.substring(7);
                    System.out.println("[WS AUTH] Token extracted");

                    try {
                        User user = jwtUtils.getUserFromToken(authToken);

                        if (user == null) {
                            System.out.println("[WS AUTH] ❌ Token parsed but user is null");
                            return message;
                        }

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        user.getAuthorities()
                                );

                        // 🔥 CRITICAL FIX
                        accessor.setUser(authentication);
                        accessor.getSessionAttributes().put("user", authentication);
                        accessor.getSessionAttributes().put("username", user.getUsername());

                        System.out.println("[WS AUTH] ✅ Authentication successful for user: " + user.getUsername());
                    } catch (Exception e) {
                        System.out.println("[WS AUTH] ❌ Exception during authentication: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                return message;
            }
        });
    }
}
