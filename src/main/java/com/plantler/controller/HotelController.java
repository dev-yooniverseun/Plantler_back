package com.plantler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelController {
	
	@GetMapping("/hotellist")
	public String hotellist() {
		return "hotel/hotellist";
	}
	@GetMapping("/hotelmaps")
	public String hotelmaps() {
		return "hotel/hotelmaps";
	}

}
