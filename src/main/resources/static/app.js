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

function connect(directory) {
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

    fetchAsync("http://localhost:8080/connect", directory).then(() => console.log("Connected"))
}

async function fetchAsync (url, directoryPath) {
    console.log("toto " + directoryPath)
    const settings = {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(directoryPath)
    };
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

function showGreeting(listIssuesByFile) {
    $("#greetings").html("");
    for (var fileName in listIssuesByFile) {
        $("#greetings").append("<tr><td>" + fileName + "</td></tr>");
        listIssuesByFile[fileName].forEach(issue =>
            $("#greetings").append("<tr><td>" + issue.severity + " - <b>" + issue.message + "</b> (<i>" + issue.fileName + "</i>) " + issue.code + " </td></tr>")
        )
    }
}

$(function () {
    let formWatch = document.getElementById("formWatch");
    formWatch.addEventListener("submit", (e) => {
        e.preventDefault();
        let directory = document.getElementById("directoryPath");
        connect(directory.value)
    });

    $( "#disconnect" ).click(function() { disconnect(); });
});

