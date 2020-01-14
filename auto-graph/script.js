var ctx;
var width = 700; //pixels
var height = 365; //pixels
// var kFieldWidth = 652; // in inches
// var kFieldHeight = 324.0; // in inches
var kFieldWidth = 16.5608; // in meters
var kFieldHeight = 8.2296; // in meters
var robotWidth = 27.0; //inches
var robotHeight = 32.0; //inches
var image;
var imageFlipped;
var routines = [[]];

function init() {
    $("#field").css("width", width + "px");
    $("#field").css("height", height+ "px");
    ctx = document.getElementById('field').getContext('2d');
    ctx.canvas.width = width;
    ctx.canvas.height = height;
    ctx.clearRect(0, 0, width, height);
    ctx.fillStyle = "#FFFFFF";
    image = new Image();
    image.src = 'field.png';
    image.onload = function () {
        ctx.drawImage(image, 0, 0, width, height);
    };
    document.addEventListener('keypress', logKey);
    importData();
    //update canvas after 1 sec
    setTimeout(function() { update(); }, 1000);
}

function addDrivePathRoutine(x, y, rotation, inverted) {
    var tables = document.getElementById("Routines");
    var newRow = tables.insertRow();
    newRow.setAttribute("class", "routine");
    var tableHTML = `
    <td><table class="path">
        <thead>
            <tr>
                <td class='name'><input placeholder='Name' style=\"width: 150px;\"></td>
                <th>X</th>
                <th>Y</th>
                <th>Rotation</th>
                <th>Comments</th>`;
    if (inverted) {
        tableHTML += `<th><input class="inverted" type="checkbox" checked>Inverted</th>`;
    } else {
        tableHTML += `<th><input class="inverted" type="checkbox">Inverted</th>`
    }
    tableHTML += `<td><button onclick='$(this).parent().parent().parent().parent().remove();update()'>Delete</button></td>
            </tr>
        </thead>
        <tbody>
            <tr class="points">
                <td></td>
                <td class="x"><input placeholder='X' value="${x}" type="number"></td>
                <td class="y"><input placeholder='Y' value="${y}" type="number"></td>
                <td class="rotation"><input placeholder='Rotation' value="${rotation}" type="number"></td>
                <td class='comments'><input placeholder='Comments'></td>
                <td></td>
                <td><button onclick='$(this).parent().parent().remove();update()'>Delete</button></td>
            </tr>
        </tbody>
        <tfoot>
                <td colspan=\"6\"><div><button class="waypoint">Add Waypoint</button></div></td>
        </tfoot>
    </td>`;
    newRow.innerHTML = tableHTML;
    addTableDnD();
}


function logKey(e) {
    //update when R is pressed
    if (e.code === "KeyR") {
        update();
    }
}

$(document).ready(function(){
    //insert new waypoint
    $(document).on('click', '.waypoint', function(){
        update();
        var newRow = `
        <tr class="points">
            <td></td>
            <td class="x"><input placeholder='X' type="number"></td>
            <td class="y"><input placeholder='Y' type="number"></td>
            <td class="rotation"><input placeholder='Rotation' type="number"></td>
            <td class='comments'><input placeholder='Comments'></td>
            <td></td>
            <td><button onclick='$(this).parent().parent().remove();update()'>Delete</button></td>
        </tr>`;
        $(this).closest("table").find(".points:last").after(newRow);
        addTableDnD();

        //lock starting coord inputs from letters
        $('input[type=number]').numeric();
    });

    //insert routine
    $(document).on('click','.misc', function () {
        update();
        var tables = document.getElementById("Routines");
        var newRow = tables.insertRow();
        newRow.setAttribute("class", "routine");
        var routineName = $(this).attr('id');
        var routineHTML = `
        <td><table>
            <thead>
                <tr>
                    <th>${routineName}</th>
                    <th style=\"width: 600px;\"></th>
                    <th align="right"><button onclick='$(this).parent().parent().parent().parent().remove();update()'>Delete</button></th>
                </tr>
            </thead>
        </table></td>`;
        newRow.innerHTML = routineHTML;
        addTableDnD();
    });
});

function update() {
    routines = [[]];
    $('#Routines tr.routine').each(function (a) {
        //add each routine to an array
        routines.push([]);
        if ($(this).find("table").hasClass("path")) {
            //add waypoints
            var $this = $(this).find("tbody");
            var name = $(this).find(".name input").val();
            var inverted = $(this).find(".inverted").is(":checked");
            $this.find("tr").each(function () {
                var x = $(this).find(".x input").val();
                var y = $(this).find(".y input").val();
                var rotation = $(this).find(".rotation input").val();
                var comments = $(this).find(".comments input").val();
                var waypoint = {x, y, rotation, comments, name, inverted};
                routines[a].push(waypoint);
            });
            drawLines();
        } else {
            //add routine name if it is not a path
            routines[a].push($(this).find('th:first').text());
        }
    });
    //pop extra index in array
    routines.pop();
}

function Dropdown() {
    document.getElementById("Dropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
    if (!event.target.matches('.dropbutton')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
};

function showModal() {
    $(".modal, .shade").removeClass("behind");
    $(".modal, .shade").removeClass("hide");
}

function closeModal() {
    $(".modal, .shade").addClass("hide");
    setTimeout(function () {
        $(".modal, .shade").addClass("behind");
    }, 500);
}