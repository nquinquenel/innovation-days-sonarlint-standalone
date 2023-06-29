var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const socket = new SockJS('/sonarlint-socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/issue', function (greeting) {
            console.log(JSON.parse(greeting.body))
            showGreeting(JSON.parse(greeting.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(listIssues) {
    listIssues.forEach(element => $("#greetings").append("<tr><td>" + element.severity + " - <b>" + element.message + "</b> (<i>" + element.fileName + "</i>) " + element.code + " </td></tr>"));
}

$(function () {
    let formWatch = document.getElementById("formWatch");
    formWatch.addEventListener("submit", (e) => {
        e.preventDefault();
        // let directory = document.getElementById("directoryPath");
        connect()
    });

    $( "#disconnect" ).click(function() { disconnect(); });
});

