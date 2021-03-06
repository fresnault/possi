package fr.istic.iodeman.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import fr.istic.iodeman.SessionComponent;
import fr.istic.iodeman.model.Planning;
import fr.istic.iodeman.model.TimeBox;
import fr.istic.iodeman.service.PlanningService;
import fr.istic.iodeman.strategy.PlanningSplitter;
import fr.istic.iodeman.strategy.PlanningSplitterImpl;
import fr.istic.possijar.Creneau;

@Controller
public class FileDownloadController {
	
	@Autowired
	PlanningService planningService;
	
	@Autowired
	private SessionComponent session;
	
	@RequestMapping(value="/planning/{planningId}/export")
	public void downloadPlanning(@PathVariable("planningId") Integer planningId, HttpServletResponse response) throws IOException{
		
		// retrieve planning
		Planning planning = planningService.findById(planningId);
		Validate.notNull(planning);
		
		// check if the current user is the admin of this planning
		session.acceptOnly(planning.getAdmin());
		
		// mime type
		//response.setContentType("application/vnd.ms-excel");
		
		// name of the returned file
		String filename = "planningExport.csv";
		
		// header
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
        		filename);
        response.setHeader(headerKey, headerValue);
        
		// retrieving of the generating file containing the agenda
		File file = planningService.exportExcel(planning);		
		
		// BEGIN write the file
		FileInputStream in = new FileInputStream(file);
		OutputStream out = response.getOutputStream();

		byte[] buffer= new byte[8192]; 
		int length = 0;

		while ((length = in.read(buffer)) > 0){
		     out.write(buffer, 0, length);
		}
		in.close();
		out.close();
		// END write the file
		
		file.delete();
	}
	
	@RequestMapping(value="/plannings/exported", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getExportedPlanning() {
		JSONObject ret = new JSONObject();
		
		Set<Integer> s = new HashSet<Integer>();
		
		for (final File fileEntry : new File("persist").listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	if(new File(fileEntry.getPath() + "/planning.ser").exists()) {
	        		s.add(Integer.valueOf(fileEntry.getName()));
	        	}
	        }
	    }
		ret.put("keys", s);
		
		return ret.toString();
	}
	
	@RequestMapping(value="/planning/{planningId}/exportPlanning", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String showPlanning(@PathVariable("planningId") Integer planningId, HttpServletResponse response) throws IOException{
		Planning planning = planningService.findById(planningId);
		Validate.notNull(planning);
		
		Map<Integer, List<Creneau>> creneaux = planningService.exportJSON(planning);
		
		PlanningSplitter splitter = new PlanningSplitterImpl();
		
		List<TimeBox> timeboxes = splitter.execute(planning);
		
		JSONObject ret = new JSONObject();
		
		JSONObject obj1 = new JSONObject();
		
		ret.put("salles", planning.getRooms());
		ret.put("priorités", planning.getPriorities());
		
		Date d1 = planning.getPeriod().getFrom();
		
		List<List<Creneau>> day = new ArrayList<List<Creneau>>();
		
		int nbPeriodeParJour = getNbDePeriodesParJour(timeboxes);
		
		int dayPlus = 0;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		
		for(int i = 0; i < creneaux.size(); i++) {
			//obj.put(""+i, creneaux.get(i));
			day.add(creneaux.get(i));
			
			if((i+1)%nbPeriodeParJour==0) {
				obj1.put(""+cal.getTimeInMillis(), day);
				if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.DAY_OF_MONTH, 1);
				}
				cal.add(Calendar.DAY_OF_MONTH, 1);
				day.clear();
			}
		}
		
		System.err.println(day.toString());
		
		ret.put("creneaux", obj1);
		
		
		
		return ret.toString();
	}
	
	private int getNbDePeriodesParJour(Collection<TimeBox> timeboxes) {
		// TODO
		int i = 0;
		int d1 = -1;
		for(TimeBox t : timeboxes) {
			if(d1 == -1) {
				d1 = t.getFrom().getDate();
			}
			if(t.getFrom().getDate() == d1) {
				i++;
			}
		}
		return i;
	}
	
}