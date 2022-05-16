## 피드백 정리

### 지하철 노선도 미션 1,2단계 - 1차 피드백

- [x] 오타
    - 요구사항 정리 오타 (17라인)
    - `LineDaoTest` - beforeEach()
- [x] `DAO`에서 사용하는 JdbcTemplate 불변
- [x] `LineDao` 사용하지 않는 메서드 존재
    - `deleteAll()`
- [x] `LineController`
    - [x] 중복 관련 에러메세지 상세화 - 어떤 속성이 중복되는지 적절한 메세지 전달 필요
    - [x] `updateLine()` - 유효성 검사 세분화
    - [x] <컨트롤러 - 서비스 - 영속성> 레이어 관련 학습
        - 서비스 레이어가 추가되었을 때 프로젝트 구조 변화
        - 컨트롤러의 역할의 변화
    - [x] `@ExceptionHandler` 예외 처리
        - 만약, Exception이 발생한다면?
        - [x] `Exception`에 대한 예외 처리 추가
- [x] `LineAcceptanceTest`
    - [x] 중복된 코드 Fixture 객체 or 메서드 분리
    - [x] `Stream`과 `람다` 활용
    - [x] 존재하지 않는 노선 제거에 대한 테스트
- [x] 테스트 DisplayName에 행위뿐만이 아닌 검증하려는 결과도 표현
    - 기존 방식과의 차이는?
    - 적용해보니 기존의 방식은 테스트하는 메서드의 행위만을 표현했다면 변경한 방식은 그 행위를 통해 받을 수 있는 결과까지 표현할 수 있음.
- [x] `RestAssured`는 어떤 기능을 제공하는 라이브러리일까?
    - REST API를 테스트하기 위한 라이브러리(실제 웹과 동일한 기능 수행)
- [x] `LineDaoTest`
    - [x] `assertAll` 검증 함수 학습 및 적용
        - assertThat과 다르게 패키지 경로가 `junit.jupiter.api~`라는 차이점도 존재
    - [x] 하나의 메서드에서 2개의 테스트를 검증
- [x] 세가지 종류의 테스트, 사용한 목적과 어떤 기능을 사용했는지 작성

### 지하철 노선도 미션 1,2단계 - 2차 피드백

- [x] `RequestBody`의 사용 목적(용도)
    - JSON 데이터를 Spring에서 Object로 변환하여 사용하기 위함.
- [x] `LineAcceptaceTest`
    - [x] when 주석 제거
    - [x] TODO 제거
    - [x] 예외 상태 코드 검증에 메시지 검증 추가하기
- [x] `InjectMock`과 `@Mock`의 차이점
    - mock : 어노테이션을 통해서 Mock 객체를 반환(즉, StationDao 클래스의 mock 객체를 반환)합니다.
    - Injectmocks : 어노테이션이 붙은 클래스의 인스턴스를 만들고 @mock을 통해 생성한 Mock 객체를 주입합니다.
- [x] `JdbcTemplate` 빈으로 등록한 적이 없으나 빈으로 관리되는 이유
    - `spirng-boot-stater-jdbc`에서 주입해주기때문에 자동 Bean 등록 가능
- [ ] 예외 메시지 - 톤 앤 매너
- [x] `ControllerAdvice`

### 지하철 노선도 미션 3단계 - 1차 피드백

- [ ] public 메서드는 테스트 필요
    - 인텔리제이의 test coverage 기능을 이용
- [x] `Section`
    - [x] boolean 타입을 반환하지만 메서드명이 `find`로 시작함 - 네이밍 수정 필요
        - find -> `isSame`
    - [x] `isLongDistance` - 더 길다의 의미를 가질 수 있도록 수정
- [x] `Sections`
    - [x] 역과 Station 단어가 혼용되어 사용됨. 일관성있게 수정 필요
        - "역"이라는 단어로 통일
    - [x] `Sections()` 기본 생성자 사용하는 곳이 있는지
        - 사용되는 곳 없기때문에 제거
    - [x] 변수명에 동사 사용 -> 메서드의 네이밍 컨벤션
    - [x] `findDownSection`은 if문을 통과하여 도달했기에 항상 Optional에 값이 채워져있는지?
        - 위 StationId를 통해 상,하행 지하철역이 있는지 검증하는 코드 + 그 아래 존재하는 if() 2개를 통해 항상 참이라고 생각
        - 하지만 인텔리제이에서는 `isPresent()`없이 사용했다는 메세지 출력
        - `ifPresent`를 통해 메서드 수행하도록 수정
    - [x] `addSplitByUpStation`
        - [x] if 문이 메서드에 있어야 하는지
            - 메서드 내 사용하던 조건문을 `add()` 메서드에서 사용하도록 수정
        - [x] add/split 두가지 동사가 이름에 들어있음을 고려
            - 실질적인 새로운 구간 추가는 if() 문이 끝난 뒤에 적용하기 때문에 `split`만 사용하여 메서드 이름 구성
        - [x] `stream()`에서 바로 get을 한다면 발생하는 문제
            - Optional.get() 시 비어있다면 `NoSuchElementException`발생할 수 있따.
            - 조건문에서 한번 검증 필요
    - [x] `removeWayPointSection`
        - 함수명에 대한 고민 필요, 내부에서는 제거뿐만 아니라 추가도함.
            - 두 구간을 합치는 의미로 `merge`를 사용하여 함수명 변경
            - `mergeUpAndDownSection`
- [x] `SectionsTest`
    - [x] 상행, 하행 종점 `StationId`의 상수화
    - [x] `hasSection()` 테스트에만 사용되기 때문에 테스트 클래스에서 private으로 사용 권장
    - [x] 오타 수정
    - [x] 코드를 보는 사람이 이해할 수 있도록 변수명 수정
- [x] `LineService`
    - [x] 원자성과 트랜잭션 학습
        - 메서드가 진행되다가 예기치 못한 상황으로 애플리케이션이 종료된다면?
    - [x] `Section`에 대한 책임을 분리
    - [x] 변수 이름에 자료형을 사용하면 좋지 않은 이유 (originSectionList)
        - 만약, 자료형을 변경하게되는 경우가 발생한다면 변수명을 변경해야하는 번거로움이 발생한다.
    - [x] `deleteSection()`에 대한 테스트
    - [x] 메서드명 수정
        - [x] 인자를 통한 의미 중복 제거
            - `~~byLineRequest` 제거 : 인자를 통해 알 수 있음.
        - [x] 메서드의 기능을 나타낼 수 있는 이름으로 수정
        - [x] Dao와 같이 프로젝트의 구조가 메서드에 드러나는 부분 수정

### 지하철 노선도 미션 3단계

- [x] 미사용 변수 제거 - 불필요한 모킹 제거
- [x] `LineService` - `showLine()`
    - Transactional, readOnly 옵션 학습
- [x] `SectionServiceTest` - 테스트 구조화 및 가독성
    - describe - context - it 패턴 적용 [참고 링크](https://johngrib.github.io/wiki/junit5-nested/)
        - describe : 설명할 테스트 대상
        - context : 테스트 대상이 놓인 상황 설명
        - it : 테스트 대상의 행동을 설명
- [x] `SectionsTest`
    - 상수화 필요한 변수 수정
