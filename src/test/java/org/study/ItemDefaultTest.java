package org.study;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.study.domain.ItemDefault;
import org.study.repository.ItemDefaultRepository;
import org.study.repository.ItemPessimisticRepository;
import org.study.service.ItemService;
import org.study.service.OptimisticLockItemServiceFacade;

@SpringBootTest
@SpringJUnitConfig
public class ItemDefaultTest {

  private final Long itemDefaultIdx = 7L;
  private final Long itemPessimisticIdx = 1L;
  private final Long itemOptimisticIdx =1L;
  @Autowired
  private ItemService itemService;
  @Autowired
  private ItemDefaultRepository itemDefaultRepository;
  @Autowired
  private ItemPessimisticRepository itemPessimisticRepository;
  @Autowired
  private OptimisticLockItemServiceFacade optimisticLockItemServiceFacade;
  private ExecutorService executorService;
  private CountDownLatch countDownLatch;

    @DisplayName("단일 쓰레드일 때를 테스트한다")
  @Test
  @Transactional
  void decreaseStockInSingle() {
    // given
    Long itemDefaultIdx = 3L;
    // when

    itemService.decreaseDefault(itemDefaultIdx);
    // then
    ItemDefault itemDefault = itemDefaultRepository.findByItemDefaultIdx(itemDefaultIdx);
    System.out.println("단일 쓰레드 수량"+" "+itemDefault.getItemDefaultStock());
//    assertNotNull(itemDefault);
//    assertEquals(99L, (long) itemDefault.getItemDefaultStock());
  }
  @Test
  @DisplayName("멀티쓰레드")
  @Transactional
  void testDecreaseStockInMulti() throws InterruptedException {
    // given
    List<Long> list = new ArrayList<>();
    final int threadCount = 1000;
    Long start = System.currentTimeMillis();
    final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    // when
    IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
      try {
        itemService.decreaseDefault(itemDefaultIdx);
        list.add(itemDefaultRepository.findByItemDefaultIdx(itemDefaultIdx).getItemDefaultStock());
      } finally {
        countDownLatch.countDown();
      }
    }));

    countDownLatch.await();
    System.out.println(list.toString());
    System.out.println(System.currentTimeMillis()-start);
  }


  @Test
  @DisplayName("pessimis 멀티쓰레드")
  @Transactional
  void pesDecreaseStockInMulti() throws InterruptedException {
    // given
    List<Long> list = new ArrayList<>();
    final int threadCount = 1000;
    Long start = System.currentTimeMillis();
    final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    // when
    IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
      try {
        itemService.decreasePessimistic(itemPessimisticIdx);
        System.out.println(itemPessimisticRepository.findByItemPessimisticIdx(itemPessimisticIdx).getItemPessimisticStock());
      } finally {
        countDownLatch.countDown();
      }
    }));

    countDownLatch.await();
    // then
    System.out.println(System.currentTimeMillis() - start + " d");

//    assertNotNull(itemDefault);
//    assertEquals(90L, (long) itemDefault.getItemDefaultStock());
  }

  @Test
  @DisplayName("Opti 멀티쓰레드")
  @Transactional
  void optiDecreaseStockInMulti() throws InterruptedException {
    // given
    final int threadCount = 1000;
    Long start = System.currentTimeMillis();
    final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    // when
    IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
      try {
        optimisticLockItemServiceFacade.decreaseOptimistic(itemOptimisticIdx);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      } finally {
        countDownLatch.countDown();
      }
    }));

    countDownLatch.await();
    // then
    System.out.println(System.currentTimeMillis() - start + " d");

//    assertNotNull(itemDefault);
//    assertEquals(90L, (long) itemDefault.getItemDefaultStock());
  }



}
