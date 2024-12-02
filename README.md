<p align="left"><img src="https://github.com/user-attachments/assets/15e61bb6-dfdc-4d56-a7ed-af05f21144b8" height=110></p>

# Genti
```
내 마음대로 표현하는, 하나뿐인 AI 사진 제작 서비스
```

<br>

## TEAM
![2024-08-01_00-28-32](https://github.com/user-attachments/assets/64bb2fa3-cd67-430a-ae53-ff20c76d53b7)

### Android Contributor

[![contributors](https://contrib.rocks/image?repo=Genti2024/Genti-Android)](https://github.com/Genti2024/Genti-Android/contributors)

<br>

## PRODUCT
![Group 8245](https://github.com/user-attachments/assets/6616126f-8be8-4011-afc8-d312145cd5e9)

<br>

## SOLUTIONS
![Group 8250](https://github.com/user-attachments/assets/c96de744-a45f-4512-9896-cd4c6b278ecb)
![Group 8251](https://github.com/user-attachments/assets/45c89b6e-6c74-498d-b498-aaa1bad0484f)
![Group 8252](https://github.com/user-attachments/assets/27210a73-d53c-479c-be24-bc9fe4c5a060)

<br>

## PROGRESS
![Group 8249](https://github.com/user-attachments/assets/30ab4785-68f0-4490-8bbe-a2b0a9a269e6)
![Group 8248](https://github.com/user-attachments/assets/a78918f8-14e5-4853-b0f2-806e880d086a)
![Group 8247](https://github.com/user-attachments/assets/cf32392b-c7f1-4966-9439-ffe150bdc4d0)

<br>

## ACHIEVEMENT
- 2024 정주영 창업경진대회 (아산나눔재단) 사업실행팀 선발 및 본상 수상
- 2024 고려대 KU 창업동아리 아이디어 트랙 선정
- 2024 동국대 아이디어 사업화 지원사업 선발
- 2024 KUCT 딥테크 스타트업 프론티어 선발전 도전상 수상
- 2024 K-Digital Challenge AI 스타트업 창업 경진대회 장려상 수상

<br>

## TECH STACK
- Android App Architecture
- Multi-Module
- Hilt
- Coroutine & Flow
- Data Binding
- Timber,  Coil,  Lottie
- Amplitude
- Firebase Cloud Messaging
- Kakao Open API
- AWS S3 with Presigned Url
- Fragment Navigation, PhotoPicker, FileProvider, RenderEffect

<br>

## EXPERIENCE

- AWS S3 Presigned Url를 활용하며 직접 클라우드에 이미지를 전송하는 기능을 도입하는 경험 ✔️
- 기존 buildSrc를 build-logic으로 전환하고 버전 카탈로그를 적용하여 의존성 관리의 효율성과 가독성을 개선하는 경험 ✔️
- Google Play 결제 라이브러리 v7로 인앱결제를 구현하는 경험 ✔️
- Github Actions를 활용해 Firebase App Distribution으로 앱을 자동 배포하는 CI/CD를 구현하는 경험 ✔️
- 버전 분기처리를 통해 PhotoPicker와 기존 갤러리 파일 탐색기를 활용해 유저가 사진을 선택할 수 있도록 구현하는 경험 ✔️
- 여러개의 사진을 업로드하는 과정을 coroutine을 통해 병렬로 비동기적 수행하도록 마이그레이션하여, 사진 전송 시간을 단축시키는 경험 ✔️
- FileProvider와 cacheDirectory를 활용해 카메라로 직접 찍은 사진을 업로드하도록 구현하는 경험 ✔️

<br>

## MODULE & PACKAGE CONVENTION
```

🗃️app
 ┗ 📂di

🗃️build-logic

🗃️core
 ┣ 📂base
 ┗ 📂extension

🗃️data
 ┣ 📂dto
 ┃ ┣ 📂response
 ┃ ┣ 📂request
 ┣ 📂datasource
 ┣ 📂datasourceImpl
 ┣ 📂interceptor
 ┣ 📂local
 ┣ 📂repositoryImpl
 ┗ 📂service

🗃️domain
 ┣ 📂entity
 ┃ ┣ 📂response
 ┃ ┣ 📂request
 ┗ 📂repository

🗃️presentation
 ┗ 📂기능 별 패키징

```

<br>

