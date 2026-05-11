package gift.wish;

public record WishRemoveResult(
        boolean isNotFound,
        boolean isForbidden
) {
    public static WishRemoveResult notFound() {
        return new WishRemoveResult(true, false);
    }

    public static WishRemoveResult forbidden() {
        return new WishRemoveResult(false, true);
    }

    public static WishRemoveResult removed() {
        return new WishRemoveResult(false, false);
    }
}
