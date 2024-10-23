# 오디 🍇
지각하는 친구에게 "너 어디야?"라고 계속해서 물어볼 때가 있습니다.  
"거의 다 왔어"라는 친구의 거짓말이 우리를 지치게 해요.  
계속 물어보기 눈치 보이고, 왠지 모르게 섭섭함이 쌓여갑니다.  
<br>
한 번 상상해 보세요. 오랜만에 만나는 친구와의 약속 날이에요.  
약속 장소에 도착했지만 친구의 연락은 없고, 여러분의 메시지는 ‘읽씹’으로 남아있습니다.  
친구가 잘 오고 있는지 알지 못해 답답합니다. 결국 "너 어디야?"라는 메시지를 보냈어요.  
<br>
이런 상황, 너무나 익숙하지 않나요?  
<br>
“아뿔싸 또 지각이야..” 늦지 않겠다고 다짐해도 마음처럼 되지 않을 때가 있습니다.  
누군가는 꼭 기다리는 상황이 반복됩니다.  
<br>
그래서 우리는 `오디`를 만들었어요.  
`오디`는 여러분이 언제, 어디서, 무엇을 하든 지각하지 않게 도와드려요.  
지각이 빈번한 친구가 제시간에 도착할 수 있게 도와주고,  
매번 기다리는 친구에게는 얼마나 기다려야 할지 정확히 알려줍니다.  
<br>
더 이상 "너 어디야?"라고 물어보지 마세요.  
약속을 더욱 즐겁고 편안하게,  
여러분의 우정을 더욱 돈독하게 만들어 드릴게요.

## **Contributors** 🟣
|AN 차람|AN 해음|AN 올리브|
|:-:|:-:|:-:|
|<img src="https://github.com/user-attachments/assets/6743a06e-785c-4fd1-98d6-69d3fde4ca6f" width=200 height=200>|<img src="https://github.com/user-attachments/assets/a7b12121-a193-4d12-ac86-c3fd383b0125" width=200 height=200>|<img src="https://github.com/user-attachments/assets/ee524838-09af-4241-bf64-aa227510c0b1" width=200 height=200>|
|[@aprilgom](https://github.com/aprilgom)|[@haeum808](https://github.com/haeum808)|[@kimhm0728](https://github.com/kimhm0728)|

<br/>

|BE 카키|BE 조조|BE 콜리|BE 제리|
|:-:|:-:|:-:|:-:|
|<img src="https://github.com/user-attachments/assets/715866d0-6e23-4326-922b-f20d8c34b5fc" width=200 height=200>|<img src="https://github.com/user-attachments/assets/e5ddea0b-3d1d-4767-85e7-058642dbfa1b" width=200 height=200>|<img src="https://github.com/user-attachments/assets/bb0859db-39e5-4102-ae50-19db20fc9d16" width=200 height=200>|<img src="https://github.com/user-attachments/assets/59cf618a-e2dd-4391-aa25-fe882f6b62e0" width=200 height=200>|
|[@hyeon0208](https://github.com/hyeon0208)|[@eun-byeol](https://github.com/eun-byeol)|[@coli-geonwoo](https://github.com/coli-geonwoo)|[@mzeong](https://github.com/mzeong)|

<br/>

## **Google Play** 🌎

[오디 다운받기](http://play.google.com/store/apps/details?id=com.mulberry.ody/)

<br/>

## **커밋 컨벤션** 📜

[오디 팀 커밋 컨벤션](https://sly-face-106.notion.site/6497236e4e8b449ebf132b1b329882e1?pvs=4)

<br/>

## **아키텍쳐 구조** 🏛️

![Group 352](https://github.com/user-attachments/assets/3d60e4b4-95cc-4ae4-b32e-0756bdc0abe8)

<br>

## **폴더 구조** 📁

<details>
  <summary>안드로이드 폴더 구조</summary>
  
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
