# pcms-personal contents management system

# 필요한 라이브러리
1. cms-mvc 를 사용
2. jetty embeded

# web.xml
1. TempDir을 만든다
2. IAuthorityChecker에서 상속받아서 AuthorityChecker 클래스를 만든다.

# 개발방향
* html5, 부트스트랩 사용
* mashup을 최대한 사용
* 폴더명등을 변경
 
# package
kr.co.kalpa 하위에 다음과 같은 package를 만든다.

* config
* controller
* service

# SessionInfo
로그인한 상태에 유지해야할 값들을 갖는 SessionInfo를 작성한다.
public class SessionInfo extends HashMap<String,Object> implements ISessionInfo {
}

# webapp 하위에
common 폴더 생성하고 그 안에 login.jsp, cms_error.jsp를 작성
common 폴더 안에 아래 폴더들을 생성

* css
* js
* images

# controller
