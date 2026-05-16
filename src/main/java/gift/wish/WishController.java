package gift.wish;

import gift.auth.AuthenticationResolver;
import gift.member.Member;
import gift.wish.exception.AuthenticationException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final WishService wishService;
    private final AuthenticationResolver authenticationResolver;

    public WishController(
        WishService wishService,
        AuthenticationResolver authenticationResolver
    ) {
        this.wishService = wishService;
        this.authenticationResolver = authenticationResolver;
    }

    @GetMapping
    public ResponseEntity<Page<WishResponse>> getWishes(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        Pageable pageable
    ) {
        var member = extractMember(authorization);
        var wishes = wishService.getWishes(member.getId(), pageable);
        return ResponseEntity.ok(wishes);
    }

    @PostMapping
    public ResponseEntity<WishResponse> addWish(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @Valid @RequestBody WishRequest request
    ) {
        var member = extractMember(authorization);

        var result = wishService.addWish(member.getId(), request.productId());

        if (!result.created()) {
            return ResponseEntity.ok(result.response());
        }

        return ResponseEntity.created(URI.create("/api/wishes/" + result.response().id()))
            .body(result.response());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWish(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @PathVariable Long id
    ) {
        var member = extractMember(authorization);

        wishService.removeWish(member.getId(), id);

        return ResponseEntity.noContent().build();
    }

    private Member extractMember(String authorization) {
        var member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            throw new AuthenticationException();
        }
        return member;
    }
}
