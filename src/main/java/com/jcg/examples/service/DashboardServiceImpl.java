package com.jcg.examples.service;

import com.jcg.examples.entity.Costo;
import com.jcg.examples.entity.DashboardData;
import com.jcg.examples.entity.HistoryPoint;
import com.jcg.examples.entity.Plan;
import com.jcg.examples.entity.Project;
import com.jcg.examples.entity.ProjectDetails;
import com.jcg.examples.entity.ResumenTareas;
import com.jcg.examples.entity.Riesgo;
import com.jcg.examples.entity.SignosVitales;
import com.jcg.examples.repo.DashboardDataProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lpimentel on 03-03-2016.
 */
public class DashboardServiceImpl implements DashboardService {
	private DashboardService dashboardServiceMock = new DashboardServiceMockImpl();
	private DashboardDataProvider dataProvider;

	public void setDataProvider(DashboardDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	public List<Project> getAllProjects() {
		return dataProvider.getAllProjects();
	}

	@Override
	public List<ProjectDetails> getAllProjectsDetails() {
		return dataProvider.getAllProjectDetails();
	}

	@Override
	public List<HistoryPoint> getHistory(Long id) {
		// return dashboardServiceMock.getHistory(id);
		List<HistoryPoint> list = dataProvider.getHistory(id);
		Collections.sort(list);
		return list;
	}

	@Override
	public ProjectDetails getProjectsDetails(Long id) {
		// TODO Auto-generated method stub
		return dataProvider.getProjectDetailsById(id);
	}

	@Override
	public DashboardData getDashboardData(Long id) {
		ProjectDetails details = getProjectsDetails(id);
		DashboardData dashboardData = new DashboardData();
		dashboardData.setDatosGenerales(details);
		List<HistoryPoint> historyPoints = getHistory(id);
		List<Plan> series = new LinkedList<Plan>();
		for (int i = 0; i < historyPoints.size(); i++) {
			HistoryPoint historyPoint = historyPoints.get(i);
			Plan plan = new Plan();
			plan.setFecha(historyPoint.getDate());
			plan.setPlan(historyPoint.getPlan());
			plan.setReal(historyPoint.getReal());
			series.add(plan);
		}

		dashboardData.setPlan(series);

		dashboardData.setCosto(dataProvider.getCosto(id));

		// Resumen tareas
		ResumenTareas resumenTareas = dataProvider.getResumenTareas(id);
		dashboardData.setResumenTareas(resumenTareas);

		// Riesgos
		List<Riesgo> riesgos = dataProvider.getRiesgos(id);
		dashboardData.setRiesgos(riesgos);

		// Signos vitales
		SignosVitales signosVitales = dataProvider.getSignosVitales(id);
		// int avance = 0;
		// signosVitales.setAvance(avance);
		dashboardData.setSignosVitales(signosVitales);

		return dashboardData;
	}
}
