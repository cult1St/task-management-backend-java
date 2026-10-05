package com.task_management.first_backend.application.chat.config;

import com.task_management.first_backend.application.shared.utils.JwtUtils;
import com.task_management.first_backend.application.users.models.User;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * STOMP / SockJS config. Principal name is always the user email so
 * {@code convertAndSendToUser(email, "/queue/chat", ...)} matches CONNECT sessions.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtUtils jwtUtils;
    private final TaskScheduler heartBeatScheduler;

    public WebSocketConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        this.heartBeatScheduler = scheduler;
    }

    @Bean(name = "heartBeatScheduler")
    public TaskScheduler heartBeatScheduler() {
        return heartBeatScheduler;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(heartBeatScheduler);
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(256 * 1024);
        registration.setSendTimeLimit(10_000);
        registration.setSendBufferSizeLimit(512 * 1024);
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setWebSocketEnabled(true)
                .setSessionCookieNeeded(false)
                .setHeartbeatTime(25_000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) {
                    return message;
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("Missing or invalid Authorization header for WebSocket CONNECT");
                    }

                    String authToken = authHeader.substring(7);
                    User user = jwtUtils.getUserFromToken(authToken);
                    if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
                        throw new IllegalArgumentException("Invalid WebSocket token");
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    user.getAuthorities()
                            );

                    accessor.setUser(authentication);
                    if (accessor.getSessionAttributes() != null) {
                        accessor.getSessionAttributes().put("user", authentication);
                        accessor.getSessionAttributes().put("username", user.getEmail());
                        accessor.getSessionAttributes().put("userId", user.getId());
                    }
                }

                return message;
            }
        });
    }
}
