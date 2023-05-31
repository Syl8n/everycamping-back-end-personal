# Everycamping back-end

캠핑용품 전문 이커머스 플랫폼입니다.

기획서: <https://everycamping.notion.site/159734775ae24628a143f5ad564245e4>
사이트: <https://everycamping.netlify.app/>

비용 문제로 백엔드 서버 배포를 중단하였습니다.

## 구현 기능

- 회원가입 (로컬, 소셜) 및  로그인
- 상품 추가/수정/삭제/조회(단일)/검색(목록)
- 리뷰 추가/수정/삭제
- 장바구니 추가/수정/삭제
- 주문 및 정산 처리
- 고객 문의 채팅

User case를 간략화 하고 기술의 다양한 사용에 중점을 두었습니다.
이를 위해 실 구현 시 외부 서비스를 사용하게 될 배송, 결제 등은 생략했습니다.

## 경험 및 성과

담당하지 않았던 파트는 생략합니다.

### 회원가입 및 로그인

프록시 및 전략 패턴을 사용해 jwt를 이용한 유저 인증/인가 로직 간략화
- 인터페이스: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/auth/service/CustomUserDetailsService.java)
- 프록시 구현체: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/auth/service/CustomUserDetailsServiceImpl.java)
- 인증/인가 클래스: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/auth/provider/JwtAuthenticationProvider.java)

구매자, 판매자, 관리자로 회원 종류가 3개로 나뉘었고, DB 테이블도 이렇게 나뉘었기에 jwt 내의 role을 확인하고 적절한 서비스를 불러올 필요가 있었습니다.
이를 위해 모든 서비스를 의존성으로 추가하고 조건문으로 그 중 하나를 실행하게 하려고 하였지만, 중복되는 조건문이 너무 많아지고 회원 클래스 추가 시에 업데이트 할 곳이 너무 많았습니다.
그래서 전략 패턴을 사용해 인터페이스를 대신 의존하게 하였는데, 사용할 구현체를 직접 지정해줘야 하는 문제가 남았습니다.
그러려면 필터에서 서비스의 의존성을 받아둬야 했는데, 레이어 구조상 좋지 않다고 생각이 들어 프록시 패턴을 추가 도입했습니다.

### 상품 검색

ES (Elastic Search) 사용으로 텍스트 기반 검색의 조회 시간을 90% 감소
- ES 쿼리 빌더: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/product/repository/ProductSearchQueryRepository.java)
- Document: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/product/entity/ProductDocument.java)
- mapping & setting: [링크](https://github.com/Syl8n/everycamping-back-end-personal/tree/develop/src/main/resources/elastic)

400만 row, m2.micro 환경의 db에서 '% 검색어 %' 형식의 텍스트 기반 검색 시 11초가 걸렸습니다.
원인은 앞에 와일드카드를 포함하는 텍스트 기반 검색 시 적절한 인덱스를 타지 못하고 풀 테이블 서치를 하기 때문이었습니다.
sql 튜닝을 시도해봤지만 worst case에는 별 효과가 없었기에 n-gram 알고리즘을 사용해야한다는 판단을 했습니다.
그 중 가장 유명한 것이 ES였기에 프로젝트에 도입하였고, 검색 시 응답을 1초까지 줄였습니다.

### 회원가입 시 인증 메일 발송

비동기 처리로 응답 시간을 3초 → 500ms 미만으로 감소
- 메일 클라이언트: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/mail/MailClient.java)

이메일 전송 시도 시 외부 서비스에 요청을 하고 스레드가 대기했다가 외부 서비스의 응답을 받고나서 프론트엔드에 응답을 주는 문제가 있었습니다.
이 때 외부 서비스에서 오는 응답과 프론트엔드에 보내는 응답 간의 연관 관계가 없게끔 설계를 했기 때문에 굳이 기다릴 필요가 없다고 판단했습니다.
그래서 메일 전송 요청은 비동기로 처리하고 전송 요청을 했다는 응답은 바로 보내는 방식으로 변경 했습니다.

### 소셜 로그인 구현

구매자 카카오 로그인 구현
- 커스텀 라이브러리: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/domain/auth/service/OAuth2UserService.java)
- 구매자 로그인 컨트롤러: [링크](https://github.com/Syl8n/everycamping-back-end-personal/blob/develop/src/main/java/com/zerobase/everycampingbackend/web/controller/CustomerController.java)

OAuth2Client 프레임워크를 사용하려 했으나 사이드 이펙트가 너무 많아서 OAuth2 인증 방식 분석 후 라이브러리를 직접 제작했습니다.
컨트롤러에서는 소셜ID 인증/인가 확인 후, 잘 처리가 되었다면 사용자 정보를 이용해 로그인 처리를 하고 jwt를 발급합니다.
