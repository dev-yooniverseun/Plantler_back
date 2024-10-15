package com.plantler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.plantler.dto.KeywordDTO;
import com.plantler.dto.PlantDetailDTO;
import com.plantler.mapper.PlantlMapper;

@Service
public class PlantService {

    @Autowired
    private PlantlMapper plantMapper;
    
	public List<PlantDetailDTO> plantRec() {
	    List<PlantDetailDTO> plantsRec = plantMapper.getRandomKeyword();
//	    for (PlantDTO plant : recPlants) {
//	        // Base64로 변환된 이미지 URL을 세팅
//	        plant.setPlantImageBase64(plant.getPlantImageBase64());
//	    }
	    return plantsRec;
	}
    
    public PlantDetailDTO getPlantDetail(Long id) {
        return plantMapper.getPlantDetailById(id);
    }
    
    public List<PlantDetailDTO> getPlantAll() {
    	List<PlantDetailDTO> plantsAll = plantMapper.getPlantDetail();
        return plantsAll;
    }
    
    public List<KeywordDTO> getPlantKeyword(){
    	return plantMapper.getPlantKeyword();
    }
    
}
