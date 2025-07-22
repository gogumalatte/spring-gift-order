package gift.dto;

public record MemberLoginRequest(
    String email,
    String password
) {
}