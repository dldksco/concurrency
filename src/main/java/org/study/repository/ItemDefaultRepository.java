package org.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.domain.ItemDefault;

public interface ItemDefaultRepository extends JpaRepository<ItemDefault,Long> {
  public ItemDefault findByItemDefaultIdx(Long itemDefaultIdx);
}
