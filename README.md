## CEP(Convenience store Event Products) 편의점 행사상품 조회
<p style="text-align: center">
<span style="font-size: 20px; font-weight: bold;"></span><br><br>
편의점 행사상품을 편의점과 이벤트별로 검색할 수 있고,  즐겨찾기 기능을 사용할 수 있는 웹페이지입니다. <br> 프론트엔드는 React를 기반으로, 백엔드는 Spring Boot를 기반으로 개발되었습니다.
</p>
<br>

---
- **프로젝트 배포 Url** : https://eventprod.store
<br>

- **프론트엔드 Github** : https://github.com/zeroempty2/CEPF
<br>

- **백엔드 GitHub** :  https://github.com/zeroempty2/CEP
<br>

- **api 명세** :  https://docs.google.com/spreadsheets/d/1dO67-8X0fWnsUL8AAtTOKoPh1EhYiT8FYMDhoy7MQqY/edit?gid=0#gid=0
 <br>
 
 ---
 
 ## 기술 스택

- **백엔드**: Java, Spring Boot, QueryDSL, Selenium, jsoup, MySQL, AWS EC2, docker
- **프론트엔드**: JavaScript, CSS, HTML, React

<br>

 ---

## 프로젝트 소개

- 편의점 행사상품을 편의점과 이벤트별로 검색할 수 있고,  즐겨찾기 기능을 사용할 수 있는 웹페이지입니다.  프론트엔드는 React를 기반으로, 백엔드는 Spring Boot를 기반으로 개발되었습니다.
- jsoup과 selenium, chromedriver를 이용하여 편의점 행사상품 페이지에서 데이터를 자동으로 수집해 저장하게 했습니다.
- QueryDSL을 사용하여 동적 쿼리를 처리하고, 페이징 처리를 하였습니다.
- Spring Scheduler를 활용하여 주기적 웹 크롤링 작업 자동화를 하였습니다.
- Spring Scheduler와 SpringBoot Custom Filter를 통해 크롤링 작업을 하고 있는 특정 시간대에는 API접근을 하지 못하게 제한하였습니다.
- React를 사용해 웹 페이지 무한스크롤 구현을 했으며, 모바일 환경의 ui와 작동성을 고려해 구현하였습니다.
- Github Action과 dockerHub를 통한 ci/cd 파이프라인을 구축하였습니다.
- docker를 통한 서비스 분리와 로드밸런싱을 구현했습니다.

  <br>
  
 ---
## 프로젝트 워크플로우
![워크플로우2 drawio](https://github.com/user-attachments/assets/6b0d66f8-5e0e-4153-b619-94f7fb63c855)
