/**
 * Creates a player
 * @param {String} gameplayId user session id
 */
function Player( gameplayId ){
	this.gameplayId = gameplayId;
	this.name = null;
	this.goals = {};
	this.score = 0;
	this.currentPhase = null;
	this.actionsSent = {};
	this.changed = true;
}

/**
 * Sets the result for a goal
 * @param {String} goalId the goal id
 * @param {Object} result  the result
 */
Player.prototype.setGoal = function( goalId, result ) {
	this.goals[goalId] = result;
	this.changed = true;
};

/**
 * Returns the current result for the goal
 * @param  {String} goalId       Goal id
 * @param  {Object} defaultValue the default result for the goal, in case it's not set in the player
 */
Player.prototype.getGoal = function(goalId, defaultValue) {
	return this.goals[goalId] || defaultValue;
};

/**
 * Sets the current phase for the player
 * @param {String} phaseId the phase identifier
 */
Player.prototype.setCurrentPhase = function(phaseId) {
	this.currentPhase = phaseId;
	this.changed = true;
};

/**
 * Returns the current phase for the player
 * @return {String} the current phase
 */
Player.prototype.getCurrentPhase = function() {
	return this.currentPhase;
};

/**
 * Adds score
 * @param {int} score [description]
 */
Player.prototype.setScore = function(score) {
	this.score = score;
	this.changed = true;
};

/**
 * Returns the player's score
 * @return {int} the player's score
 */
Player.prototype.getScore = function() {
	return this.score;
};

Player.prototype.addActions = function(type, count) {
	if (!actionsSent[type]){
		actionsSent[type] = 0;
	}
	actionsSent[type] += count;
	this.changed = true;
};

Player.prototype.getActions = function(type) {
	return actionsSent[type];
};