/**
 * Attribute updater
 * @param {String} tag     the tag to watch
 * @param {Function} updater a function taking a trace and an object, that sets in the object the proper attributes according to the trace
 */
function AttributeUpdater( tag, updater ){
	this.tag = tag;
	this.update = updater;
}