## 프로젝트명 : 멋사 SNS

프로젝트 기간 : 2022.12.19 ~ 2023.01.10

### ✔ 주요 기능

- 로그인, 회원가입(JWT, Spring Security)
- 게시글 및 댓글 CURD
- 마이 피드 기능
- 좋아요 기능
- 알림 기능

### ✔ 기술 스택

- backend : java, spring boot, JPA, spring security
- Database : MYSQL
- CI/CD : gitlab

### ✔ ERD

<img src="./ERD.png">

### ✔ API 기능 명세서

- [Swagger](http://ec2-54-180-83-38.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html#/)
- 공통 URL : http://ec2-54-180-83-38.ap-northeast-2.compute.amazonaws.com:8080/api/v1

| Category | Method | URL                                 | Description       | note                |
|----------|--------|-------------------------------------|-------------------|---------------------|
| 회원       | POST   | /users/join                         | 회원가입              | 권한 미필요              |
|          | POST   | /users/login                        | 로그인               | 권한 미필요              |
| 게시글      | GET    | /posts                              | 전체 게시글 조회         | 페이징(최신순 정렬), 권한 미필요 |
|          | GET    | /posts/{postId}                     | 게시글 상세 조회         | 권한 미필요              |
|          | POST   | /posts                              | 게시글 등록            | 권한 필요               |
|          | PUT    | /posts/{postId}                     | 게시글 수정            | 권한 필요               |
|          | DELETE | /posts/{postId}                     | 게시글 삭제            | 권한 필요               |
|          | GET    | /posts/my                           | 자신이 작성한 게시글 목록 조회 | 권한 필요               |
| 댓글       | POST   | /posts/{postId}/comments            | 해당 게시글의 전체 댓글 조회  | 페이징(최신순 정렬), 권한 미필요 |
|          | POST   | /posts{postId}/comments/{commentId} | 댓글 등록             | 권한 필요               |
|          | PUT    | /posts{postId}/comments/{commentId} | 댓글 수정             | 권한 필요               |
|          | DELETE | /posts{postId}/comments/{commentId} | 댓글 삭제             | 권한 필요               |
| 좋아요      | GET    | /posts/{postId}/likes               | 좋아요 개수 조회         | 권한 미필요              |
|          | POST   | /posts/{postId}/likes               | 좋아요 누르기           | 권한 필요               |
|          | DELETE | /posts/{postId}/likes               | 좋아요 취소            | 권한 필요               |
| 알림       | GET    | /alarm                              | 알림 조회             | 페이징(최신순 정렬), 권한 필요  |

### 미션 요구사항 분석 & 체크리스트

---

매 주 제공되는 미션 별 요구사항을 기반으로 기능에 대한 분석을 진행한 후, 아래와 같은 체크리스트를 작성합니다.

- ‘어떻게 개발을 진행 할 것인지에 대한 방향성’을 확인하는 과정이기 때문에 최대한 깊이있게 분석 후 진행해주시기 바랍니다.

### 2주차 미션 요약

---

**[접근 방법]**

체크리스트를 중심으로 각각의 기능을 구현하기 위해 어떤 생각을 했는지 정리합니다.

- 무엇에 중점을 두고 구현하였는지, 어떤 공식문서나 예제를 참고하여 개발하였는지 뿐만 아니라 미션을 진행하기 전 개인적으로 실습한 것도 포함하여 작성해주시기 바랍니다.
- 실제 개발 과정에서 목표하던 바가 무엇이었는지 작성해주시기 바랍니다.
- 구현 과정에 따라 어떤 결과물이 나오게 되었는지 최대한 상세하게 작성해주시기 바랍니다.

**[특이사항]**

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다.

- 추후 리팩토링 시, 어떤 부분을 추가적으로 진행하고 싶은지에 대해 구체적으로 작성해주시기 바랍니다.

  **참고: [Refactoring]**

    - Refactoring 시 주로 다루어야 할 이슈들에 대해 리스팅합니다.
    - 1차 리팩토링은 기능 개발을 종료한 후, 스스로 코드를 다시 천천히 읽어보면서 진행합니다.
    - 2차 리팩토링은 피어리뷰를 통해 전달받은 다양한 의견과 피드백을 조율하여 진행합니다.
