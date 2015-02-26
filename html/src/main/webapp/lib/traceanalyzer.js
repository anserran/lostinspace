function TraceAnalyzer(){
	this.players = {};
	this.phases = {};
	this.goals = {};
	this.attUpdaters = {};
	this.goalsList = null;
}

TraceAnalyzer.prototype.addGoal = function(tag, goal) {
	if ( !this.goals[tag] ){
		this.goals[tag] = [];
	}
	this.goals[tag].push(goal);
};

TraceAnalyzer.prototype.getGoalsList = function() {
	if ( !this.goalsList){
		var list = [];
		for ( var att in this.goals ){
			list  = list.concat( list, this.goals[att] || []);
		}
		this.goalsList = list;
	}
	return this.goalsList;
};

TraceAnalyzer.prototype.addAttributeUpdater = function(tag, attUpdater) {
	if ( !this.attUpdaters[tag] ){
		this.attUpdaters[tag] = [];
	}
	this.attUpdaters[tag].push(attUpdater);
};

TraceAnalyzer.prototype.analyze = function(traces) {
	for ( var i = 0; i < traces.length; i++){
		var trace = traces[i];
		if ( trace.event === 'game_start'){
			this.addNewPlayer( trace );
		} else {
			var player = this.players[trace.gameplayId];
			if ( player ){
				var goals = this.goals[trace.event];
				for ( var j = 0; goals && j < goals.length; j++){
					goals[j].analyze( trace, player, true );
				}
				var updaters = this.attUpdaters[trace.event];
				for ( j = 0; updaters && j < updaters.length; j++){
					updaters[j].update( trace, player);
				}
			}
		}
	}
	orderByScore();
};

TraceAnalyzer.prototype.addNewPlayer = function(trace) {
	this.players[trace.gameplayId] = new Player(trace.gameplayId);
};