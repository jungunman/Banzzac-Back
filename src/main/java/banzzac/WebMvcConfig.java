package banzzac;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		System.out.println("WebMvcConfig 실행");
		registry.addMapping("/api/**").allowedOrigins("http://localhost:5173","http://192.168.219.60:5173")
		.allowedMethods("GET", "POST")
        .allowedHeaders("Authorization", "Content-Type")	
        .exposedHeaders("Custom-Header","Authorization")
        .allowCredentials(true)
        .maxAge(3600);
		
		//WebMvcConfigurer.super.addCorsMappings(registry);
	}
	
}

