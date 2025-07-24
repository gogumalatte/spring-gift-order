package gift.service;

import gift.dto.ItemRequest;
import gift.dto.ItemResponse;
import gift.dto.OptionRequest;
import gift.dto.OptionResponse;
import gift.entity.Item;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Role;
import gift.exception.AuthorizationException;
import gift.exception.ItemNotFoundException;
import gift.repository.ItemRepository;
import gift.repository.OptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;

    public ItemService(ItemRepository itemRepository, OptionRepository optionRepository) {
        this.itemRepository = itemRepository;
        this.optionRepository = optionRepository;
    }

    public Slice<ItemResponse> getAllItems(Pageable pageable) {
        Slice<Item> itemSlice = itemRepository.findSliceBy(pageable);
        return itemSlice.map(ItemResponse::from);
    }

    public ItemResponse getItemById(Long id) {
        return itemRepository.findById(id)
            .map(ItemResponse::from)
            .orElseThrow(() -> new ItemNotFoundException("해당 ID의 상품을 찾을 수 없습니다: " + id));
    }

    @Transactional
    public ItemResponse createItem(ItemRequest request, Member loginMember) {
        validateAdminRoleForKakaoKeyword(request.getName(), loginMember);
        Item item = new Item(null, request.getName(), request.getPrice(), request.getImageUrl());
        Item savedItem = itemRepository.save(item);
        List<Option> options = request.getOptions().stream()
            .map(optionRequest -> optionRequest.toEntity(savedItem))
            .toList();
        optionRepository.saveAll(options);
        savedItem.getOptions().addAll(options);
        return ItemResponse.from(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest request, Member loginMember) {
        validateAdminRoleForKakaoKeyword(request.getName(), loginMember);
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException("수정할 상품을 찾을 수 없습니다: " + id));

        item.updateInfo(request.getName(), request.getPrice(), request.getImageUrl());

        item.getOptions().clear();

        List<Option> newOptions = request.getOptions().stream()
            .map(optionRequest -> optionRequest.toEntity(item))
            .toList();
        item.getOptions().addAll(newOptions);

        return ItemResponse.from(item);
    }

    @Transactional
    public void deleteItem(Long id, Member loginMember) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException("삭제할 상품을 찾을 수 없습니다: " + id));
        if (loginMember.getRole() != Role.ADMIN) {
            throw new AuthorizationException("상품을 삭제할 권한이 없습니다.");
        }
        itemRepository.deleteById(id);
    }

    private void validateAdminRoleForKakaoKeyword(String productName, Member member) {
        if (productName.contains("카카오") && member.getRole() != Role.ADMIN) {
            throw new AuthorizationException("카카오 관련 상품은 ADMIN만 등록할 수 있습니다.");
        }
    }

    public List<OptionResponse> getOptionsByProductId(Long productId) {
        Item item = itemRepository.findById(productId)
            .orElseThrow(() -> new ItemNotFoundException("해당 ID의 상품을 찾을 수 없습니다: " + productId));

        return item.getOptions().stream()
            .map(OptionResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void addOptionToItem(Long productId, OptionRequest optionRequest, Member loginMember) {
        Item item = itemRepository.findById(productId)
            .orElseThrow(() -> new ItemNotFoundException("옵션을 추가할 상품을 찾을 수 없습니다: " + productId));

        if (loginMember.getRole() != Role.ADMIN) {
            throw new AuthorizationException("옵션을 추가할 권한이 없습니다.");
        }

        boolean isDuplicate = item.getOptions().stream()
            .anyMatch(option -> option.getName().equals(optionRequest.getName()));
        if (isDuplicate) {
            throw new IllegalArgumentException("동일한 이름의 옵션이 이미 존재합니다.");
        }

        optionRepository.save(optionRequest.toEntity(item));
    }
}