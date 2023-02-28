package org.study.repository;

import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.study.domain.ItemDefault;
import org.study.domain.ItemPessimistic;

public interface ItemPessimisticRepository extends JpaRepository<ItemPessimistic, Long> {

@Lock(LockModeType.PESSIMISTIC_WRITE)
  public ItemPessimistic findByItemPessimisticIdx(Long itemPessimisitcIdx);
}
