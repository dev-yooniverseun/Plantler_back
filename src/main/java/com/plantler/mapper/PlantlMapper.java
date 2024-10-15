package com.plantler.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.plantler.dto.KeywordDTO;
import com.plantler.dto.PlantDetailDTO;

@Mapper
public interface PlantlMapper {
	
	@Select("SELECT * FROM pl_plant;")
	public List<PlantDetailDTO> getRecPlant();
	
//    @Select("SELECT * FROM plant_with_random_keywords;")
//    public List<PlantDTO> getRandomKeyword();
    
	// 키워드 
    @Select("SELECT * FROM pl_detail ORDER BY RAND() LIMIT 5;")
    public List<PlantDetailDTO> getRandomKeyword();
    
    @Select("SELECT * FROM pl_detail")
    public List<PlantDetailDTO> getPlantDetail();
    
    @Select("SELECT * FROM pl_detail WHERE plantNo = #{id}")
    public PlantDetailDTO getPlantDetailById(Long id);
    
    @Select("SELECT keyword_no AS keywordNo, keyword_name AS keywordName FROM pl_keyword")
    public List<KeywordDTO> getPlantKeyword();

	
}
