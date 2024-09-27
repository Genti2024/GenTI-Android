<p align="left"><img src="https://github.com/user-attachments/assets/15e61bb6-dfdc-4d56-a7ed-af05f21144b8" height=110></p>

# Genti
```
내 마음대로 표현하는, 하나뿐인 AI 사진 제작 서비스
```

<br>

## TEAM _ 안드로이드 단독 개발
![2024-08-01_00-28-32](https://github.com/user-attachments/assets/5179534b-09d6-428d-aa67-92afd1163cc7)

<br>

## SOLUTIONS
![2024-08-01_00-34-24](https://github.com/user-attachments/assets/649fb66d-6788-4d6c-a6de-f4b673f9623b)
![2024-08-01_00-34-43](https://github.com/user-attachments/assets/897c4340-d6d9-4f1a-b9bd-ff4c69675864)

<br>

## ACHIEVEMENT
- 2024 정주영 창업경진대회 (아산나눔재단) 사업실행팀 선발
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
- 버전 분기처리를 통해 PhotoPicker와 기존 갤러리 파일 탐색기를 활용해 유저가 사진을 선택할 수 있도록 구현하는 경험 ✔️
- 여러개의 사진을 업로드하는 과정을 coroutine을 통해 병렬로 비동기적 수행하도록 마이그레이션하여, 사진 전송 시간을 단축시키는 경험 ✔️

<br>

## MODULE & PACKAGE CONVENTION
```

🗃️app
 ┗ 📂di

🗃️buildSrc

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

