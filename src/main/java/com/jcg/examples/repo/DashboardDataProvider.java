package com.jcg.examples.repo;

import com.jcg.examples.entity.Costo;
import com.jcg.examples.entity.HistoryPoint;
import com.jcg.examples.entity.Project;
import com.jcg.examples.entity.ProjectDetails;
import com.jcg.examples.entity.ResumenTareas;
import com.jcg.examples.entity.Riesgo;
import com.jcg.examples.entity.SignosVitales;

import java.util.List;

/**
 * Created by lpimentel on 02-03-2016.
 */
public interface DashboardDataProvider {

	public ProjectDetails getProjectDetailsById(long id);

	public List<ProjectDetails> getAllProjectDetails();

	public List<Project> getAllProjects();

	public List<HistoryPoint> getHistory(Long id);

	public Costo getCosto(Long id);

	public ResumenTareas getResumenTareas(Long id);

	public List<Riesgo> getRiesgos(final Long id);

	public SignosVitales getSignosVitales(final Long id);

}
