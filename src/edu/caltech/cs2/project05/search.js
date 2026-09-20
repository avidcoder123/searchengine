$(function() {
    var last_active = $("#place-query");

    function update(name) {
        last_active.val(name);
    }

    function clearPrevResults() {
        const prevResultsHeader = document.getElementById("resultsHeader")
        if (prevResultsHeader != null) {
            document.body.removeChild(prevResultsHeader)
        }
        const prevResultsList = document.getElementById("resultsList")
        if (prevResultsHeader != null) {
            document.body.removeChild(prevResultsList)
        }
    }

    $("#place-query").on('focus', function() {
        last_active = $("#place-query");
    });

    $("#all").click(function() {
       clearPrevResults()
       var qstr = $("#place-query").val();
       $.get("/byquery", {'query': qstr}, function(data) {
            const resultsHeader = document.createElement("h2")
            resultsHeader.setAttribute('id', 'resultsHeader');
//            resultsHeader.innerHTML = "<center><h2>For the query \"" + qstr + "\":</h2><center>"
            document.body.appendChild(resultsHeader);
            const resultsList = document.createElement("p")
            resultsList.setAttribute('id', 'resultsList');
            resultsList.innerHTML = ""
            data = data.substring(1, data.length - 1)
            if (data.length == 0) {
                resultsList.innerHTML += "<center>No results exist for this query.</center>"
            }
            else {
                resultsList.innerHTML += "<ul>"
                data = data.split("ඞ")
                for (let i = 0; i < data.length; i++) {
                     resultsList.innerHTML += "<li><a href=\"" + data[i] + "\">" + data[i] + "</a></li>"
                }
                resultsList.innerHTML += "</ul>"
            }
            document.body.appendChild(resultsList)
       })
       update(qstr)
    });
})