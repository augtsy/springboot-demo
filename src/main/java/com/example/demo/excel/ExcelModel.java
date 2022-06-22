package com.example.demo.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExcelModel {

    private int firstRow;
    private int lastRow;
    private int firstCol;
    private int lastCol;
}
