package com.i4u.hub.application.service;

import com.i4u.hub.application.dtos.hubConnection.CreateHubConnectionReqDto;
import com.i4u.hub.application.dtos.hubConnection.HubConnectionListResDto;
import com.i4u.hub.application.dtos.hubConnection.HubConnectionResDto;
import com.i4u.hub.application.dtos.hubConnection.UpdateHubConnectionReqDto;
import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.model.HubConnection;
import com.i4u.hub.domain.repository.HubConnectionRepository;
import com.i4u.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HubConnectionService {

    private final HubRepository hubRepository;
    private final HubConnectionRepository hubConnectionRepository;

    /**
     * 출발 허브에서 도착 허브까지의 최소 이동 시간을 다익스트라 알고리즘으로 계산
     *
     * @param departureHubName 출발 허브
     * @param arrivalHubName 도착 허브
     * @return 최소 이동 시간(분) 및 경로, 경로가 없으면 null 반환
     */
    public PathResult findShortestTimePath(String departureHubName, String arrivalHubName) {
        // 허브 이름으로 허브 엔티티 조회
        Hub departureHub = hubRepository.findByHubName(departureHubName)
                .orElseThrow(() -> new IllegalArgumentException("출발 허브를 찾을 수 없��니다: " + departureHubName));

        Hub arrivalHub = hubRepository.findByHubName(arrivalHubName)
                .orElseThrow(() -> new IllegalArgumentException("도착 허브를 찾을 수 없습니다: " + arrivalHubName));


        // 모든 허브 연결 정보 가져오기
        List<HubConnection> connections = hubConnectionRepository.findAll();

        // 그래프 구성 (인접 리스트)
        Map<UUID, List<Edge>> graph = buildGraph(connections);

        // 다익스트라 알고리즘 실행
        return dijkstra(graph, departureHub.getHubId(), arrivalHub.getHubId());
    }

    private Map<UUID, List<Edge>> buildGraph(List<HubConnection> connections) {
        Map<UUID, List<Edge>> graph = new HashMap<>();

        for (HubConnection connection : connections) {
            UUID from = connection.getDepartureHub().getHubId();
            UUID to = connection.getArrivalHub().getHubId();
            int time = connection.getHubToHubTime();

            // 양방향 그래프로 구성
            graph.computeIfAbsent(from, k -> new ArrayList<>())
                 .add(new Edge(to, time));
            graph.computeIfAbsent(to, k -> new ArrayList<>())
                 .add(new Edge(from, time));
        }

        return graph;
    }

    private PathResult dijkstra(Map<UUID, List<Edge>> graph, UUID start, UUID end) {
        // 최소 힙을 사용한 우선순위 큐
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::getTime));
        // 각 노드까지의 최단 거리
        Map<UUID, Integer> distances = new HashMap<>();
        // 경로 추적을 위한 맵
        Map<UUID, UUID> previous = new HashMap<>();

        // 모든 노드의 초기 거리는 무한대
        for (UUID node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        // 시작 노드의 거리는 0
        distances.put(start, 0);
        pq.offer(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            UUID currentId = current.getId();
            int currentTime = current.getTime();

            // 현재 노드가 이미 더 짧은 경로로 처리되었다면 스킵
            if (currentTime > distances.get(currentId)) {
                continue;
            }

            // 목적지에 도달했다면 종료
            if (currentId.equals(end)) {
                break;
            }

            // 인접한 노드들 처리
            if (graph.containsKey(currentId)) {
                for (Edge edge : graph.get(currentId)) {
                    UUID neighborId = edge.getTo();
                    int newTime = currentTime + edge.getTime();

                    // 더 짧은 경로를 찾았다면 갱신
                    if (newTime < distances.getOrDefault(neighborId, Integer.MAX_VALUE)) {
                        distances.put(neighborId, newTime);
                        previous.put(neighborId, currentId);
                        pq.offer(new Node(neighborId, newTime));
                    }
                }
            }
        }

        // 경로가 없는 경우
        if (!distances.containsKey(end) || distances.get(end) == Integer.MAX_VALUE) {
            return null;
        }

        // 경로 구성
        List<UUID> path = new ArrayList<>();
        UUID current = end;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return new PathResult(distances.get(end), path);
    }

    public HubConnectionResDto createHubConnection(CreateHubConnectionReqDto createHubConnectionReqDto) {
        HubConnection savedHubConnection = hubConnectionRepository.save(createHubConnectionReqDto.toEntity());

        return HubConnectionResDto.from(savedHubConnection);
    }

    public HubConnectionResDto getHubConnection(UUID hubConnectionId) {
        HubConnection hubConnection = hubConnectionRepository.findById(hubConnectionId)
                .orElseThrow(() -> new IllegalArgumentException("허브 이동정보를 찾을 수 없습니다."));

        return HubConnectionResDto.from(hubConnection);
    }

    public HubConnectionListResDto getHubConnections() {
        List<HubConnection> hubConnections = hubConnectionRepository.findAll();

        return HubConnectionListResDto.from(hubConnections);
    }

    public HubConnectionResDto updateHubConnection(UUID hubConnectionId, UpdateHubConnectionReqDto updateHubConnectionReqDto) {
        HubConnection hubConnection = hubConnectionRepository.findById(hubConnectionId)
                .orElseThrow(() -> new IllegalArgumentException("허브 이동정보를 찾을 수 없습니다."));

        hubConnection.update(updateHubConnectionReqDto);
        HubConnection updatedHubConnection = hubConnectionRepository.save(hubConnection);

        return HubConnectionResDto.from(updatedHubConnection);
    }

    public void deleteHubConnection(UUID hubConnectionId) {
        HubConnection hubConnection = hubConnectionRepository.findById(hubConnectionId)
                .orElseThrow(() -> new IllegalArgumentException("허브 이동정보를 찾을 수 없습니다."));

        hubConnectionRepository.delete(hubConnection);
    }

    // 내부 클래스: 그래프 간선
    private static class Edge {
        private final UUID to;
        private final int time;

        public Edge(UUID to, int time) {
            this.to = to;
            this.time = time;
        }

        public UUID getTo() {
            return to;
        }

        public int getTime() {
            return time;
        }
    }

    // 내부 클래스: 다익스트라 알고리즘용 노드
    private static class Node {
        private final UUID id;
        private final int time;

        public Node(UUID id, int time) {
            this.id = id;
            this.time = time;
        }

        public UUID getId() {
            return id;
        }

        public int getTime() {
            return time;
        }
    }

    // 결과 클래스: 최단 경로와 소요 시간
    public static class PathResult {
        private final int totalTime;
        private final List<UUID> path;

        public PathResult(int totalTime, List<UUID> path) {
            this.totalTime = totalTime;
            this.path = path;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public List<UUID> getPath() {
            return path;
        }
    }
}