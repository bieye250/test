## First Commit
2021/1/27
## Java子类遮盖与覆盖
2021/11/27
```java
class A{
  int i = 1;
  static j = 2;
  void a(){
    System.out.println(i);
    System.out.println(j);
  }
}
class B extends A{
  int i = 2;
  static j = 3;
  void a(){
    System.out.println(i);
    System.out.println(j);
  }
  main(){
    B b = new B();
    b.a(); // i = 2, j = 3;
    printf(b.i,b.j) // i = 2, j = 3
    A a = b;
    a.a(); // i = 2, j = 3
    printf(a.i,a.j) // i = 1, j = 2 字段的遮盖，方法的覆盖
  }
}
```
