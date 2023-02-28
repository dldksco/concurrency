package org.study.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.study.domain.ItemDefault;
import org.study.domain.ItemOptimistic;
import org.study.domain.ItemPessimistic;
import org.study.repository.ItemDefaultRepository;
import org.study.repository.ItemOptimisticRepository;
import org.study.repository.ItemPessimisticRepository;

@RequiredArgsConstructor
@Service
public class ItemService {

  private final ItemDefaultRepository itemDefaultRepository;
  private final ItemOptimisticRepository itemOptimisticRepository;
  private final ItemPessimisticRepository itemPessimisticRepository;

  @Transactional
  public void decreaseDefault(Long itemDefaultIdx) {
    ItemDefault itemDefault = itemDefaultRepository.findByItemDefaultIdx(itemDefaultIdx);
    Long stock = itemDefault.getItemDefaultStock();
    itemDefault.setItemDefaultStock(stock - 1);
    itemDefaultRepository.save(itemDefault);
  }

  @Transactional
  public void decreaseOptimistic(Long itemOptimisticIdx) {
    ItemOptimistic itemOptimistic = itemOptimisticRepository.findByItemOptimisticIdx(
        itemOptimisticIdx);
    Long stock = itemOptimistic.getItemOptimisticStock();
    itemOptimistic.setItemOptimisticStock(stock - 1);
    itemOptimisticRepository.save(itemOptimistic);
  }
  @Transactional
  public void decreasePessimistic(Long itemPessimisticIdx) {
    ItemPessimistic itemPessimistic = itemPessimisticRepository.findByItemPessimisticIdx(
        itemPessimisticIdx);
    Long stock = itemPessimistic.getItemPessimisticStock();
    itemPessimistic.setItemPessimisticStock(stock - 1);
    itemPessimisticRepository.save(itemPessimistic);
  }


}
