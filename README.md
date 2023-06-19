# 동시성 제어
---

## Single Thread
```
  @DisplayName("싱글쓰레드")
  @Test
  @Transactional
  void decreaseStockInSingle() {
    // given
    Long itemDefaultIdx = 3L;
    // when

    itemService.decreaseDefault(itemDefaultIdx);
    // then
    ItemDefault itemDefault = itemDefaultRepository.findByItemDefaultIdx(itemDefaultIdx);
    System.out.println("싱글 쓰레드 수량"+" "+itemDefault.getItemDefaultStock());
  }
```

- 일반적인 싱글쓰레드일 경우 동시성 문제가 없이 제대로 출력됨.


## Multi Thread
```
  @Test
  @DisplayName("멀티쓰레드")
  @Transactional
  void testDecreaseStockInMulti() throws InterruptedException {
 
    Long averageStock = 0L;
    Long averageTime = 0L;
    final int threadCount = 1000;
    Long start = System.currentTimeMillis();
    final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
 
    IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
      try {
        itemService.decreaseDefault(itemDefaultIdx);
      } finally {
        countDownLatch.countDown();
      }
    }));

    countDownLatch.await();
    System.out.println(System.currentTimeMillis()-start);
  }
```
||1차 테스트|2차 테스트|3차 테스트|4차 테스트| 5차 테스트|
|---|---|---|---|---|---|
|stock|801|804|811|807|805|
|time|1148|918|887|1061|902| 

- 천 개의 쓰레드를 만들어서 각 쓰레드가 아이템을 하나씩 줄이도록 테스트를 함
- 표에서 볼 수 있듯이 예상과는 다르게 stock이 0이 되지 않음
 - 어떤 식으로 stock이 감소하는지 확인결과 다음과 같음(일부만 발췌)

 [998, 998, 998, 997, 997, 997, 997, 996, 996, 995, 995, 994, 993, 993, 992, 992, 992, 992, 992, 991, 990, 990, 990, 989, 988, 988, 988, 986, 985, 985, 985, 984, 983, 983, 983, 982, 982, 981, 981, 980, 980, 980, 980, 979, 978, 978, 978, 978, 978, 977, 976, 976, 976, 976, 975, 975, 974] 

 ## Pessimistic Lock
 - 자원 요청에 따른 동시성 문제가 발생할 것이라고 예상해 락을 걸어버리는 방법론
- Exclusive Lock
  - 다른 트랜잭션에서 읽기, 쓰기가 둘 다 불가능
- Shared Lock
    - 다른 트랜잭션에서 읽기만 가능

- 이번 테스트에서는 Exclusive Lock을 통해 구현

## 장점
- 충돌이 자주 발생하는 환경에 대해서는 롤백의 횟수를 줄일 수 있으므로 성능에서 유리
- 데이터 무결성을 보장하는 수준이 매우 높음

## 단점
- 데이터 자체에 락을 걸어 동시성이 떨어져 성능 손해를 많이 보게 됨. 특히 읽기가 많이 이루어지는 DB에서는 손해가 더욱 두드러짐
- 서로 자원이 필요한 경우에, 데드락이 일어날 가능성이 있음

## Code
```
public interface ItemPessimisticRepository extends JpaRepository<ItemPessimistic, Long> {

@Lock(LockModeType.PESSIMISTIC_WRITE)
  public ItemPessimistic findByItemPessimisticIdx(Long itemPessimisitcIdx);
}
```
- Entity와 Service TestCode 모두 동일
- Repository에만 @Lock(LockModeType.PESSIMISTIC_WRITE) 추가

||1차 테스트|2차 테스트|3차 테스트|4차 테스트| 5차 테스트|
|---|---|---|---|---|---|
|stock|0|0|0|0|0|
|time|1563|1498|1455|1421|1429|

- 표에서 볼 수 있듯이 Stock이 모두 0으로 된 것을 확인할 수 있지만 기존 코드보다 속도가 느려진 것을 확인할 수 있음
---



## Optimistic Lock

- 자원에 락을 걸어 선점하지말고, 동시성 문제가 발생하면 그때 처리하자는 방법론

    - 트랜잭션의 충돌이 발생하지 않을 것이라고 기대
    - 일반적으로 version의 상태를 보고 충돌을 확인
    - DB단이 아닌 어플리케이션단에서 처리함
    - 추가 기능을 별도로 구현해야하는 번거로움 존재
    - Version 충돌 시 재시도 로직을 구현해야함

## 장점
- 충돌이 안난다는 가정하에, 동시 요청에 대해 처리 성능이 좋음

## 단점 
- 잦은 충돌이 일어나는 경우 롤백처리에 대한 비용이 많이 들어 오히려 성능에서 손해를 볼 수 있음

### 이번 Test에서는 일부러 충돌을 일으키므로 가장 속도가 느림

## Code

```
public interface ItemOptimisticRepository extends JpaRepository<ItemOptimistic,Long> {
  @Lock(LockModeType.OPTIMISTIC)
  public ItemOptimistic findByItemOptimisticIdx(Long itemOptimisticIdx);
}
```
- 먼저 다음과 같이 Repository에 @Lock(LockModeType.OPTIMISTIC) 사용

```
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="item_optimistic")
public class ItemOptimistic {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  Long itemOptimisticIdx;
  Long itemOptimisticStock;
  @Version
  Long version;
}
```
- Entity 객체에 Version property를 추가해 현재 객체의 최신 Version 값을 추적할 수 있도록 함

```
@Service
@RequiredArgsConstructor
public class OptimisticLockItemServiceFacade {
  private final ItemService itemService;

  public void decreaseOptimistic(Long itemOptimisticIdx) throws InterruptedException  {
      while(true){
        try{
          itemService.decreaseOptimistic(itemOptimisticIdx);
          break;
        }catch (Exception e){
          Thread.sleep(1);
        }
      }
  }
}
```

- 서비스 객체는 기존의 서비스와 같음
- 기존 Service 객체에서 충돌 재시도 로직까지 구현할 경우, 의존성이 높아지므로 PacadePattern을 이용한다.
- Test Code는 동일

||1차 테스트|2차 테스트|3차 테스트|4차 테스트| 5차 테스트|
|---|---|---|---|---|---|
|stock|0|0|0|0|0|
|time|3742|3722|3534|3687|3695|

- 표에서 볼 수 있듯이 충돌이 일어나게 Test Code를 작성했으므로 속도가 가장 느린 것을 확인할 수 있음
---
# 결론
- 동시성 문제가 크게 시스템 상에서 일어나지 않을 경우 DB에게만 위임하는 것이 가장 빠름
- 읽기가 많이 이루어지지 않고 충돌이 자주 발생하며, 데이터 무결성이 매우 중요할 경우 비관적 락을 사용이 유리
- 잦은 충돌이 이뤄지지 않고 데이터의 무결성도 챙기고 싶을 경우 낙관적 락을 사용할 경우 유리
