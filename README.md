# 3M-B2B Introduce
## 물류 관리 및 배송 시스템을 위한 MSA 기반 플랫폼

<br>

## 우리 팀
|정민수|박준혁|이수연|정아현|
|:----:|:----:|:----:|:----:|
|[@dbp-jack](https://github.com/dbp-jack)|[@sall6550](https://github.com/sall6550)|[@greenblueredgreen](https://github.com/greenblueredgreen)|[@azuressu](https://github.com/azuressu)|
|사용자, 게이트웨이, 인프라|허브, 메시지, AI|주문, 배송, 배송 담당자|업체, 상품|


### ⇒ [팀 노션 바로가기](https://www.notion.so/teamsparta/16-I-4-U-1b12dc3ef51480a1b684c3c0a8489ad0)
### ⇒ [프로젝트 노션 바로가기](https://www.notion.so/teamsparta/I4U-MMM-1c02dc3ef5148075843af1bd8b3314bf)

<br>

## Architecture

![](https://velog.velcdn.com/images/azuressu/post/325b3775-558f-45a8-954a-8694188b9f64/image.png)

<br>

## ERD
![](https://velog.velcdn.com/images/azuressu/post/0460bd91-621b-44a5-869e-6e1069171d86/image.png)

<br>

## 개발 환경
`Java 17` `Spring Boot 3.4.3` `QueryDSL 5.0.0` `Spring Cloud 2024.0.0`


<br>

## 기술과 도구
<div style="display: flex; justify-content: center;">
  <img src="https://img.shields.io/badge/Java-007396?&style=flat&logo=java&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?&style=flat&logo=springboot&logoColor=white" style="margin-right: 10px;">
 <img src="https://img.shields.io/badge/Spring Security-6DB33F?&style=flat&logo=springsecurity&logoColor=white" style="margin-right: 10px;">
    <img src="https://img.shields.io/badge/ApachetTomcat-F8DC75?style=flat&logo=apachetomcat&logoColor=white"style="margin-right: 10px;"/>
    <img src="https://img.shields.io/badge/Json Web Tokens-000000?style=flat&logo=jsonwebtokens&logoColor=white"style="margin-right: 10px;"/>
</div>
  
<div style="display: flex; justify-content: center;">
    <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white" style="margin-right: 10px;"/>
    <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=Postgresql&logoColor=white" style="margin-right: 10px;"/>
    <img src="https://img.shields.io/badge/Redis-FF4438?style=flat&logo=redis&logoColor=white" style="margin-right: 10px;"/>
    <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white" style="margin-right: 10px;"/>
</div>
  
<div style="display: flex; justify-content: center;"> 
    <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white" style="margin-right: 10px;">
    <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white" style="margin-right: 10px;">
    <img src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white" style="margin-right: 10px;">
    <img src="https://img.shields.io/badge/Google%20Gemini-8E75B2?style=flat&logo=googlegemini&logoColor=white" style="margin-right: 10px;">
</div>
  
  
<div style="display: flex; justify-content: center;">  
  <img src="https://img.shields.io/badge/IntelliJ Idea-000000?style=flat&logo=intellijidea&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Jira Software-0052CC?style=flat&logo=jirasoftware&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Slack-4A154B?style=flat&logo=slack&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Excalidraw-6965DB?style=flat&logo=excalidraw&logoColor=white" style="margin-right: 10px;">
</div>

<br>


## 프로젝트 기능 (서비스 구성 및 실행 방법)
### 사용자
<details>
<summary>유저 정보 관리 및 조회 기능</summary>
<div markdown="1">

- **목적**: 유저 정보 관리 및 조회 기능 제공

- **기능**:
  - Slack ID와 User ID로 유저 정보를 조회
  - Redis 캐시를 활용하여 자주 조회되는 유저 정보를 빠르게 반환
  - PostgreSQL에 유저 정보 저장

- **서비스 동작**:
  - **빠른 응답 속도**: Redis 캐시를 사용하여 유저 정보가 빠르게 조회됩니다. 자주 조회되는 유저 정보는 Redis에 저장되므로 응답 시간이 짧습니다.
  - **성능 최적화**: 유저 정보 조회 시, Redis 캐시를 통해 DB 접근을 최소화하여 시스템의 성능을 최적화합니다.
  - **서비스 확장성**: 유저 정보를 PostgreSQL에 저장하고, 캐시를 활용하므로 시스템의 확장성이 뛰어납니다.

</div>
</details>

<details>
<summary>사용자 인증 및 JWT 토큰 발급</summary>
<div markdown="1">

- **목적**: 사용자 인증 및 JWT 토큰 발급

- **기능**:
  - 회원가입 및 로그인 처리
  - JWT를 사용하여 인증 처리
  - PostgreSQL에 인증 정보 저장

- **서비스 동작**:
  - **높은 성능과 낮은 지연 시간**: JWT 토큰을 사용하여 인증 과정을 최적화하고, Redis 캐시를 통해 실시간 인증이 가능하므로 빠르고 효율적인 인증 처리가 가능합니다.
  - **무상태(stateless) 인증**: JWT를 통해 서버 측에서 세션 정보를 관리하지 않아도 되므로 시스템의 부하를 줄이고 확장성이 높습니다.

</div>
</details>

<details>
<summary>API Gateway - 모든 요청을 처리 및 인증 및 라우팅 담당</summary>
<div markdown="1">

- **목적**:  API Gateway 역할을 하여 모든 요청을 처리하고, 인증 및 라우팅을 담당

- **기능**:
  - API Gateway로서 인증 및 요청 라우팅
  - Eureka Server와 연결하여 서비스 디스커버리 및 API 라우팅
  - JWT 인증을 처리하고, user-service와 auth-service로 라우팅

- **서비스 동작**:
  - **서비스 통합**: API Gateway는 클라이언트의 요청을 적절한 서비스로 라우팅하므로, 각 서비스가 독립적으로 동작할 수 있도록 돕습니다. 이를 통해 서비스 간 연결이 효율적이고 관리가 용이합니다.
  - **서비스 디스커버리**: Eureka Server와 연결되어 동적으로 서비스 목록을 가져오고 라우팅할 수 있으므로, 서비스의 변경이나 배포에 유연하게 대응할 수 있습니다.
  - **인증 및 보안**: JWT 인증을 처리하여, 보안이 강화되며, 유저의 인증 상태를 관리하고 API 접근을 제어할 수 있습니다.

</div>
</details>



### 허브

<details>
<summary>허브 관리 시스템</summary>
<div markdown="1">

  - 초기에 17개의 허브와 24개의 허브간 연결이 되어있습니다.
  - 허브가 각 인접한 허브와 연결되어있고 다익스트라 알고리즘을 통해 최단경로를 탐색합니다.

</div>
</details>

### 메시지, AI
<details>
<summary>메시지 및 AI</summary>
<div markdown="1">

  - 슬랙 API를 통해 메시지를 전달합니다.
  - 고객 요청사항을 보고 AI를 통해 허브에서 발송해야할 시간을 계산합니다.
  - AI 와 슬랙 API 를 활용해서 허브 담당자한테 최종 발송 시한을 자동으로 알려줍니다.

</div>
</details>

### 업체, 상품
<details>
<summary>업체</summary>
<div markdown="1">

  - 업체 생성, 삭제, 수정, 업체 전체조회, 업체 이름 검색 기능 구현
  - 로그인 한 사용자의 권한이 일치하고 본인 담당이 맞는지 확인
  - 요청한 업체가 실제 허브에 속하는지 확인
  
</div>
</details>

<details>
<summary>상품</summary>
<div markdown="1">

  - 상품 생성, 삭제, 수정, 상품 전체조회, 상품 이름 검색 기능 구현
  - 로그인한 사용자의 권한이 일치하고 본인 담당이 맞는지 확인
  - 요청한 상품이 실제 해당 허브에 일치하는지

</div>
</details>



### 주문, 배송, 배송 담당자
<details>
<summary>주문</summary>
<div markdown="1">

  - 사용자는 원하는 상품을 주문할 수 있습니다.
  - 이후, 해당 주문에 대한 수정, 삭제, 조회, 검색 등이 가능합니다.
  - 주문 상태에 따라, 주문 삭제 및 수정 여부가 달라집니다.
  - 주문이 정상 생성됨에 따라 배송 생성 요청으로 전달됩니다.

</div>
</details>

<details>
<summary>배송</summary>
<div markdown="1">

  - 사용자로부터 주문 요청이 들어오면 배송 생성이 바로 진행됩니다.
  - 이후, 배송에 대한 수정, 삭제, 조회, 검색 등이 가능합니다.
  - 배송 상태에 따라, 주문 삭제 및 수정 여부가 달라집니다.
  - 배송이 정상 생성됨에 따라 메시지 생성 요청으로 전달됩니다.
  
</div>
</details>

<details>
<summary>배송 담당자</summary>
<div markdown="1">

  - 허브/업체에 따른 배송 담당자 생성이 가능합니다.
  - 이후, 배송 담당자에 대한 수정, 삭제, 조회, 검색 등이 가능합니다.
  - 배송 담당자는 고유한 배송 순서를 지닙니다.
  
</div>
</details>


<br>

## 적용 기술
◻️  MSA
>  유지 보수, 분업 효율, 확장성 경험을 위해 MSA 구조를 선택했습니다

◻️ QueryDSL
> 정렬, 검색어 등에 따른 동적 쿼리 작성을 위하여 QueryDSL 도입하여 활용했습니다.

◻️ Swagger
> 팀원들과의 원활한 소통을 위하여 스웨거를 도입하여 적용하였습니다.

◻️ AI / SlackAPI 연동
> 주문 정보와 최종 발송시한 전송을 위해 AI 와 Slack API를 통합하여 메시지를 전달했습니다.

◻️ Redis
> 연속된 요청으로 인한 DB병목을 해소하고 RefreshToken 등 소멸기간이 존재하는 데이터의 TimeToLive 관리를 용이하게 할 수 있도록 Redis를 도입하였습니다.

◻️ RabbitMQ
> 마이크로서비스 간 비동기 메시지 처리와 안정적인 이벤트 라우팅을 위해 RabbitMQ를 활용했습니다.

<br>

## Discussion

- MSA 프로젝트 초기 구조 [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BDiscussion%5D%5B%EC%A0%84%EC%B2%B4%E2%80%90%EC%A0%84%EC%B2%B4-%EB%8F%84%EB%A9%94%EC%9D%B8%5D-MSA-Project-%EC%B4%88%EA%B8%B0-%EA%B5%AC%EC%A1%B0)

- MSA 설계에 따른 역할 분리 [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BDiscussion%5D%5B%EB%AF%BC%EC%88%98%E2%80%90User,-Auth,-Gateway-%EB%8F%84%EB%A9%94%EC%9D%B8%5D-MSA%EC%84%A4%EA%B3%84%EC%97%90-%EB%94%B0%EB%A5%B8-%EC%97%AD%ED%95%A0%EB%B6%84%EB%A6%AC)

- 프로젝트 관리 및 협업 (JIRA) [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BDiscussion%5D%5B%EC%A0%84%EC%B2%B4%E2%80%90%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%5D-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B4%80%EB%A6%AC-%EB%B0%8F-%ED%98%91%EC%97%85-%E2%80%90-JIRA)

- Docker Compose 기반 인프라 구성 [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BDiscussion%5D%5B%EB%AF%BC%EC%88%98%E2%80%90%EC%A0%84%EC%B2%B4-%EB%8F%84%EB%A9%94%EC%9D%B8%5D-%F0%9F%90%B3-Docker-Compose-%EA%B8%B0%EB%B0%98-%EC%9D%B8%ED%94%84%EB%9D%BC-%EA%B5%AC%EC%84%B1)

<br>

## Trouble Shooting

- MSA Project 초기 구조 구성 문제 [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BTrouble-Shooting%5D%5B%EC%A0%84%EC%B2%B4%E2%80%90%EC%A0%84%EC%B2%B4-%EB%8F%84%EB%A9%94%EC%9D%B8%5D-MSA-Project-%EC%B4%88%EA%B8%B0-%EA%B5%AC%EC%A1%B0-%EA%B5%AC%EC%84%B1-%EB%AC%B8%EC%A0%9C)

- 메시지 브로커 적용하기 [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BTrouble-Shooting%5D%5B%EC%88%98%EC%97%B0%E2%80%90Order,-Delivery-%EB%8F%84%EB%A9%94%EC%9D%B8%5D-%EB%A9%94%EC%8B%9C%EC%A7%80-%EB%B8%8C%EB%A1%9C%EC%BB%A4-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0)

- Hub 데이터 초기 삽입 문제 [→ WIKI 보기](https://github.com/sparta-i4u/sparta-msa/wiki/%5BTrouble-Shooting%5D%5B%EC%A4%80%ED%98%81%E2%80%90Hub-%EB%8F%84%EB%A9%94%EC%9D%B8%5D%C2%A0hub-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%B4%88%EA%B8%B0-%EC%82%BD%EC%9E%85-%EB%AC%B8%EC%A0%9C)

<br>

## API 명세서
[API 명세서](https://www.notion.so/teamsparta/API-1b22dc3ef5148002b6ceccd511db7b16)

<br>



