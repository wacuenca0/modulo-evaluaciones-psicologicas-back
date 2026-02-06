package ec.mil.dsndft.servicio_gestion.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CatalogUserClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogUserClient(RestTemplate restTemplate,
                             @Value("${services.catalogos.base-url:http://127.0.0.1:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<UserSummary> getAllUsers() {
        String url = baseUrl + "/api/users";
        try {
            ResponseEntity<UserSummary[]> response = restTemplate.getForEntity(url, UserSummary[].class);
            UserSummary[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (RestClientException ex) {
            log.error("Error al invocar {} para obtener usuarios desde catálogo: {}", url, ex.getMessage());
            throw ex;
        }
    }

    public UserSummary findByUsernameIgnoreCase(String username) {
        return getAllUsers().stream()
            .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElse(null);
    }

    @Data
    public static class UserSummary {
        private Long id;
        private String username;
        private String email;
        private Long roleId;
        private String roleName;
    }
}
