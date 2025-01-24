package SmartLegalSearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    // 密碼加密
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 跨來源資源共用（CORS）設定
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允許所有路徑
                .allowedOrigins("http://localhost:4200") // 只允許 localhost:4200 請求
                .allowedHeaders("*") // 允許所有 Headers
                .allowedMethods("*") // 允許所有 Methods
                .allowCredentials(true); // 允許用戶憑證的跨域請求（如 Cookies）
    }
}


