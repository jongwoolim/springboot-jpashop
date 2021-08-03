package me.jongwoo.jpashop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.jongwoo.jpashop.domain.*;
import me.jongwoo.jpashop.domain.Order;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static me.jongwoo.jpashop.domain.QMember.member;
import static me.jongwoo.jpashop.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    private JPAQueryFactory jpaQueryFactory;

    public void save(Order order){
        em.persist(order);
    }

    public Order findById(Long id){
        return em.find(Order.class , id);
    }

    public List<Order> findAllByQuerydsl(OrderSearch orderSearch){
        jpaQueryFactory = new JPAQueryFactory(em);
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if(orderSearch.getOrderStatus() != null){
            builder.and(order.orderStatus.eq(orderSearch.getOrderStatus()));
        }

        if(orderSearch.getMemberName() != null){
            builder.and(member.name.eq(orderSearch.getMemberName()));
        }

        List<Order> result = jpaQueryFactory
                                        .select(order)
                                        .from(order)
                                        .innerJoin(order.member, member)
                                        .where(builder)
//                                        .where(usernameEq(orderSearch.getMemberName()), orderStatusEq(orderSearch.getOrderStatus()))
//                                        .where(allEq(orderSearch.getMemberName(), orderSearch.getOrderStatus()))
                                        .fetch();
        return result;
    }

//    private BooleanExpression usernameEq(String usernameCond){
//        return usernameCond != null ? member.name.eq(usernameCond) : null;
//    }
//
//    private BooleanExpression orderStatusEq(OrderStatus orderStatusCond){
//        return orderStatusCond != null ? order.orderStatus.eq(orderStatusCond) : null;
//    }
//
//    private BooleanExpression allEq(String usernameCond, OrderStatus orderStatusCond){
//        return usernameEq(usernameCond).and(orderStatusEq(orderStatusCond));
//    }

    public List<Order> findAllByString(OrderSearch orderSearch){

        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

    }

    /**
     * JPA Criteria
     * @param orderSearch
     * @return
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);

        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }
}
