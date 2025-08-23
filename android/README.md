# 오디 안드로이드 팀 소개
## **Contributors** 🟣

|차람(김형석)|해음(이준경)|올리브(김혜민)|
|:-:|:-:|:-:|
|<img src="https://github.com/user-attachments/assets/6743a06e-785c-4fd1-98d6-69d3fde4ca6f" width=200 height=200>|<img src="https://github.com/user-attachments/assets/a7b12121-a193-4d12-ac86-c3fd383b0125" width=200 height=200>|<img src="https://github.com/user-attachments/assets/ee524838-09af-4241-bf64-aa227510c0b1" width=200 height=200>|
|[@aprilgom](https://github.com/aprilgom)|[@haeum808](https://github.com/haeum808)|[@kimhm0728](https://github.com/kimhm0728)|

<br>

## 기술 스택 🧑‍💻
| 구분            |                                      |
|---------------|--------------------------------------|
| Launguage     | Kotlin                               |
| Architecture  | MVVM, Clean Architecture             |
| Network       | Retrofit, OkHttp                     |
| Serialization | Kotlinx Serialization                |
| Async         | Coroutines, Flow                     |
| Jetpack       | Compose, ViewModel, SplashScreen     |
| Local DB      | Room, DataStore                      |
| DI            | Hilt                                 |
| Image         | Coil                                 |
| Firebase      | FCM, Analytics, Crashlytics, Storage |
| Test          | Junit5, Junit4, AssertJ              |
| Third-Party   | Timber                               |
| Kakao         | Login, Message, Address              |
| GPS           | Google Service                       |
| CI/CD         | Github Actions                       |

<br>

## **아키텍처 구조** 🏛️

<img src="https://github.com/user-attachments/assets/3d60e4b4-95cc-4ae4-b32e-0756bdc0abe8" width="860"/>

<br>
<br>

## **패키지 구조** 📁
```
android
├─ com.mulberry.ody
  ├─ data
  │  ├─ local
  │  ├─ remote
  │  │  ├─ core
  │  │  └─ thirdparty
  │  │     ├─ address
  │  │     ├─ fcm
  │  │     ├─ image
  │  │     └─ login
  │  └─ retrofit
  ├─ di
  ├─ domain
  │  ├─ apiresult
  │  ├─ model
  │  ├─ repository
  └─ presentation
     ├─ address
     ├─ creation
     │  ├─ date
     │  ├─ destination
     │  ├─ name
     │  └─ time
     ├─ invitecode
     ├─ join
     │  ├─ complete
     ├─ login
     ├─ meetings
     ├─ notification
     ├─ room
     │  ├─ etadashboard
     │  └─ log
     ├─ setting
     │  └─ withdrawal
     └─ splash
```

<br>

## 코드 컨벤션 ⌨️
- [코드 컨벤션 보러가기](https://sly-face-106.notion.site/180abc739e634b42ad33381f2780c8e0?pvs=4)

<br>

## 개발 기여 현황 🧩
- [개발 기여 현황 보러가기](https://sly-face-106.notion.site/0d91aa0c3c6b4fb68a158c2c3840e1f6)
