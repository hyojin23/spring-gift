package gift.order;

import gift.member.Member;
import gift.option.Option;

public record OrderCreatedEvent(Member member, Order order, Option option) {
}
