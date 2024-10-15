package com.plantler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.plantler.dto.KeywordDTO;
import com.plantler.dto.PlantDetailDTO;
import com.plantler.service.PlantService;

@RestController
@RequestMapping("/api")
public class PlantController {

	@Autowired
	private PlantService plantService;
	
	// 추천식물 
	@GetMapping("/plantRec")
	public List<PlantDetailDTO> plantRec() {
		try {
	        return plantService.plantRec();
	    } catch (Exception e) {
	        e.printStackTrace(); // 로그에 에러 출력
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류", e);
	    }
	}
	
    @GetMapping("/plantDetail")
    public PlantDetailDTO getPlantDetail(@RequestParam("id") Long id) {
        return plantService.getPlantDetail(id);
    }
    
    @GetMapping("/plantTag")
    public List<PlantDetailDTO> getPlantTag() {
    	try {
        return plantService.getPlantAll();
    	 } catch (Exception e) {
 	        e.printStackTrace(); // 로그에 에러 출력
 	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류", e);
 	    }
    }
    
    @GetMapping("/plantKeyword")
    public List<KeywordDTO> getPlantKeyword() {
    	return plantService.getPlantKeyword();

    }

}
