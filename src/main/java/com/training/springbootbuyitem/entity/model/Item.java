package com.training.springbootbuyitem.entity.model;


import com.training.springbootbuyitem.enums.EnumItemState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Proxy(lazy = false)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Item extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_item", unique = true, nullable = false)
	private Long itemUid;
	@Column(unique = true)
	private String name;
	private String state;
	private String description;
	private String market;
	private BigInteger stock;
	private BigDecimal priceTag;

	@OneToMany(mappedBy = "item")
	private Set<Purchase> purchases = new HashSet<>();

	public void setStock(BigInteger stock) {
		this.stock = stock;
		if (this.stock.compareTo(BigInteger.valueOf(0)) == 0) {
			setState(EnumItemState.RESTOCK.name());
		}
	}

	public Item(String name) {
		this.name = name;
	}
}
