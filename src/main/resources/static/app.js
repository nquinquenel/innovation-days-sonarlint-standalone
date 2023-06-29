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
            showGreeting(JSON.parse(greeting.body));
        });
    });

    fetchAsyncBody("http://localhost:8080/connect", directory).then(() => console.log("Connected"))
}

async function fetchAsyncBody (url, directoryPath) {
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

async function fetchAsync (url) {
    const settings = {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        }
    };
    let response = await fetch(url, settings);
    return await response.json();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    fetchAsync("http://localhost:8080/disconnect").then(() => console.log("Disconnected"))
}

function showGreeting(listIssuesByFile) {
    $("#greetings").html("");
    for (var fileName in listIssuesByFile) {
        $("#greetings").append("<tr><td>" + fileName + "</td></tr>")
        listIssuesByFile[fileName].forEach(issue => {
            if (issue.severity === 'Information') {
                $("#greetings").append("<tr><td style='padding-left: 50px'><span style='color: cornflowerblue'>" + issue.severity + "</span> - <b>" + issue.message + "</b> <i>" + issue.code + "</i></td></tr>")
            } else if (issue.severity === 'Warning') {
                $("#greetings").append("<tr><td style='padding-left: 50px'><span style='color: orange'>" + issue.severity + "</span> - <b>" + issue.message + "</b> <i>" + issue.code + "</i></td></tr>")
            } else {
                $("#greetings").append("<tr><td style='padding-left: 50px'><span style='color: red'>" + issue.severity + "</span> - <b>" + issue.message + "</b> <i>" + issue.code + "</i></td></tr>")
            }
        })
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
