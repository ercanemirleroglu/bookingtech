package com.shameless.bookingtech.integration.automation.model;

import com.shameless.bookingtech.common.util.model.DateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaExtDto {
    private int adult;
    private int room;
    private int child;
    private String location;
    private String currency;

    public void setCustomerCounts(List<CustomerSelectModel> customerSelectModels) {
        customerSelectModels.forEach(selectModel -> {
            int count = selectModel.getCount();
            if (CustomerSelectType.ADULT.equals(selectModel.getType()))
                this.adult = count;
            else if (CustomerSelectType.CHILD.equals(selectModel.getType()))
                this.child = count;
            else if (CustomerSelectType.ROOM.equals(selectModel.getType()))
                this.room = count;
        });

    }
}
