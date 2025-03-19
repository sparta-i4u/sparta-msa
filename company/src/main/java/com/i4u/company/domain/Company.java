package com.i4u.company.domain;

import com.i4u.common.entity.Basic;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_company")
@NoArgsConstructor
public class Company extends Basic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @Column(name = "hub_id")
    private UUID hubId;

    private String name;
    
    //업체 타입 - 생산업체, 수령업체
    @Enumerated(EnumType.STRING)
    private CompanyType type;

    private String owner;

    private String address;

    private String number;

    public Company(UUID hubId, String name,CompanyType type, String owner, String address, String number) {
        this.hubId = hubId;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.address = address;
        this.number = number;
        this.isDeleted = false;
    }

    //상품 수정 함수
    public void update(final CompanyUpdateRequest newCompany) {
        this.name = newCompany.name();
        this.type = newCompany.type();
        this.owner = newCompany.owner();
        this.address = newCompany.address();
        this.number = newCompany.number();
    }

}
