var traceAnalyzer = new TraceAnalyzer();

// Goals
var goal;
var label;
var analyzer;
var comparator;

// XML
// Simple documents
label = function( result ){
	return (result ? 'Can create simple documents' : 'Can not create simple documents');
};

analyzer = function( trace ){
	return ( trace.value1 === 'xml_valid' );
};

comparator = function( value1, value2 ){
	return (value1 ? 1 : ( value1 == value2 ? 0 : -1 ));
};

goal = new Goal('simple_document', 'xml', false, label, analyzer, comparator);
goal.img = 'simple-document';

traceAnalyzer.addGoal('xml', goal);
// Documents with attributes
label = function( result ){
	return (result ? 'Can use attributes' : 'Can not use attributes');
};

analyzer = function( trace ){
	return ( trace.value1 === 'xml_valid' && trace.value2.indexOf('\"') !== -1 );
};

goal = new Goal('attributes', 'xml', false, label, analyzer, comparator);
goal.img = 'attributes';

traceAnalyzer.addGoal('xml', goal);
// Complex documents
label = function( result ){
	return (result ? 'Can use complex documents' : 'Can not use complex documents');
};

analyzer = function( trace ){
	return ( trace.value1 === 'xml_valid' && trace.value2.length > 80 );
};

goal = new Goal('complex_document', 'xml', false, label, analyzer, comparator);
goal.img = 'complex-document';

traceAnalyzer.addGoal('xml', goal);

// Programming
// Sequence
label = function( result ){
	return (result ? 'Used commands sequences' : 'Did not use commands sequences');
};

analyzer = function( trace ){
	return ( trace.value1 === 'xml_valid' && trace.value2.length > 40 );
};

goal = new Goal('sequence', 'xml', false, label, analyzer, comparator);
goal.img = 'sequence';
traceAnalyzer.addGoal('xml', goal);
// Loop
label = function( result ){
	return (result ? 'Used loops' : 'Did not use loops');
};

analyzer = function( trace ){
	return ( trace.value1 === 'xml_valid' && trace.value2.indexOf('loop') !== -1 );
};

goal = new Goal('loop', 'xml', false, label, analyzer, comparator);
goal.img = 'loop';

traceAnalyzer.addGoal('xml', goal);
// Attribute updater
var attributeUpdater;
var updater;
// Name
updater = function( trace, player ){
	player.name = trace.value1;
};
attributeUpdater = new AttributeUpdater('name', updater);
traceAnalyzer.addAttributeUpdater( 'name', attributeUpdater );

// Phase
updater = function( trace, player ){
	player.setCurrentPhase(trace.target);
};
attributeUpdater = new AttributeUpdater('phase_start', updater);
traceAnalyzer.addAttributeUpdater( 'phase_start', attributeUpdater );

// Score
updater = function( trace, player ){
	player.setScore(trace.value1.b || trace.value1);
};
attributeUpdater = new AttributeUpdater('score', updater);
traceAnalyzer.addAttributeUpdater( 'score', attributeUpdater );

// Views

// Trace views
function processXML( trace ){
	var player = traceAnalyzer.players[trace.gameplayId];
	var link = $('<a href="#" class="label label-info"/>').text( player.name ? player.name : 'unknown' );
	link.click(showUser);
	var row = $('<tr class="' + ( trace.value1 === 'xml_invalid' ? 'row-invalid': 'row-valid') +'"/>');
	var xml = $('<pre/>').text( trace.value2 );
	row.append($('<td/>').append(link));
	row.append($('<td/>').append(xml));
	row.attr('data-user', trace.gameplayId);
	$('#xml_table').prepend(row);
}

function showUser( ){
	var user = $(this).parent().parent().attr('data-user');
	$('#xml_table tr[data-user!="' + user + '"]').addClass('gone');
	$('.show-allusers').show();
}

function showUserModel(){
	var userId = $(this).attr('data-user');
	var player = traceAnalyzer.players[userId];
	$('#user-modal .username').text('Results of "' + player.name + '"');
	$('#user-modal .phase').text(player.getCurrentPhase());
	$('#user-modal .score').text(player.getScore());
	var list = $('#user-modal .goals');
	list.empty();
	var goals = traceAnalyzer.getGoalsList();
	for ( var i = 0; i < goals.length; i++ ){
		var goal = goals[i];
		var result = player.getGoal( goal.id, goal.defaultValue );
		list.append('<li' + ( result ? ' class="correct"' : '' ) + '>' + goal.label(result) + '</li>');
	}
	$('#user-modal').modal();
}

function processName( trace ){
	var row = $('<tr/>');
	var session = $('<td><small class="label">' + trace.gameplayId.substring(0, 6) + '</small></td>');
	var linkName = $('<a href="#" data-user="' + trace.gameplayId + '">' + trace.value1 + '</a>');
	linkName.click( showUserModel );
	var name = $('<td/>');
	name.append(linkName);
	var phase = $('<td class="phase"><code>None</code></td>');
	var score = $('<td class="score">0</td>');
	var goals = $('<td class="goals"></td>');
	var goalsList = traceAnalyzer.getGoalsList();
	for ( var i = 0; i< goalsList.length; i++ ){
		var def = goalsList[i];
		goals.append('<img title="' + def.label(def.defaultValue) + '"class="semi-transparent" data-goal="' + def.id + '" src="img/' + def.img + '.png"/>');
	}
	row.append(session);
	row.append(name);
	row.append(phase);
	row.append(score);
	row.append(goals);
	row.attr('data-user', trace.gameplayId);
	$('#users').append(row);
}

// Player views
function processPhaseStart( player ){
	$('#users tr[data-user="' + player.gameplayId + '"] code').text(player.getCurrentPhase());
}

function processScore( player ){
	$('#users tr[data-user="' + player.gameplayId + '"] .score').text(player.getScore());
}

function processGoals( player ){
	var goals = traceAnalyzer.getGoalsList();
	for ( var i = 0; i< goals.length; i++ ){
		var goal = goals[i];
		var result = player.getGoal( goal.id, goal.defaultValue );
		var img = $('#users tr[data-user="' + player.gameplayId + '"] .goals img[data-goal="' + goal.id + '"]');
		if ( result ){
			img.removeClass('semi-transparent');
		}
		img.attr('title', goal.label(result));
	}
}

function orderByScore(){
	var players = [];
	var table = $('#users');
	for (var key in traceAnalyzer.players) {
		players.push(traceAnalyzer.players[key]);
	}
	players = _.sortBy(players, function(player){
		return -player.score;
	});
	_.each(players, function( p ){
		var row = $('#users tr[data-user="' + p.gameplayId + '"]');
		row.remove();
		table.append(row);
	});
}

var traceViews = {
	'xml': [ processXML ],
	'name': [ processName ]
};
var playerViews = [ processPhaseStart, processScore, processGoals ];


