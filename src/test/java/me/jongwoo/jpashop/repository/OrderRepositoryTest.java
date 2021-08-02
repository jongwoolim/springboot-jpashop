package me.jongwoo.jpashop.repository;

import me.jongwoo.jpashop.domain.*;
import me.jongwoo.jpashop.domain.item.Book;
import me.jongwoo.jpashop.domain.item.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class OrderRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    @Rollback(false)
    public void querydslTest() throws Exception{
        //Given
        createOrder();
        OrderSearch orderSearch = new OrderSearch();
//        orderSearch.setMemberName("member 2");
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // When
        final List<Order> result = orderRepository.findAllByQuerydsl(orderSearch);

        //Then
        assertThat(result.size()).isEqualTo(10);
//        assertThat(result.get(0).getOrderStatus()).isEqualTo(OrderStatus.ORDER);
//        assertThat(result.get(0).getTotalPrice()).isEqualTo(30000);
    }

    private void createOrder(){
        IntStream.rangeClosed(0, 20).forEach(idx -> {
            Member member = createMember(idx);
            Book book = createBook("jpa" + idx, 10000, 50);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            int orderCount = idx + 1;
            // 주문상품 생성
            OrderItem orderItem = OrderItem.createOrderItem(book, book.getPrice(), orderCount);

            // 주문 생성
            Order order = Order.createOrder(member, delivery, orderItem);
            //When
            orderRepository.save(order);

            if(idx % 2 == 0){
                final Order savedOrder = orderRepository.findById(order.getId());
                savedOrder.cancel();
            }
        });
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember(int idx) {
        Member member = new Member();
        member.setName("member " + idx);
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }
}