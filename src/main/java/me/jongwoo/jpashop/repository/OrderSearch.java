package me.jongwoo.jpashop.repository;

import lombok.Data;
import me.jongwoo.jpashop.domain.OrderStatus;

@Data
public class OrderSearch {

    private String memberName; // 회원 이름
    private OrderStatus orderStatus; //주문 상태 ORDER,CANCEL
}
