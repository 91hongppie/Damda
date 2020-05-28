# 심화 PJT

## 팀소개

**팀명** : 은비까비들 (B205)

**팀장** : 조선행

**팀원** : 박태수, 송지영, 장은비, 조규홍



## 프로젝트 소개

**주제** : AI를 활용한 가족 사진첩

**프로젝트 명** : 담다



### 주차별 자료 정리

* [1주차](https://lab.ssafy.com/s02-final/s02p31b205/tree/develop/1%EC%A3%BC) : 서비스 기획 자료
* [2주차](https://lab.ssafy.com/s02-final/s02p31b205/tree/develop/2%EC%A3%BC) : 기획 및 코틀린 자료



### WBS

```mermaid
gantt
    title 담다, Damda
    dateFormat  YYYY-MM-DD #바꾸지 않음 
    section 앨범
   	앨범 목록	:done, 2020-05-08, 7d  #완료되면 done을 기입 
    사진 상세	:done, 2020-05-11, 5d
    앨범 사진 조회	:done, 2020-05-11, 10d
    전체 사진 조회	:done, 2020-05-18, 5d 
    사진 공유	:done, 2020-05-18, 5d 
    앨범 / 사진 내려받기	:done, 2020-05-20, 7d 
    앨범 / 사진 삭제	:done, 2020-05-20, 7d 
    사진 이동	: 2020-05-31, 10d
    앨범 생성, 사진 추가	: 2020-06-03, 7d 
    section 회원관리
    이메일	:done, 2020-05-08, 5d
    카카오	:done, 2020-05-11, 7d
   	가족 만들기	:done, 2020-05-13, 10d
   	가족 구성원 추가	:done, 2020-05-18, 7d
   	요청 수락 / 거절	:done, 2020-05-18, 7d
   	
    section 업로드
    수동 업로드	: 2020-05-11, 10d
    자동 업로드	: 2020-05-25, 10d
  
    section 푸시
    디바이스 등록	:done, 2020-05-20, 6d
    푸시 전송	:done, 2020-05-08, 14d
    푸시 자동 전송	: 2020-05-27, 5d
    푸시 온오프	: 2020-05-30, 7d
    
    section 영상
    영상 업로드	: 2020-05-27, 10d
   	영상 추가 / 삭제	: 2020-05-27, 10d
   	
   	section 웹
   	로그인, 회원가입	:2020-05-30, 10d
   	앨범, 사진 목록 페이지	: 2020-05-30, 10d
   	영상 페이지	: 2020-05-30, 10d
   	개인 정보 페이지지	: 2020-05-30, 10d
   
  	section UCC
  	UCC 촬영 / 편집 : 2020-06-05, 6d
```



### android

* [담다](https://lab.ssafy.com/s02-final/s02p31b205/tree/develop/damda) : 안드로이드 프로젝트 ( 주 서비스 )



### Server

* [back](https://lab.ssafy.com/s02-final/s02p31b205/tree/develop/damda-django) : Django REST API



### 서비스 홈페이지

* [front](https://lab.ssafy.com/s02-final/s02p31b205/tree/develop/damda-vue) : 서비스 사용법, 개발자 소개 등을 위한 홈페이지