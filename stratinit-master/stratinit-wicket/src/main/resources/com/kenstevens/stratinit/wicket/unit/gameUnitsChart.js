google.load("visualization", "1", {
	packages : [ "corechart" ]
});
google.setOnLoadCallback(drawChart);
function drawChart() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Unit');
	data.addColumn('number', 'Love');
	data.addRows(${unitLove});

	var options = {
		title : 'Units Built in Game #${gameId}',
		chartArea: {
			left: '50',
			top: '50'
		},
		backgroundColor: '#F3F3F3'
	};

	var chart = new google.visualization.PieChart(document
			.getElementById('${gameId}_chart_div'));
	chart.draw(data, options);
}