package org.study.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="item_pessimistic")
public class ItemPessimistic {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  Long itemPessimisticIdx;
  Long itemPessimisticStock;
}
