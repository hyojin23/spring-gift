package gift.order;

import gift.auth.AuthenticationResolver;
import gift.member.Member;
import gift.wish.exception.AuthenticationException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final AuthenticationResolver authenticationResolver;
    private final OrderService orderService;

    public OrderController(
        AuthenticationResolver authenticationResolver,
        OrderService orderService
    ) {
        this.authenticationResolver = authenticationResolver;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        Pageable pageable
    ) {
        var member = extractMember(authorization);
        var orders = orderService.getOrders(member.getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @Valid @RequestBody OrderRequest request
    ) {
        var member = extractMember(authorization);

        var response = orderService.createOrder(member, request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id()))
            .body(response);
    }

    private Member extractMember(String authorization) {
        var member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            throw new AuthenticationException();
        }
        return member;
    }
}
