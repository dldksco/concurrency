package org.study.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="item_default")
@ToString
public class ItemDefault {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  Long itemDefaultIdx;
  Long itemDefaultStock=100L;
}
