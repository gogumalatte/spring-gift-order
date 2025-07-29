package gift.controller.web;

import gift.dto.ItemRequest;
import gift.dto.ItemResponse;
import gift.dto.OptionRequest;
import gift.entity.Member;
import gift.login.Authenticated;
import gift.login.LoggedInMember;
import gift.service.ItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/items")
@Authenticated
public class AdminItemController {

    private final ItemService itemService;

    public AdminItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String listItems(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        Model model,
        @LoggedInMember Member loginMember
    ) {
        Slice<ItemResponse> itemSlice = itemService.getAllItems(pageable);
        model.addAttribute("itemSlice", itemSlice);
        model.addAttribute("loginMember", loginMember);
        return "admin/items/list";
    }

    @GetMapping("/new")
    public String newItemForm(@ModelAttribute("item") ItemRequest itemRequest) {
        if (itemRequest.getOptions() == null || itemRequest.getOptions().isEmpty()) {
            itemRequest.setOptions(List.of(new OptionRequest(null, 1)));
        }
        return "admin/items/form";
    }

    @Authenticated
    @PostMapping
    public String createItem(
        @Valid @ModelAttribute("item") ItemRequest itemRequest,
        BindingResult bindingResult,
        @LoggedInMember Member loginMember,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/items/form";
        }
        itemService.createItem(itemRequest, loginMember);
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 등록되었습니다!");
        return "redirect:/admin/items";
    }

    @GetMapping("/{id}")
    public String detailItem(@PathVariable("id") Long id, Model model) {
        ItemResponse item = itemService.getItemById(id);
        if (!model.containsAttribute("option")) {
            model.addAttribute("option", new OptionRequest(null, 1));
        }
        model.addAttribute("item", item);
        return "admin/items/detail";
    }

    @Authenticated
    @GetMapping("/{id}/edit")
    public String editItemForm(@PathVariable("id") Long id, Model model) {
        ItemResponse item = itemService.getItemById(id);
        List<OptionRequest> optionRequests = item.options().stream()
            .map(optionResponse -> new OptionRequest(optionResponse.name(), optionResponse.quantity()))
            .toList();
        model.addAttribute("item",
            new ItemRequest(item.name(), item.price(), item.imageUrl(), optionRequests));
        model.addAttribute("itemId", id);
        return "admin/items/form";
    }

    @Authenticated
    @PostMapping("/{id}/update")
    public String updateItem(
        @PathVariable("id") Long id,
        @Valid @ModelAttribute("item") ItemRequest itemRequest,
        BindingResult bindingResult,
        @LoggedInMember Member loginMember,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("itemId", id);
            return "admin/items/form";
        }
        itemService.updateItem(id, itemRequest, loginMember);
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 수정되었습니다!");
        return "redirect:/admin/items/" + id;
    }

    @Authenticated
    @PostMapping("/{id}/delete")
    public String deleteItem(
        @PathVariable("id") Long id,
        @LoggedInMember Member loginMember
    ) {
        itemService.deleteItem(id, loginMember);
        return "redirect:/admin/items";
    }

    @Authenticated
    @PostMapping("/{productId}/options")
    public String addOption(
        @PathVariable("productId") Long productId,
        @Valid @ModelAttribute("option") OptionRequest optionRequest,
        BindingResult bindingResult,
        @LoggedInMember Member loginMember,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            ItemResponse item = itemService.getItemById(productId);
            model.addAttribute("item", item);
            return "admin/items/detail";
        }

        itemService.addOptionToItem(productId, optionRequest, loginMember);
        return "redirect:/admin/items/" + productId;
    }
}