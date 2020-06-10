# 코틀린

## 기본 문법

### var, val

```kotlin
/*
val => 읽기전용, 값 할당 1회만 가능, 초기화 필요, 나중에 할당 X
var => 값 할당 여러번 가능
*/

var name: string
name = "kotlin"
```



### 문자열 템플릿

```kotlin
val name = "name"
val str = "hello $name. Your name has ${name.length} characters"
```

- `$` 또는 `${}` 통해서 문자열에서 바로 사용할 수 있음

### 범위

```kotlin
val aToz = "a".."z"
val isTrue = "c" in aToz
```

- `..`을 통해서 범위를 나타낼 수 있음
- `aToz.step()`로 간격 조정
- `aToz.reversed()`로 순서 뒤집기 가능

### 널 문법

```kotlin
var str: string? = null
```

- 코틀린에서 선언은 `var str: string`과 같은 형식으로 가능
- 이때 변수에 `null` 값이 들어갈 수 있다면 뒤에 `?`를 붙여서 표현

### when

```kotlin
fun whatNumber(x: Int) {
    when (x) {
        0 -> println("x is zero")
        1 -> println("x is 1")
        else -> println("x is neither 0 nor 1")
    }
}
```

- when 문법에서 마지막 `else`는 무조건 작성해야함

### 참조 동등성, 구조 동등성

#### 참조 동등성

```kotlin
val a = File("/exam.doc")
val b = File("/exam.doc")
val sameRef = a === b
```

- `a === b` 값은 `false`
- 같은 파일을 참조하지만 각각 별개의 File 객체 인스턴스

```kotlin
var a: Int = 10000
var b: Int = 10000
println(a === b) // true
println(a == b)  // true

// 자바 문법으로 변환시 Int -> int 형으로 Int? -> Integer (오브젝트) 형으로 변환되기 때문
var a: Int = 10000
var b: Int? = 10000
println(a === b) // false
println(a == b)  // true
```



#### 구조 동등성

```kotlin
val a = File("/exam.doc")
val b = File("/exam.doc")
val sameRef = a == b
```

- `a == b` 값은 `true`
- 두 객체가 같은 값을 가졌는지 확인



