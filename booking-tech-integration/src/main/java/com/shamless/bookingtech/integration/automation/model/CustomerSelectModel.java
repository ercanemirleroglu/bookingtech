package com.shamless.bookingtech.integration.automation.model;

import com.shamless.bookingtech.common.util.model.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSelectModel {
    private CustomerSelectType type;
    private int count;

    public static List<CustomerSelectModel> toModel(Map<Param, String> params) {
        return params.entrySet().stream().filter(p -> p.getKey().isCustomerSelect())
                .map(p -> {
                    CustomerSelectModel customerSelectModel = new CustomerSelectModel();
                    Param param = p.getKey();
                    Optional<CustomerSelectType> foundedType = Arrays.stream(CustomerSelectType.values()).filter(type -> param.name().contains(type.name()))
                            .findFirst();
                    if (foundedType.isEmpty())
                        throw new IllegalArgumentException("Customer Select Type not found in param keys!");
                    customerSelectModel.setType(foundedType.get());
                    customerSelectModel.setCount(Integer.parseInt(p.getValue()));
                    return customerSelectModel;
                }).collect(Collectors.toList());
    }
}
