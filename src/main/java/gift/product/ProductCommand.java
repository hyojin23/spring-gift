package gift.product;

public record ProductCommand(
    String name,
    int price,
    String imageUrl,
    Long categoryId
) {
}
