package org.study.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
