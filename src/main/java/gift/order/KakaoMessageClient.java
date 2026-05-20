package gift.order;

import gift.product.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoMessageClient {
    private final RestClient restClient;
    private final KakaoMessageTemplateBuilder templateBuilder;

    public KakaoMessageClient(RestClient.Builder builder, KakaoMessageTemplateBuilder templateBuilder) {
        this.restClient = builder.build();
        this.templateBuilder = templateBuilder;
    }

    public void sendToMe(String accessToken, Order order, Product product) {
        var templateObject = templateBuilder.build(order, product);

        var params = new LinkedMultiValueMap<String, String>();
        params.add("template_object", templateObject);

        restClient.post()
            .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(params)
            .retrieve()
            .toBodilessEntity();
    }
}
