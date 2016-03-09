package com.jcg.examples.service;


import com.jcg.examples.entity.DashboardData;
import com.jcg.examples.entity.HistoryPoint;
import com.jcg.examples.entity.Project;
import com.jcg.examples.entity.ProjectDetails;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lpimentel on 26-02-2016.
 */
public class DashboardServiceMockImpl implements DashboardService {

    @Override
    public List<Project> getAllProjects() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 10; i++) {
            list.add(new Project("Proyecto " + i, i));
        }
        return list;
    }

    @Override
    public List<HistoryPoint> getHistory(Long id) {
//        int[] planPoints = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
//        int[] realPoints = new int[]{8, 16, 22, 31, 42, 56, 71, 79, 92, 100};
//        String[] dates = new String[]{"1/1", "2/1", "3/1", "4/1", "5/1", "6/1", "7/1", "8/1", "9/1", "10/1"};
//        List<HistoryPoint> historyPoints = new LinkedList<HistoryPoint>();
//        int i = 0;
//        for (String date : dates) {
//            historyPoints.add(new HistoryPoint(date, planPoints[i], realPoints[i], null));
//            i++;
//        }
//
//        return historyPoints;

        return null;
    }

    @Override
    public List<ProjectDetails> getAllProjectsDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProjectDetails getProjectsDetails(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DashboardData getDashboardData(Long id) {
        // TODO Auto-generated method stub
        return null;
    }


}
