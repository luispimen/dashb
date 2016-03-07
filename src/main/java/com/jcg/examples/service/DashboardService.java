package com.jcg.examples.service;

import com.jcg.examples.entity.DashboardData;
import com.jcg.examples.entity.HistoryPoint;
import com.jcg.examples.entity.Project;
import com.jcg.examples.entity.ProjectDetails;

import java.util.List;

/**
 * Created by lpimentel on 26-02-2016.
 */
public interface DashboardService {
	public List<Project> getAllProjects();

	public List<ProjectDetails> getAllProjectsDetails();

	public ProjectDetails getProjectsDetails(Long id);

	public List<HistoryPoint> getHistory(Long id);

	public DashboardData getDashboardData(Long id);

}
