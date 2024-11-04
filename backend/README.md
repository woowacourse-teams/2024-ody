# 오디 백엔드 팀 소개

<br>

## **Contributors** 🙋🏻‍

|                                                     카키(서현준)                                                      |                                                     조조(조은별)                                                      |                                                     콜리(김건우)                                                      |                                                     제리(김민정)                                                      |
|:----------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/715866d0-6e23-4326-922b-f20d8c34b5fc" width=200 height=200> | <img src="https://github.com/user-attachments/assets/e5ddea0b-3d1d-4767-85e7-058642dbfa1b" width=200 height=200> | <img src="https://github.com/user-attachments/assets/bb0859db-39e5-4102-ae50-19db20fc9d16" width=200 height=200> | <img src="https://github.com/user-attachments/assets/59cf618a-e2dd-4391-aa25-fe882f6b62e0" width=200 height=200> |
|                                    [@hyeon0208](https://github.com/hyeon0208)                                    |                                    [@eun-byeol](https://github.com/eun-byeol)                                    |                                 [@coli-geonwoo](https://github.com/coli-geonwoo)                                 |                                       [@mzeong](https://github.com/mzeong)                                       |

<br>

## **기술 스택** ⚙️

<img src="https://github.com/user-attachments/assets/1dfc2d88-b059-4d69-b38f-d7679d39befc" width="800" height="auto">

> #### [기술 스택 선정 이유 보러가기](https://sly-face-106.notion.site/26c77083f28a4634a88ef3f4e9dbcea0?pvs=4)


<br>

## **ERD** 📈

![image](https://github.com/user-attachments/assets/032e659e-bfc5-4477-b343-22ecc25df2b9)


<br>

## **아키텍처 구조** 🏗️

![image](https://github.com/user-attachments/assets/aa50092b-1616-4ce0-a712-d21989914aea)

<br>

## 패키지 구조 🗂
<details>
<summary> 펼치기 </summary>
<div markdown="1">

```angular2html
backend
├── java
│   └── com
│       └── ody
│           ├── auth
│           │   ├── config
│           │   ├── controller
│           │   ├── domain
│           │   ├── dto
│           │   │   ├── request
│           │   │   └── response
│           │   ├── service
│           │   └── jwtToken
│           ├── common
│           │   ├── annotation
│           │   ├── aop
│           │   ├── argumentresolver
│           │   ├── config
│           │   ├── domain
│           │   ├── exception
│           │   ├── filter
│           │   ├── interceptor
│           │   │   └── dto
│           │   ├── mapper
│           │   └── validator
│           ├── eta
│           │   ├── domain
│           │   ├── dto
│           │   │   ├── request
│           │   │   └── response
│           │   ├── repository
│           │   └── service
│           ├── mate
│           │   ├── controller
│           │   ├── domain
│           │   ├── dto
│           │   │   ├── request
│           │   │   └── response
│           │   ├── repository
│           │   └── service
│           ├── meeting
│           │   ├── controller
│           │   ├── domain
│           │   ├── dto
│           │   │   ├── request
│           │   │   └── response
│           │   ├── repository
│           │   └── service
│           ├── member
│           │   ├── controller
│           │   ├── domain
│           │   ├── repository
│           │   └── service
│           ├── notification
│           │   ├── config
│           │   ├── domain
│           │   │   └── message
│           │   ├── dto
│           │   │   └── response
│           │   ├── repository
│           │   └── service
│           ├── route
│           │   ├── config
│           │   ├── controller
│           │   ├── domain
│           │   ├── dto
│           │   ├── mapper
│           │   ├── repository
│           │   └── service
│           ├── swagger
│           │   ├── annotation
│           │   └── config
│           └── util
└── resources
    ├── db
    │   └── migration
    ├── static
    │   ├── css
    │   └── js
    └── templates
```

</div>
</details>

<br>

## **코드 컨벤션** 📃
- [코드 컨벤션 보러가기](https://sly-face-106.notion.site/0bd7a08f43fa4cb7821bd3392ec3ce5b?pvs=73)
