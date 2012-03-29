google.load("visualization", "1", {
	packages : [ "corechart" ]
});
google.setOnLoadCallback(drawChart);
function drawChart() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Day');
	var unitTypes = ${unitTypes};
	for ( var i = 0; i < unitTypes.length; ++i) {
		data.addColumn('number', unitTypes[i]);
	}
	data.addRows(${unitTypesBuilt});

	var options = {
		title : 'Units Built by Day',
		hAxis : {
			title : 'Day',
			titleTextStyle : {
				color : 'red'
			}
		},
		isStacked : true
	};

	var chart = new google.visualization.AreaChart(document
			.getElementById('chart_div'));
	chart.draw(data, options);
};
