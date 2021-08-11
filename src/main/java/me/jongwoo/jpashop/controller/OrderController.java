package me.jongwoo.jpashop.controller;

import lombok.RequiredArgsConstructor;
import me.jongwoo.jpashop.domain.Member;
import me.jongwoo.jpashop.domain.Order;
import me.jongwoo.jpashop.domain.item.Item;
import me.jongwoo.jpashop.repository.OrderSearch;
import me.jongwoo.jpashop.service.ItemService;
import me.jongwoo.jpashop.service.MemberService;
import me.jongwoo.jpashop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute OrderSearch orderSearch, Model model){

        final List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";

    }

    @PostMapping("/order")
    public String order(
            @RequestParam Long memberId,
            @RequestParam Long itemId,
            @RequestParam int count){
        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    @GetMapping("/order")
    public String createForm(Model model){
        final List<Member> members = memberService.findMembers();
        final List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";

    }
}
