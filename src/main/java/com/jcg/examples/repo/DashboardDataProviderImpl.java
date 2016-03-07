package com.jcg.examples.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.jcg.examples.entity.Costo;
import com.jcg.examples.entity.HistoryPoint;
import com.jcg.examples.entity.Project;
import com.jcg.examples.entity.ProjectDetails;
import com.jcg.examples.entity.ResumenTareas;
import com.jcg.examples.entity.Riesgo;
import com.jcg.examples.entity.SignosVitales;

/**
 * Created by lpimentel on 02-03-2016.
 */
public class DashboardDataProviderImpl implements DashboardDataProvider {
	private JdbcTemplate jdbcTemplate;
	public static final int PROJECT_MANAGER_PROP = 2;
	public static final int GERENTE_PROP = 3;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public ProjectDetails getProjectDetailsById(final long id) {
		final ProjectDetails details = jdbcTemplate.query("select * from PN_PROJECT_SPACE where project_id=?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setLong(1, id);
					}
				}, new ResultSetExtractor<ProjectDetails>() {
					public ProjectDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							ProjectDetails projectDetails = new ProjectDetails();
							projectDetails.setDesc((resultSet.getString("PROJECT_DESC") != null)
									? resultSet.getString("PROJECT_DESC") : "");
							projectDetails.setId(resultSet.getLong("PROJECT_ID"));
							projectDetails.setName(resultSet.getString("PROJECT_NAME") != null
									? resultSet.getString("PROJECT_NAME") : "");
							projectDetails.setSponsor(resultSet.getString("SPONSOR_DESC") != null
									? resultSet.getString("SPONSOR_DESC") : "");
							projectDetails.setStartDate(resultSet.getDate("START_DATE"));
							projectDetails.setEndDate(resultSet.getDate("END_DATE"));
							Long porciento = resultSet.getLong("PERCENT_COMPLETE");
							projectDetails.setPorciento((porciento != null) ? porciento : 0);
							return projectDetails;
						}
						return null;
					}
				});
		jdbcTemplate.query("select * from PN_PROJECT_SPACE_META_VALUE where project_id=?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setLong(1, id);
					}
				}, new ResultSetExtractor<ProjectDetails>() {
					public ProjectDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						while (resultSet.next()) {
							if (resultSet.getInt("PROPERTY_ID") == GERENTE_PROP) {
								details.setJefeProyecto(resultSet.getString("PROPERTY_VALUE"));
							} else if (resultSet.getInt("PROPERTY_ID") == PROJECT_MANAGER_PROP) {
								details.setLider(resultSet.getString("PROPERTY_VALUE"));
							}

						}
						return null;
					}
				});

		return details;

	}

	@Override
	public List<ProjectDetails> getAllProjectDetails() {
		return jdbcTemplate.query(
				"select pn_project_space.project_name, pn_project_space.sponsor_desc,  pn_project_space.PROJECT_DESC, \n"
						+ "       pn_plan.plan_id\n" + "from pn_plan, pn_space_has_plan, pn_project_space\n"
						+ "where pn_plan.plan_id=pn_space_has_plan.plan_id and\n"
						+ "      pn_project_space.project_id=pn_space_has_plan.space_id ",
				new ResultSetExtractor<List<ProjectDetails>>() {
					public List<ProjectDetails> extractData(ResultSet resultSet)
							throws SQLException, DataAccessException {
						List<ProjectDetails> list = new LinkedList<ProjectDetails>();
						while (resultSet.next()) {
							ProjectDetails projectDetails = new ProjectDetails();
							projectDetails.setDesc(resultSet.getString("PROJECT_DESC") != null
									? resultSet.getString("PROJECT_DESC") : "");
							projectDetails.setId(resultSet.getLong("plan_id"));
							String name = resultSet.getString("PROJECT_NAME");
							projectDetails.setName(name != null ? name : "");
							String sponsor = resultSet.getString("sponsor_desc");
							projectDetails.setSponsor(name != null ? name : "");
							list.add(projectDetails);
						}
						return list;
					}
				});
	}

	@Override
	public List<Project> getAllProjects() {
		return jdbcTemplate.query(
				"select * from pn_plan, pn_space_has_plan, pn_project_space\n"
						+ "where pn_plan.plan_id=pn_space_has_plan.plan_id and\n"
						+ "      pn_project_space.project_id=pn_space_has_plan.space_id ",
				new ResultSetExtractor<List<Project>>() {
					public List<Project> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						List<Project> list = new LinkedList<Project>();
						while (resultSet.next()) {
							Project project = new Project();
							project.setId(resultSet.getLong("PROJECT_ID"));
							String name = resultSet.getString("PROJECT_NAME");
							project.setName(name != null ? name : "");
							list.add(project);
						}
						return list;
					}
				});
	}

	@Override
	public List<HistoryPoint> getHistory(final Long id) {
		String idQuery = "select pn_plan.plan_id from pn_plan, pn_space_has_plan, pn_project_space "
				+ "where pn_plan.plan_id=pn_space_has_plan.plan_id and " + "pn_space_has_plan.space_id =  ?";

		final Long planId = jdbcTemplate.query(idQuery, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, id);
			}
		}, new ResultSetExtractor<Long>() {
			public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				Long planId = new Long(-1);
				if (resultSet.next()) {
					return resultSet.getLong("PLAN_ID");
				}

				return planId;
			}
		});

		String query = " SELECT NULL LINK, TO_CHAR(LABEL, 'DD/MM/RR'), SUM(PLAN) PLAN, sum(hecho) HECHO, SUM(BASE) BASE\n"
				+ "\n" + "FROM\n" + "(\n" + "select NULL LINK, \n" + "       label, \n"
				+ "       (sum(plan) OVER (order BY label  ROWS UNBOUNDED PRECEDING) ) AS PLAN, \n"
				+ "       (sum(hecho) OVER (order BY label  ROWS UNBOUNDED PRECEDING) ) AS HECHO,      \n"
				+ "       null as BASE\n" + "from\n" + "(SELECT NULL LINK,\n" + "       pn_task.date_FINISH AS LABEL,\n"
				+ "       SUM(pn_task.work) AS Plan,\n" + "       SUM(pn_task.work_COMPLETE) AS Hecho\n" + "  \n"
				+ "from pn_plan, pn_task, pn_plan_has_task, pn_space_has_plan, pn_project_space\n" + "\n"
				+ "where pn_task.task_id=pn_plan_has_task.task_id and \n"
				+ "      pn_plan.plan_id=pn_plan_has_task.plan_id and\n"
				+ "      pn_plan.plan_id=pn_space_has_plan.plan_id and\n"
				+ "      pn_project_space.project_id=pn_space_has_plan.space_id  AND \n" + "      pn_plan.plan_id=?\n"
				+ "      and pn_task.task_type='task'\n" + "      \n" + "group by pn_task.date_FINISH\n"
				+ "order by pn_task.date_FINISH )\n" + "\n" + "UNION\n" + "\n" + "SELECT NULL LINK, \n"
				+ "       LABEL, \n" + "       null PLAN, \n" + "       null HECHO,\n"
				+ "      (SUM(BASE) OVER (order BY LABEL  ROWS UNBOUNDED PRECEDING)) AS BASE\n" + "FROM\n"
				+ "(SELECT PN_TASK_VERSION.DATE_FINISH AS LABEL,\n" + "       SUM(PN_TASK_VERSION.work) AS Base\n"
				+ "       from pn_plan, pn_baseline, PN_BASELINE_HAS_TASK, PN_TASK_VERSION\n"
				+ "       where pn_plan.plan_id = pn_baseline.object_id and \n"
				+ "             is_default_for_object=1  and\n"
				+ "             PN_BASELINE_HAS_TASK.baseline_id = pn_baseline.baseline_id and\n"
				+ "             PN_TASK_VERSION.TASK_VERSION_ID = PN_BASELINE_HAS_TASK.TASK_VERSION_ID and\n"
				+ "             pn_plan.plan_id=?\n" + "             and pn_task_version.task_type='task'\n"
				+ "       GROUP BY PN_TASK_VERSION.DATE_FINISH\n" + "       order by PN_TASK_VERSION.DATE_FINISH)\n"
				+ ") \n" + "\n" + "GROUP BY LABEL\n" + "ORDER BY LABEL";
		return jdbcTemplate.query(query, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, planId);
				preparedStatement.setLong(2, planId);
			}
		}, new ResultSetExtractor<List<HistoryPoint>>() {
			public List<HistoryPoint> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				Map<String, HistoryPoint> pointMap = new HashMap<String, HistoryPoint>();
				List<HistoryPoint> list = new LinkedList<HistoryPoint>();
				while (resultSet.next()) {
					HistoryPoint historyPoint = new HistoryPoint();
					historyPoint.setDate(resultSet.getString("TO_CHAR(LABEL,'DD/MM/RR')"));
					historyPoint.setPlan(resultSet.getLong("PLAN"));
					historyPoint.setReal(resultSet.getLong("HECHO"));
					if (pointMap.containsKey(historyPoint.getDate())) {
						if (pointMap.get(historyPoint.getDate()).getPlan() < historyPoint.getPlan()) {
							pointMap.put(historyPoint.getDate(), historyPoint);
						}
					} else {
						pointMap.put(historyPoint.getDate(), historyPoint);
					}
				}
				for (String date : pointMap.keySet()) {
					list.add(pointMap.get(date));
				}
				return list;
			}
		});
	}

	@Override
	public Costo getCosto(final Long id) {
		final Costo costoResult = jdbcTemplate.query("select * from PN_PROJECT_SPACE where project_id=?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setLong(1, id);
					}
				}, new ResultSetExtractor<Costo>() {
					public Costo extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							Costo costo = new Costo();
							costo.setPresupuesto(resultSet.getLong("BUDGETED_TOTAL_COST_VALUE"));
							costo.setPlanificadoFecha(resultSet.getLong("CURRENT_EST_TOTAL_COST_VALUE"));
							costo.setGastado(resultSet.getLong("ACTUAL_TO_DATE_COST_VALUE"));
							costo.setMoneda(resultSet.getString("DEFAULT_CURRENCY_CODE"));
							costo.setDisponible(costo.getPresupuesto() - costo.getGastado());
							return costo;
						}
						return null;
					}
				});
		return costoResult;
	}

	@Override
	public ResumenTareas getResumenTareas(final Long id) {

		final ResumenTareas resumenTareas = new ResumenTareas();
		String query = "select   t.task_name, t.task_desc, t.task_type, t.priority,   t.duration, t.duration_units, t.work, t.work_units, "
				+ "t.work_complete,   t.work_complete_units, t.date_start, t.date_finish, t.task_id,   t.actual_start, "
				+ "t.actual_finish, t.percent_complete,   t.date_created, t.date_modified, t.modified_by,   t.parent_task_id, "
				+ "t.record_status, t.critical_path,   pt.task_name as parent_task_name, t.constraint_type,   t.constraint_date,"
				+ " t.deadline, t.seq, pht.plan_id,   t.ignore_times_for_dates, t.early_start, t.late_start,   t.early_finish, "
				+ "t.late_finish, ph.phase_id, ph.phase_name, ph.sequence,  t.work_percent_complete, t.is_milestone, shp.space_id,"
				+ "  shrd.exported_object_id, shrd.read_only, shbl.space_id as exporting_space_id,   spon.name as"
				+ " exporting_space_name, ctv.work, ctv.work_units,   ctv.duration, ctv.duration_units, ctv.date_start, "
				+ "ctv.date_finish,  ctv.baseline_id, t.calculation_type_id, t.unallocated_work_complete,  "
				+ "t.unallocated_work_complete_unit, t.unassigned_work, t.unassigned_work_units, t.wbs, t.wbs_level, "
				+ " cc.code_name from   pn_space_has_plan shp,   pn_plan p,   pn_plan_has_task pht,   pn_task t,   "
				+ "pn_task pt,   pn_phase_has_task phht,   pn_phase ph,  pn_shared shrd,   pn_shareable shbl,   "
				+ "pn_object_name spon,   pn_current_task_version ctv,   pn_charge_code cc,   pn_object_has_charge_code"
				+ " ohcc where   shp.plan_id = p.plan_id   and shp.plan_id = pht.plan_id   and p.plan_id = pht.plan_id  "
				+ " and pht.task_id = t.task_id   and t.parent_task_id = pt.task_id(+)   and t.task_id = phht.task_id(+)   and"
				+ " phht.phase_id = ph.phase_id(+)   and ph.record_status(+) = 'A'   and t.task_id = shrd.imported_object_id(+) "
				+ "  and shrd.exported_object_id = shbl.object_id(+)   and shbl.space_id = spon.object_id(+)  "
				+ " and t.task_id = ctv.task_id(+)   and cc.code_id (+) = ohcc.code_id   and ohcc.object_id (+)= t.task_id and "
				+ " t.work_percent_complete < 100  and  t.date_finish < TO_DATE('today_date', 'MM/DD/YYYY HH24:MI') and  shp.space_id = ? "
				+ "and t.record_status = 'A'";
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String today = dateFormat.format(new Date());
		query = query.replaceFirst("today_date", today);
		jdbcTemplate.query(query, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, id);
			}
		}, new ResultSetExtractor<ResumenTareas>() {
			public ResumenTareas extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<String> atrasadas = new LinkedList<String>();
				while (resultSet.next()) {
					String tarea = resultSet.getString("TASK_NAME");
					atrasadas.add(tarea);
				}

				resumenTareas.setRetrasadas(atrasadas.toArray(new String[atrasadas.size()]));
				return null;
			}
		});

		query = "select   t.task_name, t.task_desc, t.task_type, t.priority,   t.duration, t.duration_units, t.work, t.work_units, t.work_complete,   t.work_complete_units, t.date_start, t.date_finish, t.task_id,   t.actual_start, t.actual_finish, t.percent_complete,   t.date_created, t.date_modified, t.modified_by,   t.parent_task_id, t.record_status, t.critical_path,   pt.task_name as parent_task_name, t.constraint_type,   t.constraint_date, t.deadline, t.seq, pht.plan_id,   t.ignore_times_for_dates, t.early_start, t.late_start,   t.early_finish, t.late_finish, ph.phase_id, ph.phase_name, ph.sequence,  t.work_percent_complete, t.is_milestone, shp.space_id,  shrd.exported_object_id, shrd.read_only, shbl.space_id as exporting_space_id,   spon.name as exporting_space_name, ctv.work, ctv.work_units,   ctv.duration, ctv.duration_units, ctv.date_start, ctv.date_finish,  ctv.baseline_id, t.calculation_type_id, t.unallocated_work_complete,  t.unallocated_work_complete_unit, t.unassigned_work, t.unassigned_work_units, t.wbs, t.wbs_level,  cc.code_name from   pn_space_has_plan shp,   pn_plan p,   pn_plan_has_task pht,   pn_task t,   pn_task pt,   pn_phase_has_task phht,   pn_phase ph,  pn_shared shrd,   pn_shareable shbl,   pn_object_name spon,   pn_current_task_version ctv,   pn_charge_code cc,   pn_object_has_charge_code ohcc where   shp.plan_id = p.plan_id   and shp.plan_id = pht.plan_id   and p.plan_id = pht.plan_id   and pht.task_id = t.task_id   and t.parent_task_id = pt.task_id(+)   and t.task_id = phht.task_id(+)   and phht.phase_id = ph.phase_id(+)   and ph.record_status(+) = 'A'   and t.task_id = shrd.imported_object_id(+)   and shrd.exported_object_id = shbl.object_id(+)   and shbl.space_id = spon.object_id(+)   and t.task_id = ctv.task_id(+)   and cc.code_id (+) = ohcc.code_id   and ohcc.object_id (+)= t.task_id and  shp.space_id = ? and t.record_status = 'A' and ((t.work_percent_complete = 100))";
		jdbcTemplate.query(query, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, id);
			}
		}, new ResultSetExtractor<ResumenTareas>() {
			public ResumenTareas extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<String> completadas = new LinkedList<String>();
				while (resultSet.next()) {
					String tarea = resultSet.getString("TASK_NAME");
					completadas.add(tarea);
				}

				resumenTareas.setRealizadas(completadas.toArray(new String[completadas.size()]));
				return null;
			}
		});

		query = "select t.task_name, t.task_desc, t.task_type, t.priority,   t.duration, t.duration_units, t.work, t.work_units, t.work_complete,   t.work_complete_units, t.date_start, t.date_finish, t.task_id,   t.actual_start, t.actual_finish, t.percent_complete,   t.date_created, t.date_modified, t.modified_by,   t.parent_task_id, t.record_status, t.critical_path,   pt.task_name as parent_task_name, t.constraint_type,   t.constraint_date, t.deadline, t.seq, pht.plan_id,   t.ignore_times_for_dates, t.early_start, t.late_start,   t.early_finish, t.late_finish, ph.phase_id, ph.phase_name, ph.sequence,  t.work_percent_complete, t.is_milestone, shp.space_id,  shrd.exported_object_id, shrd.read_only, shbl.space_id as exporting_space_id,   spon.name as exporting_space_name, ctv.work, ctv.work_units,   ctv.duration, ctv.duration_units, ctv.date_start, ctv.date_finish,  ctv.baseline_id, t.calculation_type_id, t.unallocated_work_complete,  t.unallocated_work_complete_unit, t.unassigned_work, t.unassigned_work_units, t.wbs, t.wbs_level,  cc.code_name from   pn_space_has_plan shp,   pn_plan p,   pn_plan_has_task pht,   pn_task t,   pn_task pt,   pn_phase_has_task phht,   pn_phase ph,  pn_shared shrd,   pn_shareable shbl,   pn_object_name spon,   pn_current_task_version ctv,   pn_charge_code cc,   pn_object_has_charge_code ohcc where   shp.plan_id = p.plan_id   and shp.plan_id = pht.plan_id   and p.plan_id = pht.plan_id   and pht.task_id = t.task_id   and t.parent_task_id = pt.task_id(+)   and t.task_id = phht.task_id(+)   and phht.phase_id = ph.phase_id(+)   and ph.record_status(+) = 'A'   and t.task_id = shrd.imported_object_id(+)   and shrd.exported_object_id = shbl.object_id(+)   and shbl.space_id = spon.object_id(+)   and t.task_id = ctv.task_id(+)   and cc.code_id (+) = ohcc.code_id   and ohcc.object_id (+)= t.task_id and  t.date_finish >= TO_DATE('03/06/2016 05:00', 'MM/DD/YYYY HH24:MI') and  t.work_percent_complete < 100 and  (t.task_type = 'task' or t.task_type = 'summary')  and  shp.space_id = ? and t.record_status = 'A' and (((t.date_finish >= TO_DATE('ini_date', 'MM/DD/YYYY HH24:MI') and t.date_finish <= TO_DATE('end_date', 'MM/DD/YYYY HH24:MI'))))";
		String ini = dateFormat.format(new Date());
		query = query.replaceFirst("ini_date", ini);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, 7);
		String end = dateFormat.format(c.getTime());
		query = query.replaceFirst("ini_date", ini);
		query = query.replaceFirst("end_date", end);

		jdbcTemplate.query(query, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, id);
			}
		}, new ResultSetExtractor<ResumenTareas>() {
			public ResumenTareas extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<String> proximas = new LinkedList<String>();
				while (resultSet.next()) {
					String tarea = resultSet.getString("TASK_NAME");
					proximas.add(tarea);
				}

				resumenTareas.setProximas(proximas.toArray(new String[proximas.size()]));
				return null;
			}
		});

		return resumenTareas;
	}

	public List<Riesgo> getRiesgos(final Long id) {
		final List<Riesgo> riesgos = new LinkedList<Riesgo>();
		try {

			final String NOMBRE = "Nombre";
			final String IMPACTO = "Impacto";
			final String VARIABLE_AFECTADA = "Variable Afectada";
			final String MITIGACION = "Mitigacion";
			final String FACTOR_RIESGO = "Factor de Riesgo";

			String query = "select * from pn_class where OWNER_SPACE_ID=? and CLASS_NAME like '%Riesgo%'";
			final StringBuffer masterTableName = new StringBuffer();
			final Long classId = jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, id);
				}
			}, new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					List<String> atrasadas = new LinkedList<String>();
					if (resultSet.next()) {
						masterTableName.append(resultSet.getString("MASTER_TABLE_NAME"));
						return resultSet.getLong("CLASS_ID");
					}
					return new Long(0);
				}
			});

			if (classId == 0)
				return riesgos;

			final String[] fields = new String[] { NOMBRE, IMPACTO, MITIGACION, VARIABLE_AFECTADA, FACTOR_RIESGO };
			final Map<String, String> fieldMap = new HashMap<String, String>(fields.length);

			query = "select cf.field_id, e.element_id, e.element_name, e.db_field_datatype, cf.field_label, cf.data_table_name, "
					+ "cf.data_column_name, cf.data_column_size, cf.data_column_scale, cf.data_column_exists, cf.row_num, "
					+ "cf.row_span, cf.column_num, cf.column_span, cf.field_group, cf.domain_id,cf.max_value, cf.min_value,"
					+ " cf.default_value, ep.default_value as tag, e.element_label, edc.class_id, cf.instructions_clob,"
					+ " cf.record_status, cf.is_multi_select, cf.use_default, cf.column_id, cf.crc, cf.is_value_required, "
					+ "cf.hidden_for_eaf from pn_class_field cf , pn_element e, pn_element_property ep, pn_element_display_class "
					+ "edc where cf.class_id=? and e.element_id = cf.element_id and edc.element_id (+)= cf.element_id and "
					+ "ep.element_id(+)= cf.element_id and ep.property_type(+)='tag' and cf.record_status='A' order by cf.row_num"
					+ " asc, cf.column_num asc";
			jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, classId);
				}
			}, new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					while (resultSet.next()) {
						String fieldName = resultSet.getString("FIELD_LABEL");
						for (int i = 0; i < fields.length; i++) {
							if (fields[i].toLowerCase().contains(fieldName.toLowerCase())) {
								fieldMap.put(fields[i], resultSet.getString("DATA_COLUMN_NAME"));
							}

						}
					}

					return new Long(0);
				}
			});

			query = "select distinct MASTER_TABLE_NAME.object_id, MASTER_TABLE_NAME.version_id, MASTER_TABLE_NAME.seq_num, MASTER_TABLE_NAME.multi_data_seq , "
					+ "MASTER_TABLE_NAME." + fieldMap.get(NOMBRE) + ", MASTER_TABLE_NAME." + fieldMap.get(IMPACTO)
					+ ", MASTER_TABLE_NAME." + fieldMap.get(MITIGACION) + "," + " MASTER_TABLE_NAME."
					+ fieldMap.get(VARIABLE_AFECTADA) + ", MASTER_TABLE_NAME." + fieldMap.get(FACTOR_RIESGO) + ",  "
					+ "pn_class_instance.space_id ,  PN_ASSIGNMENT.ASSIGNOR_ID , TO_CHAR(MASTER_TABLE_NAME.date_modified , 'FMMM/DD/YYYY HH24:MI'), "
					+ "TO_CHAR(MASTER_TABLE_NAME.date_created , 'FMMM/DD/YYYY HH24:MI'), MASTER_TABLE_NAME.modify_person_id , MASTER_TABLE_NAME.create_person_id , "
					+ " PN_ASSIGNMENT.PERSON_ID  , (select count(PN_ENVELOPE_HAS_OBJECT.ENVELOPE_ID) from PN_ENVELOPE_HAS_OBJECT  where "
					+ "PN_ENVELOPE_HAS_OBJECT.OBJECT_ID = MASTER_TABLE_NAME.object_id ) env , MASTER_TABLE_NAME.creator_email as creator_email  from pn_class_instance , "
					+ "MASTER_TABLE_NAME , PN_ASSIGNMENT  , PN_ENVELOPE_HAS_OBJECT  where pn_class_instance.class_id = ? and pn_class_instance.record_status = 'A' "
					+ "and pn_class_instance.class_instance_id = MASTER_TABLE_NAME.object_id  AND PN_ASSIGNMENT.OBJECT_ID(+) = MASTER_TABLE_NAME.object_id AND "
					+ "PN_ASSIGNMENT.RECORD_STATUS(+) = 'A' AND PN_ASSIGNMENT.STATUS_ID(+) <> 70 and is_current=1  and PN_ENVELOPE_HAS_OBJECT.OBJECT_ID(+) ="
					+ " MASTER_TABLE_NAME.object_id  and PN_ENVELOPE_HAS_OBJECT.RECORD_STATUS(+) = 'A' order by MASTER_TABLE_NAME.version_id, "
					+ "MASTER_TABLE_NAME.multi_data_seq asc";

			query = query.replaceAll("MASTER_TABLE_NAME", masterTableName.toString());

			jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, classId);
				}
			}, new ResultSetExtractor<List<Riesgo>>() {
				public List<Riesgo> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

					while (resultSet.next()) {
						Riesgo riesgo = new Riesgo();
						riesgo.setFactorDeRiesgo(resultSet.getString(fieldMap.get(FACTOR_RIESGO)));
						riesgo.setImpacto(resultSet.getString(fieldMap.get(IMPACTO)));
						riesgo.setMitigacion(resultSet.getString(fieldMap.get(MITIGACION)));
						riesgo.setNombre(resultSet.getString(fieldMap.get(NOMBRE)));
						riesgo.setVariableAfectada(resultSet.getString(fieldMap.get(VARIABLE_AFECTADA)));
						riesgos.add(riesgo);
					}

					return riesgos;
				}
			});

		} catch (Exception e) {
		}
		return riesgos;
	}

	@Override
	public SignosVitales getSignosVitales(final Long id) {

		final SignosVitales signosVitales = new SignosVitales();

		// RRHH
		String query = "select * from pn_class where OWNER_SPACE_ID=? and CLASS_NAME like '%RRHH%'";
		{
			final StringBuffer masterTableName = new StringBuffer();
			final Long classId = jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, id);
				}
			}, new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					List<String> atrasadas = new LinkedList<String>();
					if (resultSet.next()) {
						masterTableName.append(resultSet.getString("MASTER_TABLE_NAME"));
						return resultSet.getLong("CLASS_ID");
					}
					return new Long(0);
				}
			});

			if (classId == 0)
				return signosVitales;

			final String INDICADOR = "Nombre Indicador";
			final String PREGUNTA = "Pregunta";
			final String RESPUESTA = "Respuesta";

			final String[] fields = new String[] { INDICADOR, PREGUNTA, RESPUESTA };
			final Map<String, String> fieldMap = new HashMap<String, String>(fields.length);

			query = "select cf.field_id, e.element_id, e.element_name, e.db_field_datatype, cf.field_label, cf.data_table_name, "
					+ "cf.data_column_name, cf.data_column_size, cf.data_column_scale, cf.data_column_exists, cf.row_num, "
					+ "cf.row_span, cf.column_num, cf.column_span, cf.field_group, cf.domain_id,cf.max_value, cf.min_value,"
					+ " cf.default_value, ep.default_value as tag, e.element_label, edc.class_id, cf.instructions_clob,"
					+ " cf.record_status, cf.is_multi_select, cf.use_default, cf.column_id, cf.crc, cf.is_value_required, "
					+ "cf.hidden_for_eaf from pn_class_field cf , pn_element e, pn_element_property ep, pn_element_display_class "
					+ "edc where cf.class_id=? and e.element_id = cf.element_id and edc.element_id (+)= cf.element_id and "
					+ "ep.element_id(+)= cf.element_id and ep.property_type(+)='tag' and cf.record_status='A' order by cf.row_num"
					+ " asc, cf.column_num asc";
			jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, classId);
				}
			}, new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					while (resultSet.next()) {
						String fieldName = resultSet.getString("FIELD_LABEL");
						for (int i = 0; i < fields.length; i++) {
							if (fields[i].toLowerCase().contains(fieldName.toLowerCase())) {
								fieldMap.put(fields[i], resultSet.getString("DATA_COLUMN_NAME"));
							}

						}
					}

					return new Long(0);
				}
			});

			query = "select distinct MASTER_TABLE_NAME.object_id, MASTER_TABLE_NAME.version_id, MASTER_TABLE_NAME.seq_num, MASTER_TABLE_NAME.multi_data_seq , "
					+ "MASTER_TABLE_NAME." + fieldMap.get(INDICADOR) + ", MASTER_TABLE_NAME." + fieldMap.get(PREGUNTA)
					+ ", MASTER_TABLE_NAME." + fieldMap.get(RESPUESTA) + ",  "
					+ "pn_class_instance.space_id ,  PN_ASSIGNMENT.ASSIGNOR_ID , TO_CHAR(MASTER_TABLE_NAME.date_modified , 'FMMM/DD/YYYY HH24:MI'), "
					+ "TO_CHAR(MASTER_TABLE_NAME.date_created , 'FMMM/DD/YYYY HH24:MI'), MASTER_TABLE_NAME.modify_person_id , MASTER_TABLE_NAME.create_person_id , "
					+ " PN_ASSIGNMENT.PERSON_ID  , (select count(PN_ENVELOPE_HAS_OBJECT.ENVELOPE_ID) from PN_ENVELOPE_HAS_OBJECT  where "
					+ "PN_ENVELOPE_HAS_OBJECT.OBJECT_ID = MASTER_TABLE_NAME.object_id ) env , MASTER_TABLE_NAME.creator_email as creator_email  from pn_class_instance , "
					+ "MASTER_TABLE_NAME , PN_ASSIGNMENT  , PN_ENVELOPE_HAS_OBJECT  where pn_class_instance.class_id = ? and pn_class_instance.record_status = 'A' "
					+ "and pn_class_instance.class_instance_id = MASTER_TABLE_NAME.object_id  AND PN_ASSIGNMENT.OBJECT_ID(+) = MASTER_TABLE_NAME.object_id AND "
					+ "PN_ASSIGNMENT.RECORD_STATUS(+) = 'A' AND PN_ASSIGNMENT.STATUS_ID(+) <> 70 and is_current=1  and PN_ENVELOPE_HAS_OBJECT.OBJECT_ID(+) ="
					+ " MASTER_TABLE_NAME.object_id  and PN_ENVELOPE_HAS_OBJECT.RECORD_STATUS(+) = 'A' order by MASTER_TABLE_NAME.version_id, "
					+ "MASTER_TABLE_NAME.multi_data_seq asc";

			query = query.replaceAll("MASTER_TABLE_NAME", masterTableName.toString());

			int rrhhInd = 0;

			rrhhInd += jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, classId);
				}
			}, new ResultSetExtractor<Integer>() {
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					int ind = 0;
					while (resultSet.next()) {

						String respuesta = getValuePnetById(
								Long.parseLong(resultSet.getString(fieldMap.get(RESPUESTA))));
						ind += (respuesta.equalsIgnoreCase("no")) ? -1 : 1;
					}

					return ind;
				}
			});

			signosVitales.setRrhh(rrhhInd);

		}
		// Problemas
		query = "select * from pn_class where OWNER_SPACE_ID=? and CLASS_NAME like '%Problemas%'";
		{
			final StringBuffer masterTableName = new StringBuffer();
			final Long classId = jdbcTemplate.query(query, new PreparedStatementSetter() {

				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, id);
				}
			}, new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					List<String> atrasadas = new LinkedList<String>();
					if (resultSet.next()) {
						masterTableName.append(resultSet.getString("MASTER_TABLE_NAME"));
						return resultSet.getLong("CLASS_ID");
					}
					return new Long(0);
				}
			});

			if (classId == 0)
				return signosVitales;

			final String IMPACTO = "Impacto";
			final String ESTADO = "Estado";

			final String[] fields = new String[] { IMPACTO, ESTADO };
			final Map<String, String> fieldMap = new HashMap<String, String>(fields.length);

			query = "select cf.field_id, e.element_id, e.element_name, e.db_field_datatype, cf.field_label, cf.data_table_name, "
					+ "cf.data_column_name, cf.data_column_size, cf.data_column_scale, cf.data_column_exists, cf.row_num, "
					+ "cf.row_span, cf.column_num, cf.column_span, cf.field_group, cf.domain_id,cf.max_value, cf.min_value,"
					+ " cf.default_value, ep.default_value as tag, e.element_label, edc.class_id, cf.instructions_clob,"
					+ " cf.record_status, cf.is_multi_select, cf.use_default, cf.column_id, cf.crc, cf.is_value_required, "
					+ "cf.hidden_for_eaf from pn_class_field cf , pn_element e, pn_element_property ep, pn_element_display_class "
					+ "edc where cf.class_id=? and e.element_id = cf.element_id and edc.element_id (+)= cf.element_id and "
					+ "ep.element_id(+)= cf.element_id and ep.property_type(+)='tag' and cf.record_status='A' order by cf.row_num"
					+ " asc, cf.column_num asc";
			jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, classId);
				}
			}, new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					while (resultSet.next()) {
						String fieldName = resultSet.getString("FIELD_LABEL");
						for (int i = 0; i < fields.length; i++) {
							if (fields[i].toLowerCase().contains(fieldName.toLowerCase())) {
								fieldMap.put(fields[i], resultSet.getString("DATA_COLUMN_NAME"));
							}

						}
					}

					return new Long(0);
				}
			});

			query = "select distinct MASTER_TABLE_NAME.object_id, MASTER_TABLE_NAME.version_id, MASTER_TABLE_NAME.seq_num, MASTER_TABLE_NAME.multi_data_seq , "
					+ "MASTER_TABLE_NAME." + fieldMap.get(IMPACTO) + ", MASTER_TABLE_NAME." + fieldMap.get(ESTADO)
					+ ", pn_class_instance.space_id ,  PN_ASSIGNMENT.ASSIGNOR_ID , TO_CHAR(MASTER_TABLE_NAME.date_modified , 'FMMM/DD/YYYY HH24:MI'), "
					+ "TO_CHAR(MASTER_TABLE_NAME.date_created , 'FMMM/DD/YYYY HH24:MI'), MASTER_TABLE_NAME.modify_person_id , MASTER_TABLE_NAME.create_person_id , "
					+ " PN_ASSIGNMENT.PERSON_ID  , (select count(PN_ENVELOPE_HAS_OBJECT.ENVELOPE_ID) from PN_ENVELOPE_HAS_OBJECT  where "
					+ "PN_ENVELOPE_HAS_OBJECT.OBJECT_ID = MASTER_TABLE_NAME.object_id ) env , MASTER_TABLE_NAME.creator_email as creator_email  from pn_class_instance , "
					+ "MASTER_TABLE_NAME , PN_ASSIGNMENT  , PN_ENVELOPE_HAS_OBJECT  where pn_class_instance.class_id = ? and pn_class_instance.record_status = 'A' "
					+ "and pn_class_instance.class_instance_id = MASTER_TABLE_NAME.object_id  AND PN_ASSIGNMENT.OBJECT_ID(+) = MASTER_TABLE_NAME.object_id AND "
					+ "PN_ASSIGNMENT.RECORD_STATUS(+) = 'A' AND PN_ASSIGNMENT.STATUS_ID(+) <> 70 and is_current=1  and PN_ENVELOPE_HAS_OBJECT.OBJECT_ID(+) ="
					+ " MASTER_TABLE_NAME.object_id  and PN_ENVELOPE_HAS_OBJECT.RECORD_STATUS(+) = 'A' order by MASTER_TABLE_NAME.version_id, "
					+ "MASTER_TABLE_NAME.multi_data_seq asc";

			query = query.replaceAll("MASTER_TABLE_NAME", masterTableName.toString());

			int problemas = 0;

			problemas += jdbcTemplate.query(query, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, classId);
				}
			}, new ResultSetExtractor<Integer>() {
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					int ind = 0;
					while (resultSet.next()) {
						String impacto = getValuePnetById(Long.parseLong(resultSet.getString(fieldMap.get(IMPACTO))));
						// ind += (impacto.equalsIgnoreCase("no")) ? -1 : 1;
						int impactoInt = 0;
						String estado = getValuePnetById(Long.parseLong(resultSet.getString(fieldMap.get(ESTADO))));
						if (estado.equalsIgnoreCase("vigente")) {
							if (impacto.equalsIgnoreCase("bajo"))
								impactoInt = 1;
							else if (impacto.equalsIgnoreCase("medio"))
								impactoInt = 2;
							else
								impactoInt = 3;

							ind += impactoInt;
						}
					}

					return ind;
				}
			});

			signosVitales.setProblemas(problemas);
		}
		return signosVitales;

	}

	private String getValuePnetById(final Long id) {
		String query = "select domain_value_name from pn_class_domain_values where domain_value_id = ?";

		return jdbcTemplate.query(query, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setLong(1, id);
			}
		}, new ResultSetExtractor<String>() {
			public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				if (resultSet.next()) {
					return resultSet.getString("DOMAIN_VALUE_NAME");
				}
				return "";

			}
		});
	}

}
