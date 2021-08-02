package me.jongwoo.jpashop.service;

import me.jongwoo.jpashop.domain.Address;
import me.jongwoo.jpashop.domain.Member;
import me.jongwoo.jpashop.domain.Order;
import me.jongwoo.jpashop.domain.OrderStatus;
import me.jongwoo.jpashop.domain.item.Book;
import me.jongwoo.jpashop.domain.item.Item;
import me.jongwoo.jpashop.exception.NotEnoughStockException;
import me.jongwoo.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //Given
        Member member = createMember();

        Item book = createBook("JPA", 10000, 10);

        //When
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //Then
        Order getOrder = orderRepository.findById(orderId);

        // 상품 주문시 상태는 ORDER
        assertThat(getOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER);

        // 주문한 상품 종류 수 정확해야 함
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);

        // 주문 가격은 가격 * 수량
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount);

        // 주문 수량만큼 재고가 줄어야 함
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }



    @Test
    @DisplayName("재고 수량 부족 예외가 발생해야 함")
    public void 상품주문_재고수량초과() throws Exception{
        //Given
        Member member = createMember();
        Book item = createBook("JPA", 10000, 10);

        int orderCount = 11;

        //When

        assertThrows(NotEnoughStockException.class ,() ->
            orderService.order(member.getId(), item.getId(), orderCount));
        //Then

    }

    @Test
    public void 주문취소() throws Exception{
        //Given
        Member member = createMember();
        Book item = createBook("JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //When
        orderService.cancelOrder(orderId);
        //Then
        Order getOrder = orderRepository.findById(orderId);

        // 주문 취소시 상태는 CANCEL
        assertThat(getOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
        // 주문이 취소된 상품은 주문수량 만큼 재고가 증가해야 함
        assertThat(item.getStockQuantity()).isEqualTo(10);
    }



    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }

}