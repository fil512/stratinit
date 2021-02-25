google.load("visualization", "1", {
	packages : [ "corechart" ]
});
google.setOnLoadCallback(draw${unitBaseType}Chart);
function draw${unitBaseType}Chart() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Day');
	var unitTypes = ${unitTypes};
	for ( var i = 0; i < unitTypes.length; ++i) {
		data.addColumn('number', unitTypes[i]);
	}
	data.addRows(${unitTypesBuilt});

	var options = {
		title : '${unitBaseType} Units Built by Day',
		hAxis : {
			title : 'Day'
		},
		backgroundColor: '#F3F3F3',
		chartArea: {
			left: '50'
		},
		isStacked : true
	};

	var chart = new google.visualization.AreaChart(document
			.getElementById('${unitBaseType}_chart_div'));
	chart.draw(data, options);
};
