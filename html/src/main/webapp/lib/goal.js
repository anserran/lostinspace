/**
 * Goals
 * @param {String} id           unique identifier for the goal
 * @param {String} category     the goal category
 * @param {Object} defaultValue default result of the goal
 * @param {Function} label      function that takes the result of evaluating the goal, and returns the label for the result
 * @param {Function} analyzer     function that takes one trace and returns the result of the goal
 * @param {Function} comparator function that takes two goal results.  Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
 */
function Goal( id, category, defaultValue, label, analyzer, comparator ){
	this.id = id;
	this.defaultValue = defaultValue;
	this.label = label;
	this.analyzer = analyzer;
	this.comparator = comparator;
}

/**
 * Analyze the goal for the given trace and player, and sets the result in the player
 * @param  {Object} trace            the trace
 * @param  {Object} player           the player
 * @param  {boolean} overrideIfBetter override the result in the player only if the result is better than the current goal result in the player
 */
Goal.prototype.analyze = function( trace, player, overrideIfBetter ){
	var result = this.analyzer( trace );
	var override = !overrideIfBetter || this.comparator( result, player.getGoal(this.id, this.defaultValue)) > 0;
	if ( override ){
		player.setGoal( this.id, result );
	}
};