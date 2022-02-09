package com.demo.tutorial.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.tutorial.model.Tutorial;
import com.demo.tutorial.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class TutorialController {
	
	 private static Logger LOGGER = LoggerFactory.getLogger(TutorialController.class);
	
	@Autowired
	TutorialRepository repository;
	
	
	
	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required= false) String title){
		
		List<Tutorial> tutorials = new ArrayList<Tutorial>();
		
		try {
		if(title == null) {
			repository.findAll().forEach(tutorials::add);
		}
		else 
			repository.findByTitleContaining(title).forEach(tutorials::add);
		
		if(tutorials.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		else 
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		
		}catch(Exception e) {
			LOGGER.error("Exception getting tutorials");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
		@GetMapping("tutorials/{id}")
		public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id")long id){
			
			Optional<Tutorial> tutorial = repository.findById(id);
			
			if(tutorial.isPresent()) {
				return new ResponseEntity<>(tutorial.get(), HttpStatus.OK);
			}
			else
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		
		@PostMapping("/tutorials")
		public ResponseEntity<Tutorial> saveTutorial(@RequestBody Tutorial tutorial){
			
			try {
				LOGGER.info("saving new tutorial");
			Tutorial newTutorial = repository.save(tutorial);
			
			return new ResponseEntity<>(newTutorial, HttpStatus.CREATED);
			}catch(Exception e ) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@PutMapping("/tutorials/{id}")
		public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id")long id, @RequestBody Tutorial tutorial){
			
			Optional<Tutorial> _tutorial = repository.findById(id);
			if(_tutorial.isPresent()) {
				Tutorial updateTutorial = _tutorial.get();
				updateTutorial.setTitle(tutorial.getTitle());
				updateTutorial.setDescription(tutorial.getDescription());
				updateTutorial.setPublished(tutorial.isPublished());
				
				return new ResponseEntity<>(repository.save(updateTutorial), HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@DeleteMapping("/tutorials/{id}")
		public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") long id){
			LOGGER.info("Deleting tutorial at index:"+id);
			try {
			repository.deleteById(id);
			LOGGER.info("Tutorial successfully deleted");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
			} catch(Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@DeleteMapping("/tutorials")
		public ResponseEntity<HttpStatus> deleteAllTutorials(){
			try {
			repository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch(Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@GetMapping("/tutorials/published")
		public ResponseEntity<List<Tutorial>> findByPublished(){
			
			try {
			List<Tutorial> tutorials = repository.findByPublished(true);
			
			if(tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		
				return new ResponseEntity<>(tutorials,HttpStatus.OK);
		
			} catch(Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
		
	

}
