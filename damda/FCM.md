# Firebase Cloud Messaging

## 앱 등록

![image-20200524014657784](FCM.assets/image-20200524014657784.png)

- Tools - Firebase

![image-20200524014955045](FCM.assets/image-20200524014955045.png)

- Cloud Messaging - Set up Firebase Cloud Messaging
  - 순서대로 하면 앱 등록 완료
  - 은비까비들 구글 계정으로 로그인하면 프로젝트 만들어둬서 그걸로 등록하면 됨

## 오류나면

1. build.gradle에 있는 firebase가 들어가는 부분을 다 지우고 sync now
2. 탐색기에서 damda/app/ 안에 있는 google-services.json 삭제
3. 안드로이드 스튜디오 껐다가 다시 켜기
4. 다시 앱 등록해보기



# 푸시 테스트

- 앱 설치(AVD는 느려서 폰으로 하는 걸 추천)
- 파이어베이스 콘솔 들어가서 왼쪽에 `성장` - `Cloud Messaging`
- `새 알림` 누르고 내용 입력 후 `다음`
- 대상은 앱으로 설정하고 내려서 `검토`

## 장고에서는



