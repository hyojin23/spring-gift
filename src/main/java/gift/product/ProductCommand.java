package gift.product;

public record ProductCommand(
    String name,
    int price,
    String imageUrl,
    Long categoryId,
    boolean allowKakaoName
) {
    public ProductCommand(String name, int price, String imageUrl, Long categoryId) {
        this(name, price, imageUrl, categoryId, false);
    }
}
