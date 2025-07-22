package gift.controller.api;

import gift.dto.ItemRequest;
import gift.dto.ItemResponse;
import gift.dto.OptionResponse;
import gift.entity.Member;
import gift.login.Authenticated;
import gift.login.Login;
import gift.service.ItemService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/products")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request, @Login Member loginMember) {
        ItemResponse newItem = itemService.createItem(request, loginMember);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(
                newItem.id())
            .toUri();
        return ResponseEntity.created(location).body(newItem);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable("productId") Long id) {
        ItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getAllItems(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ItemResponse> itemPage = itemService.getAllItems(pageable);
        return ResponseEntity.ok(itemPage);
    }

    @Authenticated
    @PutMapping("/{productId}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable("productId") Long id,
        @RequestBody ItemRequest request, @Login Member loginMember) {
        ItemResponse updatedItem = itemService.updateItem(id, request, loginMember);
        return ResponseEntity.ok(updatedItem);
    }

    @Authenticated
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteItem(
        @PathVariable("productId") Long id,
        @Login Member loginMember
    ) {
        itemService.deleteItem(id, loginMember);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/options")
    public ResponseEntity<List<OptionResponse>> getOptionsByProductId(@PathVariable("productId") Long productId) {
        List<OptionResponse> options = itemService.getOptionsByProductId(productId);
        return ResponseEntity.ok(options);
    }
}
