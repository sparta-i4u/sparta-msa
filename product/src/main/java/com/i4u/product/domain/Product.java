package com.i4u.product.domain;


import com.i4u.common.entity.Basic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;
import com.i4u.product.application.dto.request.ProductUpdateRequest;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@Table(name="p_product")
@Entity
@SQLRestriction("is_deleted IS NULL OR is_deleted = false")
//Basic을 extends 받아서 is_deleted 상속받음
public class Product extends Basic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "company_id")
    private UUID companyId;

    private String name;

    private Integer price;

    private String content;

    public Product(final UUID hubId, final UUID companyId, final String name, final Integer price , final String content) {
        this.hubId = hubId;
        this.companyId = companyId;
        this.name =  name;
        this.content = content;
        this.price = price;
        this.isDeleted = false;
    }

    //상품 수정 함수
    public void update(final ProductUpdateRequest newProduct) {
        this.name = newProduct.name();
        this.content = newProduct.content();
        this.price = newProduct.price();
    }

    //상품 여러개 삭제 함수
    public void softDelete() {
        this.isDeleted = true;
    }
}
