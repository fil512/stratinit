				google.load("visualization", "1", {packages:["corechart"]});
				google.setOnLoadCallback(drawChart);
				function drawChart() {
				var data = new google.visualization.DataTable();
				data.addColumn('string', 'Year');
				data.addColumn('number', 'Sales');
				data.addColumn('number', 'Expenses');
				data.addRows([
				['2004', 1000, 400],
				['2005', 1170, 460],
				['2006', 660, 1120],
				['2007', 1030, 540]
				]);

				var options = {
				title: '${title}',
				hAxis: {title: 'Year', titleTextStyle: {color: 'red'}},
				isStacked: true
				};

				var chart = new
				google.visualization.AreaChart(document.getElementById('chart_div'));
				chart.draw(data, options);
				}