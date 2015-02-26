function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

var versionId = getUrlParameter('version');

var lastId;

$(function () {
    updatePlayers();
    $('.show-all').click(function () {
        $('#xml_table').removeClass('show-invalid show-valid');
    });
    $('.show-valid').click(function () {
        $('#xml_table').removeClass('show-invalid');
        $('#xml_table').addClass('show-valid');
    });
    $('.show-invalid').click(function () {
        $('#xml_table').removeClass('show-valid');
        $('#xml_table').addClass('show-invalid');
    });
    $('.show-allusers').click(function () {
        $('#xml_table tr').removeClass('gone');
        $(this).hide();
    }).hide();
});

function updatePlayers() {

    $.get('/api/traces/' + versionId + '/' + (lastId ? lastId : '0'), function (traces) {
        traceAnalyzer.analyze(traces);
        var players = traceAnalyzer.players;
        for (var att in players) {
            var player = players[att];
            if (player.changed) {
                for (i = 0; i < playerViews.length; i++) {
                    var playerView = playerViews[i];
                    playerView(player);
                }
            }
        }
        for (var i = 0; i < traces.length; i++) {
            var trace = traces[i];
            var views = traceViews[trace.event];
            for (var j = 0; views && j < views.length; j++) {
                var view = views[j];
                view(trace);
            }
        }
        if (traces.length > 0) {
            lastId = traces[traces.length - 1]._id;
        }
    });
}

setInterval(updatePlayers, 5000);
