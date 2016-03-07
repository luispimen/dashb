package com.jcg.examples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcg.examples.entity.DashboardData;
import com.jcg.examples.entity.Project;
import com.jcg.examples.entity.ProjectDetails;

@Controller
public class DashboardController {
	@Autowired
	private DashboardService dashboardService;

	@RequestMapping(value = "/projectdata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getDashboardData(@RequestParam(value = "id", defaultValue = "0") String id)
			throws JsonProcessingException {
		DashboardData dashboardData = dashboardService.getDashboardData(Long.parseLong(id));
		// dashboardData.toJSON()
		return dashboardData.toJSON().replaceAll("null", "\"\"");
	}

	@RequestMapping(value = "/allprojects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getAllProjects() throws JsonProcessingException {
		List<Project> projects = dashboardService.getAllProjects();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(projects).replaceAll("null", "\"\"");
	}
}
