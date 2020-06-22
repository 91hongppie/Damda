### 최초 실행 시

**python 버전이 다를 경우 최초 한번만 실행**

python 3.7.4 설치 ( 설치 시 path 추가 옵션 선택 )

시스템 환경 변수 -> 사용자 변수 -> Path 에 `~\Python37\Scripts\`, `~\Python37\` 있는지 확인

시스템 환경 변수 -> 시스템 변수 ->  Path 에 다른 버전의 파이썬을 잠시 지운 후 가상환경 구축 후 복구 

```bash
$python -V
3.7.4
# 버전 확인 후 다음 코드 실행
$mkdir ~/python-virtualenv
# 3.7.4이름의 가상환경 생성
$python -m venv ~/python-virtualenv/3.7.4
# 가상환경 실행
$source ~/python-virtualenv/3.7.4/Scripts/activate
# 정상 실행 시 아래와 같이 가상환경 이름이 표시됨
(3.7.4)
$
# 가상환경 종료
$deactivate
```



**clone 등의 이유로 프로젝트 가상환경을 새로 만들 때 실행**

```bash
# 프로젝트 폴더 안
$source ~/python-virtualenv/3.7.4/Scripts/activate
(3.7.4)
# python 3.7.4버전의 이름이 venv인 가상환경 생성
$python -m venv venv
# 가상환경 실행
$source venv/Scripts/activate
(venv)
$
```



**TIP**

```bash
$vi ~/.bashrc
```

```
# bash 단축키 설정 느낌
# 편집기가 열리면 i 또는 insert 누르고 아래의 코드를 입력
alias venv="source ~/python-virtualenv/3.7.4/Scripts/activate"
# 코드 입력 후 esc, :wq 입력 후 엔터
편집기 종료
```

```bash
# venv가 source ~/python-virtualenv/3.7.4/Scripts/activate를 의미
$venv
(3.7.4)
$source venv/Scripts/activate # 이거도 ~/.bashrc에 설정하면 대체 가능
(venv)
$
```



### 필요 라이브러리 설치

```bash
(venv)
$pip install -r requirements.txt
```



### DB 설정

MySQL WorkBench 사용

id : root
password : 개인설정 (damda/settings/local.py 에서 수정 후 사용)

```sql
DB 생성
create database damda DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
```

```bash
# 테이블 생성
(venv)$python manage.py migrate
```



### 회원 로그인 관련 초기 설정

[회원인증 초기 설정](https://lab.ssafy.com/s02-final/s02p31b205/blob/master/damda-django/%ED%9A%8C%EC%9B%90%EC%9D%B8%EC%A6%9D%EC%B4%88%EA%B8%B0%EC%84%A4%EC%A0%95.md)



### 실행

```bash
# 서버 실행
(venv)$python manage.py runserver
```
