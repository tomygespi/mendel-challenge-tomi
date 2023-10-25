package com.challenge.java.tomi.mapper;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for Transaction and TransactionDTO.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "type", target = "type", qualifiedByName = "typeToTypeEnum")
    Transaction toTransaction(TransactionDTO transactionDTO);

    @Named("typeToTypeEnum")
    static TypeEnum typeToTypeEnum(String type) {
        return TypeEnum.find(type.toUpperCase());
    }
}
