package com.i4u.hub.domain.model;

import com.i4u.common.entity.Basic;
import com.i4u.hub.application.dtos.hubConnection.UpdateHubConnectionReqDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLRestriction("is_deleted IS NULL OR is_deleted = false")
@Table(name = "p_hub_connection")
public class HubConnection extends Basic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID hubConnectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Hub departureHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Hub arrivalHub;

    // 허브간 이동시간 분으로 처리 예: 150 (2시간 30분)
    @Column(nullable = false)
    private Integer hubToHubTime;

    // 단위: km
    @Column(nullable = false)
    private Integer distance;

    public void update(UpdateHubConnectionReqDto dto) {
        if (dto.getHubToHubTime() != null) {
            this.hubToHubTime = dto.getHubToHubTime();
        }
        if (dto.getDistance() != null) {
            this.distance = dto.getDistance();
        }
    }
}
