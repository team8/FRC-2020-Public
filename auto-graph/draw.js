// function test() {
//     ctx.beginPath();
//     ctx.moveTo(0, height / 2);
//     ctx.lineTo(100, height / 2);
//     ctx.lineWidth = 20;
//     ctx.arc(95, 50, 40, 0, 2 * Math.PI);
//     ctx.stroke();
// }
function drawLines() {
    clear();
    // var canvas = document.getElementById("field");
    ctx.lineCap = "round";
    if ($("#startingX").val() !== '' && $("#startingY") !== '') {
        var initX = $("#startingX").val() * (width / kFieldWidth);
        var initY = height - $("#startingY").val() * (height / kFieldHeight);
    } else {
        initX = 0;
        initY = height - height / 2;
    }
    var a;
    var color = 20;
    var prevX = initX;
    var prevY = initY;
    for (a = 0; a < routines.length; a++) {
        if (typeof routines[a][0] === 'object') {
            ctx.beginPath();
            ctx.moveTo(prevX, prevY);
            ctx.strokeStyle = "rgb(100," + color + ", 10)";
            color += 100;
            for (let i = 0; i < routines[a].length; i++) {
                ctx.lineWidth = 10;
                var x = routines[a][i].x * (width / kFieldWidth) + initX;
                var y = -routines[a][i].y * (height / kFieldHeight) + initY;
                ctx.lineTo(x, y);
                prevX = x;
                prevY = y;
            }
            ctx.stroke();

        }
    }
    //draw points last so they go over lines
    for (a = 0; a < routines.length; a++) {
        if (typeof routines[a][0] === 'object') {
            for (let i = 0; i < routines[a].length; i++) {
                var x = routines[a][i].x * (width / kFieldWidth) + initX;
                var y = -routines[a][i].y * (height / kFieldHeight) + initY;
                drawPoint(x, y)
            }
        }
    }
}
function clear() {
    ctx.clearRect(0, 0, width, height);
    ctx.fillStyle = "#FF0000";
    ctx.drawImage(image, 0, 0, width, height);
}

function drawPoint(x, y) {
    ctx.beginPath();
    ctx.strokeStyle = 'orange';
    ctx.lineWidth = 10;
    ctx.arc(x, y, 5, 0, 2 * Math.PI);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
    // circles.push({
    //     x: x,
    //     y: y,
    //     radius: 5,
    //     color: randomColor()
    // });
}

function addTableDnD() {
    //allow drag and drop for tables
    $("table").tableDnD({});
}