package me.jongwoo.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;
import me.jongwoo.jpashop.domain.Category;
import me.jongwoo.jpashop.exception.NotEnoughStockException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * stock 증가
     * @param quantity
     */
    /* 비지니스 로직 */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     * @param quantity
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0)
            throw new NotEnoughStockException("need more stock");

        this.stockQuantity = restStock;
    }
}
