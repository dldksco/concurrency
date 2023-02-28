package org.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.study.domain.ItemOptimistic;

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
