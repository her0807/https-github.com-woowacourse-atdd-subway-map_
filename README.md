# 기능 요구사항

- [x] 지하철 역 삭제
    - [ ] 역이 노선에 포함되어 있을 경우 삭제 불가
  
- [x] 지하철 역 이름 중복 금지
  
- [ ] 지하철 역 이름 끝에 '역' 포함
  
- [ ] 지하철 역 이름 길이 '역' 포함 3 ~ 12 글자 제한

- [ ] 지하철 역 간 거리 & 추가 요금 양수만 가능

- [ ] 지하철 역 간 거리 단위 'km'
  
- [x] 지하철 노선 등록

- [x] 노선 목록 조회
    
- [x] 노선 조회
    - [x] 노선 조회 시 구간에 포함된 역 목록 응답

- [x] 노선 수정

- [x] 노선 삭제

- [x] 노선 이름 중복 금지

- [ ] 노선 역 이름 끝에 '선' 포함

- [ ] 노선 역 이름 길이 '선' 포함 3 ~ 12 글자 제한

- [x] 구간 추가
    - [x] 상행 종점과 하행 종점 같은 역 금지
    - [x] 노선 내 갈래길 형성 금지
  
- [x] 구간 삭제
    - [x] 노선 내 구간이 하나일 경우 삭제 금지
  
- [x] Spring JDBC 의존성 추가
    - [x] LineDao JdbcTemplate 추가
    - [x] StationDao JdbcTemplate 추가

- [x] H2 데이터베이스 설정

- [x] 스프링 빈 활용