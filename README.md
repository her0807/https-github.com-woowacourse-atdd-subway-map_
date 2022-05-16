<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacourse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-map">
</p>

<br>

# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션

## 3단계 기능 요구사항

### 지하철 노선 추가 API 수정
- 노선 추가 시 3가지 정보를 추가로 입력 받음
  - upStationId: 상행 종점
  - downStationId: 하행 종점
  - distance: 두 종점간의 거리
- 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록
- 변경된 API 스펙은 API 문서v2 참고

### 구간 관리 API 구현
- 추가 기능
  - 노선에 구간을 추가
  - 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
  - 구간 제거
- 구간 관리 API 스펙은 API 문서v2 참고

### 구간 등록 기능 요구 사항 상세
- 이미 등록되어 있는 역을 기준으로 새로운 구간을 추가한다.
- 가능한 경우
  - a(7)b(3)c + d(5)a -> d(5)a(7)b(3)c
  - a(7)b(3)c + c(5)d -> d(5)a(7)b(3)c(5)d
  - a(10)b + a(5)c -> a(5)b(5)c
- 불가능한 경우
  - a(7)b(3)c + a(5)b : 상행역과 하행역이 이미 모두 노선에 등록되어 있는 경우 
  - a(7)b(3)c + a(5)c : 상행역과 하행역이 이미 모두 노선에 등록되어 있는 경우
  - a(7)b(3)c + a(7)d : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같은 경우
  - a(7)b(3)c + d(5)e : 상행역과 하행역 둘 중 하나도 포함되어있지 않은 경우
  - a(7)b(3)c + a(5)a : 상행역과 하행역이 같은 경우

### 구간 제거 기능 요구 사항 상세
- 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- 중간역이 제거될 경우 재배치를 함
  - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
  - a(7)b(3)c - b -> a(10)c : 거리는 두 구간의 거리의 합으로 정함
- 불가능한 경우
  - a(5)b - a : 구간이 하나인 노선에서 마지막 구간을 제거하는 경우

<br>

## 🚀 Getting Started
### Usage
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
