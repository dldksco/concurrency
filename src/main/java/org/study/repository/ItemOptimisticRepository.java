package org.study.repository;

import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.study.domain.ItemDefault;
import org.study.domain.ItemOptimistic;

public interface ItemOptimisticRepository extends JpaRepository<ItemOptimistic,Long> {
  @Lock(LockModeType.OPTIMISTIC)
  public ItemOptimistic findByItemOptimisticIdx(Long itemOptimisticIdx);
}
