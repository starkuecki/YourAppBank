package com.example.boobybank.entity.accounts;

import com.example.boobybank.control.accounts.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @NotNull
    @Size(min = 22, max = 22)
    private String iban;


    @NotNull
    private double balance;

    @NotNull
    @Pattern(regexp = "current|savings")
    private String accountType;

    @NotNull
    private UUID ownerId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "account_transactions",           // eigene Tabelle für die Transaktionen
            joinColumns = @JoinColumn(name = "iban")  // Fremdschlüssel zurück zum Account
    )
    @NotNull
    private List<Transaction> transactions;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AccountEntity that = (AccountEntity) o;
        return getIban() != null && Objects.equals(getIban(), that.getIban());
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
