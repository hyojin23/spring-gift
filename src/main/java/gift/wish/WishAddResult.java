package gift.wish;

public record WishAddResult(
        WishResponse response,
        boolean created
) {
    public static WishAddResult created(Wish wish) {
        return new WishAddResult(WishResponse.from(wish), true);
    }

    public static WishAddResult existing(Wish wish) {
        return new WishAddResult(WishResponse.from(wish), false);
    }
}
