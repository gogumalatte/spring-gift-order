package gift.service;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Item;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.exception.ItemNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoApiService kakaoApiService;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository, WishRepository wishRepository, KakaoApiService kakaoApiService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoApiService = kakaoApiService;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, Member member) {
        Option option = optionRepository.findById(request.optionId())
            .orElseThrow(() -> new ItemNotFoundException("해당 ID의 옵션을 찾을 수 없습니다: " + request.optionId()));

        option.subtractQuantity(request.quantity());

        Order order = new Order(member, option, request.quantity(), request.orderMessage());
        Order savedOrder = orderRepository.save(order);

        Item orderedItem = option.getItem();
        wishRepository.findByMemberAndProduct(member, orderedItem)
            .ifPresent(wishRepository::delete);

        if (member.isKakaoUser()) {
            kakaoApiService.sendMessageToMe(member.getKakaoAccessToken(), savedOrder);
        }

        return OrderResponse.from(savedOrder);
    }
}