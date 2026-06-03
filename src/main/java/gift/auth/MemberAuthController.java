package gift.auth;

import gift.member.MemberRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles member registration and login.
 *
 * @author brian.kim
 * @since 1.0
 */
@RestController
@RequestMapping("/api/members")
public class MemberAuthController {
    private final MemberAuthService memberAuthService;

    public MemberAuthController(MemberAuthService memberAuthService) {
        this.memberAuthService = memberAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(memberAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberAuthService.login(request));
    }
}
