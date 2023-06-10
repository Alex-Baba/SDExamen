var button_number = 1

function incrementCounter(id){
    console.log("a fost apasat " + id);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/increment', true);

    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onreadystatechange = function() { 
        if (this.readyState === XMLHttpRequest.DONE && this.status === 204) {
            console.log("Gud");
        }
    }
    xhr.send('{"id":"' + id + '"}');

}

function creezButoane() {
    var button_id = "Button" + button_number;
    var button = document.createElement("button");
    button.id = button_id;
    button.innerHTML = button_id;
    button.onclick = function(){incrementCounter(button_id)};
    document.body.appendChild(button);
    document.body.appendChild(document.createElement("br"));
    button_number++;
}