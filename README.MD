## 프로젝트명 : Mutsa-SNS

프로젝트 기간 : 2022.12.20 ~ 2023.01.10

### ✔ 주요 기능

- 로그인, 회원가입(JWT, Spring Security)
- 게시글 CRUD
- 댓글 CRUD
- 좋아요 기능
- 알림 기능

### ✔ 기술 스택

- backend : java, spring boot, JPA, spring security
- Database : MYSQL
- CI/CD : gitlab

### ✔ ERD

<img src="./ERD.png">

### ✔ Endpoint

- [Swagger](http://ec2-54-180-83-38.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html#/)

| Category | Method | URL                                 | Description       | note                      |
|----------|--------|-------------------------------------|-------------------|---------------------------|
| 회원       | POST   | /users/join                         | 회원가입              | 권한 미필요                    |
|          | POST   | /users/login                        | 로그인               | 권한 미필요                    |
| 게시글      | GET    | /posts                              | 전체 게시글 조회         | 페이징(최신순 정렬), 권한 미필요       |
|          | GET    | /posts/my                           | 자신이 작성한 게시글 목록 조회 | 페이징(최신순 정렬), 권한 필요        |
|          | GET    | /posts/{postId}                     | 게시글 상세 조회         | 권한 미필요                    |
|          | POST   | /posts                              | 게시글 등록            | 권한 필요                     |
|          | PUT    | /posts/{postId}                     | 게시글 수정            | 권한 필요                     |
|          | DELETE | /posts/{postId}                     | 게시글 삭제            | 게시글 삭제 시 댓글,좋아요 삭제, 권한 필요 |
| 댓글       | GET    | /posts/{postId}/comments            | 해당 게시글의 전체 댓글 조회  | 페이징(최신순 정렬), 권한 미필요       |
|          | POST   | /posts{postId}/comments/{commentId} | 댓글 등록             | 권한 필요                     |
|          | PUT    | /posts{postId}/comments/{commentId} | 댓글 수정             | 권한 필요                     |
|          | DELETE | /posts{postId}/comments/{commentId} | 댓글 삭제             | 권한 필요                     |
| 좋아요      | GET    | /posts/{postId}/likes               | 좋아요 개수 조회         | 권한 미필요                    |
|          | POST   | /posts/{postId}/likes               | 좋아요 누르기           | 권한 필요                     |
|          | DELETE | /posts/{postId}/likes               | 좋아요 취소            | 권한 필요                     |
| 알림       | GET    | /alarm                              | 알림 조회             | 페이징(최신순 정렬), 권한 필요        |

### ✔ 체크리스트
- [x] Gitlab CI & Crontab CD
- [x] Swagger
- [x] 회원가입
- [x] 로그인
- [x] 포스트 작성, 수정, 삭제, 리스트
- [x] 댓글 작성, 수정, 삭제, 리스트
- [x] 좋아요 누르기, 조회
- [x] 마이피드 리스트
- [x] 알림 리스트
- [x] Swagger에 ApiOperation 적용
- [x] Test Code 작성
- [ ] Admin기능
- [ ] UI구현

### ✔ 2주차 미션 요약

**[접근 방법]**

- CI & CD, Spring Security, JWT 부분은 수업 자료를 참고하여 작성하였다.
- 최대한 주어진 요구사항에 맞추어 작성하려고 노력하였다. 

**[특이사항]**

- 아쉬운 점
1. 1주 차 미션을 참여하지 못해 2주 차 미션과 1주 차 미션을 병행하느라 도전과제(UI,ADMIN)를 하지 못한 것이 아쉬웠다.
2. 테스트 코드 짜는데 많은 시간을 소요하였고 paging 및 리스트 조회 테스트 코드는 작성하지 못하였다.


- 추가하고 싶은 점
1. ADMIN 기능
2. 1대1 채팅 기능
3. 화면 UI
