package com.i4u.hub.application.service;

import com.i4u.hub.application.dtos.CreateHubReqDto;
import com.i4u.hub.application.dtos.HubDetailResDto;
import com.i4u.hub.application.dtos.HubListResDto;
import com.i4u.hub.application.dtos.UpdateHubReqDto;
import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.repository.HubRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    public HubDetailResDto createHub(CreateHubReqDto hubReqDto) {
        Hub savedHub = hubRepository.save(hubReqDto.toEntity());
        return HubDetailResDto.from(savedHub);
    }

    public HubDetailResDto getHub(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        return HubDetailResDto.from(hub);
    }

    public HubListResDto getHubs() {
        List<Hub> hubs = hubRepository.findAll();

        return HubListResDto.from(hubs);
    }

    @Transactional
    public HubDetailResDto updateHub(UUID hubId, UpdateHubReqDto hubReqDto) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));

        hub.update(hubReqDto);
        Hub updatehub = hubRepository.save(hub);

        return HubDetailResDto.from(updatehub);
    }

    public void deleteHub(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        hubRepository.delete(hub);
    }

}
