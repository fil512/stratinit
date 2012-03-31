google.load("visualization", "1", {
	packages : [ "corechart" ]
});
google.setOnLoadCallback(drawChart);
function drawChart() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Task');
	data.addColumn('number', 'Hours per Day');
	data.addRows([ [ 'Work', 11 ], [ 'Eat', 2 ], [ 'Commute', 2 ],
			[ 'Watch TV', 2 ], [ 'Sleep', 7 ] ]);

	var options = {
		title : 'My Daily Activities'
	};

	var chart = new google.visualization.PieChart(document
			.getElementById('${gameId}_chart_div'));
	chart.draw(data, options);
}