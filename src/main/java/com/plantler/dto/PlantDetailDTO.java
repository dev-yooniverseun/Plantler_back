package com.plantler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantDetailDTO {
    private Long plantNo;
    private String plantTitle;
    private String plantImage; // 이미지 URL
    private String plantContent; // 식물 설명
    private String keyword1;
    private String keyword2;
    private String keyword3;
//    private String keyword4;
//    private String keyword5;
//    private String keyword6;
//    private String keyword7;
//    private String keyword8;
//    private String keyword9;
//    private String keyword10;
    private String knowhow1; // 추가된 노하우들
    private String knowhow2;
//    private String knowhow3;
//    private String knowhow4;
}