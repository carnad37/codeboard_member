# STUDY

### 구조
* spring boot + webflux + r2dbc + reactive redis 사용
* 인증서버를 따로 쓰지 않을거라서 해당 모듈처리 극대화 목표
* DB는 현재 Mariadb로 테스트. 
* 다른 db호환도 가능하나, 다만 r2dbc 구현체가 존재하는 db만 가능.

### Webflux
* Webflux 자체가 아직 사용자가 많지 않은 기술.
* 기존의 webflux + jpa 등의 구조로는 mvc + jpa와 성능이 그닥 크게 차이안남.
* 이에 DB와의 연결도, blocking I/O 이 아닌 reactive programing으로 이루워지게 구성.
* 이론적으로는 성능의 극대화가 가능할걸로 보임.
* 다만 reactor(Mono, Flux)를 기본으로하는데, 생각보다 흐름을 코드로 구현하기 어려웠다.

### Webflux Spring Security
* reference : https://github.com/eriknyk/webflux-jwt-security-demo/tree/master/src/main/java/com/github/eriknyk/webfluxjwtsecurity/configuration/security
* 기존의 MVC srping security와는 구조가 좀 다름.
* @EnableWebFluxSecurity 설정 bean의경우 해당 어노테이션 필요.
* 내부에 ServerHttpSecurityConfiguration.class, WebFluxSecurityConfiguration.class, ReactiveOAuth2ClientImportSelector.class가 Import되어있음.

### 메모
* contextWrite 의 역할 확인하기.

: chatGPT 말대로라면 contextWrite는 Subscriber에 전달되는 context값을 수정하는데 이용됨.
contextWrite로 인증정보를 Filter에 추가해주게되면 차후에 로그인상태여부를 체크할시, ReactorSecurityContextHolder에서
현재 유저 정보를 가져오는게 가능해진다.

* WebFilter를 이용해 구현하는방법과 AuthenticationWebFilter를 생성해 구현하는 방법 비교.

: 전자의 WebFilter를 이용하게되면 구현은 간단하나, 설정에서 접근권한을 설정한 url들을 한번더
체크해줘야하는 번거로움이 생긴다. 전자의 경우는 SrpingSecurity의 설정을 가져가면서도 간단하게 구현이
가능하나 구조에대한 이해를 조금더 필요로한다.
