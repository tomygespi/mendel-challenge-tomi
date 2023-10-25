package com.challenge.java.tomi.domain;

import com.challenge.java.tomi.domain.transaction.TypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
public class Transaction {
    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    @Column(name = "parent_id")
    private Long parentId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Set<Transaction> nestedTransactions;
}
