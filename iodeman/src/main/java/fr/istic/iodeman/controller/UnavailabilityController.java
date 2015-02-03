package fr.istic.iodeman.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.istic.iodeman.model.Unavailability;
import fr.istic.iodeman.service.UnavailabilityService;


@RequestMapping("/unavailabilities") 
@RestController
public class UnavailabilityController {
	
	@Autowired
	private UnavailabilityService unavailabilityService;
	
	@RequestMapping("/{id}/")
	public List<Unavailability> findById(@PathVariable("id") Integer id, @RequestParam("person") Integer idperson ){
		List<Unavailability> unavailabilities = new ArrayList<Unavailability>();
		unavailabilities = unavailabilityService.findById(id, idperson);

		return unavailabilities;
	}

}