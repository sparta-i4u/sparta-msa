package com.i4u.product.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;
import com.i4u.product.application.dto.request.ProductUpdateRequest;

@Getter
@NoArgsConstructor
@Table(name="p_product")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "company_id")
    private UUID companyId;

    @Embedded
    private String name;

    private Integer price;

    @Embedded
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
