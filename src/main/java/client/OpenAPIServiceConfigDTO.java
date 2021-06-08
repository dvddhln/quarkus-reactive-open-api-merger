package client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class OpenAPIServiceConfigDTO {

    List<OpenAPIService> openAPIServices;

    @Builder
    @Getter
    public static class OpenAPIService {

        private String displayName;

        private String url;

        private boolean hide;

    }


}
