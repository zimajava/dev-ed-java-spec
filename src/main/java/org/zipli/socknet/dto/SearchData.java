package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SearchData extends BaseData {
    private String searchParam;

    public SearchData(String searchParam) {
        this.searchParam = searchParam;
    }

    public SearchData(String userId, String searchParam) {
        super(userId);
        this.searchParam = searchParam;
    }
}
