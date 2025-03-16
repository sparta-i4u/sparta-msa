package com.i4u.hub.domain.model;

import com.i4u.common.entity.Basic;
import com.i4u.hub.application.dtos.UpdateHubReqDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_hub")
public class Hub extends Basic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID hubId;

    @Column(nullable = false)
    private String hubName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public void update(UpdateHubReqDto dto) {
        if (dto.getHubName() != null) {
            this.hubName = dto.getHubName();
        }
        if (dto.getAddress() != null) {
            this.address = dto.getAddress();
        }
        if (dto.getLatitude() != null) {
            this.latitude = dto.getLatitude();
        }
        if (dto.getLongitude() != null) {
            this.longitude = dto.getLongitude();
        }
    }
}
