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
            console.log(greeting)
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

async function fetchAsync (url, directory) {
    const settings = {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(directory)
    };
    console.log(directory)
    console.log(settings)
    let response = await fetch(url, settings);
    return await response.json();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendIssue() {
    stompClient.send("/app/issue", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    let formWatch = document.getElementById("formWatch");
    formWatch.addEventListener("submit", (e) => {
        e.preventDefault();
        let directory = document.getElementById("directoryPath");
        connect()
    });

    $( "#disconnect" ).click(function() { disconnect(); });
});

