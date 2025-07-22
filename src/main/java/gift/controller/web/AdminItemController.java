package gift.controller.web;

import gift.dto.ItemRequest;
import gift.dto.ItemResponse;
import gift.dto.OptionRequest;
import gift.login.Authenticated;
import gift.service.ItemService;
import gift.entity.Member;
import gift.login.Login;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class AdminItemController {

    private final ItemService itemService;

    public AdminItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String listItems(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        Model model
    ) {
        Page<ItemResponse> itemPage = itemService.getAllItems(pageable);
        model.addAttribute("itemPage", itemPage);
        return "admin/items/list";
    }

    @GetMapping("/new")
    public String newItemForm(Model model) {
        model.addAttribute("item", new ItemRequest(null, 0, null, new ArrayList<>()));
        return "admin/items/form";
    }

    @Authenticated
    @PostMapping
    public String createItem(
        @Valid @ModelAttribute("item") ItemRequest itemRequest,
        BindingResult bindingResult,
        @Login Member loginMember,
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
        @Login Member loginMember,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
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
        @Login Member loginMember,
        RedirectAttributes redirectAttributes
    ) {
        itemService.deleteItem(id, loginMember);
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 삭제되었습니다!");
        return "redirect:/admin/items";
    }

    @Authenticated
    @PostMapping("/{productId}/options")
    public String addOption(
        @PathVariable("productId") Long productId,
        @Valid @ModelAttribute("option") OptionRequest optionRequest,
        BindingResult bindingResult,
        Model model,
        @Login Member loginMember
    ) {
        if (bindingResult.hasErrors()) {
            ItemResponse item = itemService.getItemById(productId);
            model.addAttribute("item", item);
            return "admin/items/detail";
        }

        itemService.addOptionToItem(productId, optionRequest);
        return "redirect:/admin/items/" + productId;
    }
}