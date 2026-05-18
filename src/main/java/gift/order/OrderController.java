package gift.order;

import gift.auth.AuthenticationResolver;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> getOrders(
        @RequestHeader("Authorization") String authorization,
        Pageable pageable
    ) {
        // auth check
        var member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            return ResponseEntity.status(401).build();
        }
        var orders = orderService.getOrders(member.getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    // order flow:
    // 1. auth check
    // 2. validate option
    // 3. subtract stock
    // 4. deduct points
    // 5. save order
    // 6. cleanup wish
    // 7. send kakao notification
    @PostMapping
    public ResponseEntity<?> createOrder(
        @RequestHeader("Authorization") String authorization,
        @Valid @RequestBody OrderRequest request
    ) {
        // auth check
        var member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            return ResponseEntity.status(401).build();
        }

        var response = orderService.createOrder(member, request);
        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.created(URI.create("/api/orders/" + response.get().id()))
            .body(response.get());
    }
}
