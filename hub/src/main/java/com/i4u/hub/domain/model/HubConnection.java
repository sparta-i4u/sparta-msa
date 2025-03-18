package com.i4u.hub.domain.model;

import com.i4u.hub.application.dtos.hubConnection.UpdateHubConnectionReqDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_hub_connection")
public class HubConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID hub_connection_id;

    @Column(nullable = false)
    private UUID departure_hub_id;

    @Column(nullable = false)
    private UUID arrival_hub_id;

    // 허브간 이동시간 분으로 처리 예: 150 (2시간 30분)
    @Column(nullable = false)
    private Integer hub_to_hub_time;

    // 단위: km
    @Column(nullable = false)
    private Integer distance;

    public void update(UpdateHubConnectionReqDto dto) {
        if (dto.getHubToHubTime() != null) {
            this.hub_to_hub_time = dto.getHubToHubTime();
        }
        if (dto.getDistance() != null) {
            this.distance = dto.getDistance();
        }
    }
}
