package gift.controller.api;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.login.Authenticated;
import gift.login.LoggedInMember;
import gift.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Authenticated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
        @Valid @RequestBody OrderRequest request,
        @LoggedInMember Member loginMember
    ) {
        OrderResponse response = orderService.placeOrder(request, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}