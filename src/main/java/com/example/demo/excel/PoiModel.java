package com.example.demo.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PoiModel {

    private String content;

    private String oldContent;

    private String cellContent;

    private String cellOldContent;

    private String primaryKey;

    private int rowIndex;

    private int cellIndex;

}