package gift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ProductNameValidator implements ConstraintValidator<ValidProductName, String> {

    private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$");

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isBlank()) {
            return true;
        }

        if (name.length() > 15) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.")
                .addConstraintViolation();
            return false;
        }

        if (!ALLOWED_PATTERN.matcher(name).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("허용되지 않는 특수문자가 포함되어 있습니다. (사용 가능: ( ), [ ], +, -, &, /, _)")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}