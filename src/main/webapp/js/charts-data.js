// Flot Charts sample data for SB Admin template

// Flot Line Chart with Tooltips
$(document).ready(
		function() {
			initSelect()

			data = {
				"datosGenerales" : {
					"nombre" : "b",
					"descripcion" : "b",
					"jefe" : "d",
					"sponsor" : "d",
					"lider" : "f"
				}
			}
			function initDatosG() {
				$('#nombreP').text(data.datosGenerales.name)
				$('#jefeProyecto').text(data.datosGenerales.jefeProyecto)
				$('#sponsor').text(data.datosGenerales.sponsor)
				$('#liderProyecto').text(data.datosGenerales.lider)
				$('#descripcion').text(data.datosGenerales.desc)
			}

			function initSignosVitales() {
				$("#costo").attr('data-scale', data.signosVitales.costo)
				$('#avance').attr('data-scale', data.signosVitales.avance)
				$('#documento')
						.attr('data-scale', data.signosVitales.documento)
				$('#rrhh').attr('data-scale', data.signosVitales.rrhh)
				$('#problemas')
						.attr('data-scale', data.signosVitales.problemas)
				$('#cc').attr('data-scale', data.signosVitales.controlDeCambio)
			}

			function initCostos() {
				$('#moneda').text(data.costo.moneda);
				$('.presupuesto').text(data.costo.presupuesto);
				$('#planificadoFecha').text(data.costo.planificadoFecha);
				$('#gastado').text(data.costo.gastado);
				$('#disponible').text(
						data.costo.presupuesto - data.costo.gastado);

				var gastado = data.costo.gastado;
				var pFecha = data.costo.planificadoFecha;
				var indicador = 1;
				if (pFecha == gastado)
					indicador = 0
				else if (pFecha < gastado)
					idicador = 2;

				$("#indicador").attr('data-scale', indicador);

			}

			function initTable() {
				var dataSet = [
						[ "Tiger Nixon", "System Architect", "Edinburgh",
								"5421", "2011/04/25" ],
						[ "Yuri Berry", "Chief Marketing Officer (CMO)",
								"6154", "2009/06/25", "$675,000" ],

				]
				$('#table').DataTable({
					data : dataSet,
					"scrollY" : "280px",
					"scrollCollapse" : true,
					"paging" : false,
					"ordering" : false,
					"info" : false,
					searching : false
				});
			}

			function init(d) {
				data = d
				initDatosG()
				initSignosVitales();
				initCostos();
				initGouge();
				initPieChart(d);
				planificacionChart(d);
				initPlanificacion();
				initTable();

			}

			function initSelect() {
				$.get(
						"api/allprojects",
						function(data) {
							data = JSON.parse(data)
							var select = $('.selectpicker');
							for (var i = 0; i < data.length; i++) {
								select.append($("<option value='" + data[i].id
										+ "'>" + data[i].name + "</option>"))
							}
							select.selectpicker('refresh')
							$('.selectpicker').selectpicker({
								style : 'btn-info',
								size : 4
							});
							select.on('change', function() {
								$.get(
										"api/projectdata?id=" + select.val(),
										function(data) {
											$('#dashboard').css('display',
													'block');
											init(JSON.parse(data))

										}).error(function(e) {
									console.log(e)
								});
							})
						}).error(function(e) {
					console.log(e)
				});

			}

			function initPieChart(d) {
				var disponible = d.costo.presupuesto - d.costo.gastado;
				var gastado = d.costo.gastado;
				if (disponible == 0 && gastado == 0) {
					disponible = 1;
					gastado = 1;
				}
				var data = [ {
					label : "Gastado&nbsp;&nbsp;",
					data : gastado
				}, {
					label : "Disponible",
					data : disponible
				} ];
				$("#pie-chart").empty()
				$.plot($("#pie-chart"), data, {
					series : {
						pie : {
							show : true
						}
					},
					legend : {
						show : true,
						position : "sd",
						noColumns : 2,
						margin : 5
					},
					grid : {
						hoverable : true
					},
					tooltip : true,
					tooltipOpts : {
						content : "%p.0%, %s", // show percentages, rounding to
						// 2 decimal places
						shifts : {
							x : 20,
							y : 0
						}
					}
				});
			}

			function initGouge() {
				$('#g1').empty()
				if ($('#dashboard').is(":visible"))
					g1 = new JustGage({
						id : 'g1',
						value : 0,
						min : 0,
						max : 100,
						symbol : '%',
						pointer : true,
						gaugeWidthScale : 1.4,
						customSectors : [ {
							color : '#ff0000',
							lo : 50,
							hi : 100
						}, {
							color : '#00ff00',
							lo : 0,
							hi : 50
						} ],
						counter : true
					});
			}

			function planificacionChart(d) {
				$('#planificacionChart').empty();
				
				var data = d.plan;

				Morris.Line({
					element : 'planificacionChart',
					data : data,
					xkey : 'fecha',
					ykeys : [ 'plan', 'real' ],
					labels : [ 'Plan', 'Real' ]
				});
			}

			function initPlanificacion() {
				$('#p1').text(data.datosGenerales.porciento);
				$('#p2').text('-');
				$('#p3').text('-');
				$('#p4').text(data.datosGenerales.startDate);
				$('#p5').text(data.datosGenerales.endDate);
			}
		});
