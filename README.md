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

# 요구사항 정리
- 지하철 역
  - [x] 지하철 등록 API를 구현한다.
    - [x] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답한다.
  - [x] 지하철역 목록 조회 API를 구현한다.
  - [x] 지하철역 삭제 API를 구현한다.
    - [x] 해당 역이 포함된 구간이 있을 경우, 예외코드를 반환한다.

- 지하철 노선
  - [x] 지하철 노선 등록 API를 구현한다.
    - [x] 같은 이름의 노선은 생성 불가하다.
    - [x] 노선 추가 시 상행 종점, 하행 종점, 두 종점간의 거리를 입력받는다.
    - [x] 노선 등록과 동시에 종점 사이의 구간 정보도 함께 등록한다.
    - [x] 예외) 상행, 하행의 역이 같으면 안된다.
    - [x] 예외) 상행, 하행 역이 존재하여야 한다.
    - [x] 예외) 거리가 0보다 커야 한다.
  - [x] 지하철 노선 목록 API를 구현한다.
  - [x] 지하철 노선 조회 API를 구현한다.
    - [x] 노선 정보와 노선에 포함된 구 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 반환한다.
  - [x] 지하철 노선 수정 API를 구현한다.
  - [x] 지하철 노선 삭제 API를 구현한다.

- 지하철 구간
  - DAO
    - [x] 노선 저장 기능을 구현한다.
    - [x] 노선 검색 기능을 구현한다.
    - [x] 노선 업데이트 기능을 구현한다.
    - [x] 노선에 해당하는 구간을 모두 삭제하하는 기능을 구현한다.
  - [x] 지하철 구간 등록 API를 구현한다.
    - [x] 새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.
    - [x] 노선에는 갈래길이 허용되지 않아 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경한다. (다음 역이 존재할 경우, 기준 역과 다음역 사이에 새로운 역을 위치시킨다.)
    - [x] 예외) 역 사이에 새로운 역을 등록할 때 기존 역 사이의 길이보다 크거나 같으면 등록을 할 수 없다.
    - [x] 예외) 요청의 상행역과 하행역이 모두 노선에 등록되어 있으면 추가할 수 없다.
    - [x] 예외) 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.
  - [x] 지하철 구간 제거 API를 구현한다.
    - [x] 노선에 존재하지 않는 역은 삭제할 수 없다.
    - [x] 종점이 제거될 경우 다음 역이 종점이 된다.
    - [x] 중간역이 제거될 경우, 양 옆의 역을 연결하며 재배치해야한다.
    - [x] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.
    

- End to End 테스트 작성한다.

> [API 문서](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#Line)

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
