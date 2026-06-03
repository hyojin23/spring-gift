package gift.order;

public record OrderNotificationPayload(
    String kakaoAccessToken,
    String productName,
    String optionName,
    int productPrice,
    int quantity,
    String message
) {
}
