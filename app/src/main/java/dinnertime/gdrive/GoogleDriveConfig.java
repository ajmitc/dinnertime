package dinnertime.gdrive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author aaron.mitchell
 */
@Configuration
public class GoogleDriveConfig {
    
    @Bean
    public GoogleDriveApi getGoogleDriveApi(){
        GoogleDriveApi api = new GoogleDriveApi();
        return api;
    }
}
