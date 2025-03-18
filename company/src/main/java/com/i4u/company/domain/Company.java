package com.i4u.company.domain;

import com.i4u.common.entity.Basic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    private String type;

    private String owner;

    private String address;

    private String number;

    public Company(UUID hubId, String name,String type, String owner, String address, String number) {
        this.hubId = hubId;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.address = address;
        this.number = number;
        this.isDeleted = false;
    }
}
