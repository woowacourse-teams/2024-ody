# 🍇오디🍇 - 여러분이 언제, 어디서, 무엇을 하든 지각하지 않게 도와드려요. 
<img src="https://github.com/user-attachments/assets/dab4f935-2c6a-4c36-95e4-a678cdf9b57d" width="960"/>

<br>
<br>

## 핵심 기능 💫
<img src="https://github.com/user-attachments/assets/a72736bc-2e50-427d-861b-fe22a3dfebf4" width="240"/>
<img src="https://github.com/user-attachments/assets/cd869d4c-4764-41e9-ad8b-3f1567223aa9" width="240"/>
<img src="https://github.com/user-attachments/assets/d87d1bec-51e7-406f-b659-1abb05d471ed" width="240"/>
<img src="https://github.com/user-attachments/assets/3a65ed17-36fb-429a-bd31-4a7f68fbe4ac" width="240"/>

<br>
<br>

## **Google Play** 🌎

[오디 다운받기](http://play.google.com/store/apps/details?id=com.mulberry.ody)

<br>

## **Contributors** 🟣

|AN 차람|AN 해음|AN 올리브|
|:-:|:-:|:-:|
|<img src="https://github.com/user-attachments/assets/6743a06e-785c-4fd1-98d6-69d3fde4ca6f" width=200 height=200>|<img src="https://github.com/user-attachments/assets/a7b12121-a193-4d12-ac86-c3fd383b0125" width=200 height=200>|<img src="https://github.com/user-attachments/assets/ee524838-09af-4241-bf64-aa227510c0b1" width=200 height=200>|
|[@aprilgom](https://github.com/aprilgom)|[@haeum808](https://github.com/haeum808)|[@kimhm0728](https://github.com/kimhm0728)|

<br>

|BE 카키|BE 조조|BE 콜리|BE 제리|
|:-:|:-:|:-:|:-:|
|<img src="https://github.com/user-attachments/assets/715866d0-6e23-4326-922b-f20d8c34b5fc" width=200 height=200>|<img src="https://github.com/user-attachments/assets/e5ddea0b-3d1d-4767-85e7-058642dbfa1b" width=200 height=200>|<img src="https://github.com/user-attachments/assets/bb0859db-39e5-4102-ae50-19db20fc9d16" width=200 height=200>|<img src="https://github.com/user-attachments/assets/59cf618a-e2dd-4391-aa25-fe882f6b62e0" width=200 height=200>|
|[@hyeon0208](https://github.com/hyeon0208)|[@eun-byeol](https://github.com/eun-byeol)|[@coli-geonwoo](https://github.com/coli-geonwoo)|[@mzeong](https://github.com/mzeong)|

<br>

## 팀 문화 🧑‍🤝‍🧑
- [주간 회고](https://github.com/woowacourse-teams/2024-ody/discussions)
  - 매주 KPT(Keep, Problem, Try) 회고를 통해 팀 문화를 되돌아보며 발전시키고 있어요.
- [3분 오디톡](https://sly-face-106.notion.site/5269de0e949b4a7fa5eb7351b69597a9?pvs=74)
  - 새로운 기술을 도입하거나 복잡한 설계를 할 때, 이를 팀원들과 적극적으로 공유하여 모든 팀원이 같은 방향의 코드를 바라볼 수 있도록 노력하고 있어요.
- [팀 문화 자세히 보러가기](https://sly-face-106.notion.site/5e4ee4452a8f4f9e850ba9366bded06c)

<br>

## **커밋 컨벤션** 📜

[오디 팀 커밋 컨벤션](https://sly-face-106.notion.site/6497236e4e8b449ebf132b1b329882e1?pvs=4)

<br/>

## 안드로이드 🤖
### 기술 스택 🧑‍💻
| 구분           |                                                                |
|---------------|----------------------------------------------------------------|
| Launguage     | Kotlin, XML                                                    |
| Architecture  | MVVM, Clean Architecture                                       |
| Network       | Retrofit, OkHttp                                               |
| Serialization | Moshi                                                          |
| Async         | Coroutines, Flow                                               |
| Jetpack       | ViewModel, DataBinding, ViewPager2, SplashScreen, AlarmManager |
| Local DB      | Room, DataStore                                                |
| DI            | Hilt                                                           |
| Firebase      | FCM, Analytics, Crashlytics, Storage                           |
| Test          | Junit5, Junit4, Espresso, AssertJ                              |
| Third-Party   | dotsindicator, Timber                                          |
| Kakao         | Login, Message, Address                                        |
| GPS           | Google Service                                                 |
| CI/CD         | Github Actions                                                 |

<br>

### **아키텍처 구조** 🏛️

<img src="https://github.com/user-attachments/assets/3d60e4b4-95cc-4ae4-b32e-0756bdc0abe8" width="860"/>

<br>

### **패키지 구조** 📁

<details>
  <summary>안드로이드 패키지 구조</summary>
  
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
</details>

<br>

### 코드 컨벤션 ⌨️
- [코드 컨벤션 보러가기](https://sly-face-106.notion.site/180abc739e634b42ad33381f2780c8e0)

<br>

### 역할 분담 🧩
- [역할 분담 보러가기](https://sly-face-106.notion.site/0d91aa0c3c6b4fb68a158c2c3840e1f6)

