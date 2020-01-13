function importData() {
    let importedRoutines = importedAuto;
    console.log(importedRoutines);
    importedRoutines.forEach(function (item) {
        if (Array.isArray(item)) {
            addDrivePathRoutine(item[0].X, item[0].Y, item[0].Deg, false); //change inverse once inverse path is added
            for (var i = 1; i < item.length; i++) {
                importPoint(item[i].X, item[i].Y, item[i].Deg)
            }
        } else {
            importRoutine(item);
        }
        addTableDnD();
        update();
    });
}
function importPoint(x, y, rotation) {
    //insert new waypoint
    var newRow = `
        <tr class="points">
            <td></td>
            <td class="x"><input placeholder='X' value="${x}" type="number"></td>
            <td class="y"><input placeholder='Y' value="${y}" type="number"></td>
            <td class="rotation"><input placeholder='Rotation' value="${rotation}" type="number"></td>
            <td class='comments'><input placeholder='Comments'></td>
            <td></td>
            <td><button onclick='$(this).parent().parent().remove();update()'>Delete</button></td>
        </tr>`;
    $(".points:last").after(newRow);
}

function importRoutine(name) {
    var tables = document.getElementById("Routines");
    var newRow = tables.insertRow();
    newRow.setAttribute("class", "routine");
    var routineHTML = `
        <td><table>
            <thead>
                <tr>
                    <th>${name}</th>
                    <th style=\"width: 600px;\"></th>
                    <th align="right"><button onclick='$(this).parent().parent().parent().parent().remove();update()'>Delete</button></th>
                </tr>
            </thead>
        </table></td>`;
    newRow.innerHTML = routineHTML;
}

// function importData() {
//     $('#upl').click();
//     let u = $('#upl')[0];
//     $('#upl').change(() => {
//         var file = u.files[0];
//         console.log(file);
//         var fr = new FileReader();
//         fr.onload = function (e) {
//             var c = fr.result;
//             var name = file.name;
//
//             lines = c.split(/\r?\n/);
//             // console.log(lines)
//             const constants = new Map();
//             $("tbody").empty();
//             lines.forEach((wpd) => {
//                 data = wpd;
//
//                 if (data.includes("//")) {
//                     // skip comments
//                 } else if (data.includes("final double ")) {
//                     var v = data.replace("final double ", '');
//                     v = v.split(" = ");
//                     v[1] = v[1].replace(/sDistances./g, '');
//                     v[1] = v[1].replace(/PhysicalConstants./g, '');
//                     console.log(typeof(eval(v[1])));
//                     v[0] = v[0].split('k');
//                     v[0][1] = "k" + v[0][1];
//                     constants.set(v[0][1], eval(v[1]));
//                     console.log(constants)
//                 } else if (data.includes(".add(new Path.")) {
//                     data.split("add.");
//                     var wp = data;
//                     wp = wp.replace("Path.", " ");
//                     wp = wp.replace("StartToCargoShip.add", " ");
//                     var keys = constants.keys();
//                     var i;
//                     var check = constants.keys();
//                     console.log(constants.get("kHabLineX"));
//                     console.log(constants);
//                     for (i = 0; i < constants.size; i++) {
//                         check = keys.next().value;
//                         console.log(check);
//                         if (wp.includes(String(check))) {
//                             var a = constants.get(check);
//                             wp = wp.replace(String(check), a);
//                         }
//                     }
//                     console.log(wp);
//                     wp = eval(wp);
//
//                     // wp = eval("(new Waypoint(new Translation2d(0,0),0,));");
//                     // data = data.split(".add(new Path.Waypoint(new Translation2d(");
//                     // cordinate = data[1];
//                     // cordinate = cordinate.split(", ");
//                     // cordinate[1] = cordinate[1].replace(")", '');
//                     // cordinate[2] = cordinate[2].replace("));", '');
//                     // console.log("LOADED")
//                     // console.log(data)
//                     // // var wp = undefined
//                     // // if (blue) {
//                     // wp = new Waypoint(new Translation2d(parseFloat(cordinate[0]) + x, parseFloat(cordinate[1]) + y), cordinate[2], 0, "No Comment");
//                     // }
//                     // else {
//                     // 	var x_off_red = 652
//                     // 	wp = new Waypoint(new Translation2d(-1*parseFloat(data[0])-x + x_off_red, parseFloat(data[1])+y), data[2], 20, "No Comment");
//                     // }
//
//                     // console.log(wp);
//                     //TODO: need fixing
//                     $("tbody").append("<tr>"
//                         + "<td><input value='" + wp.position.x + "'></td>"
//                         + "<td><input value='" + wp.position.y + "'></td>"
//                         + "<td><input value='" + wp.speed + "'></td>"
//                         + "<td class='comments'><input placeholder='Comments' value='" + wp.comment + "'></td>"
//                         + "<td><button onclick='$(this).parent().parent().remove();''>Delete</button></td></tr>"
//                     );
//                 }
//             });
//             update();
//             $('input').unbind("change paste keyup");
//             $('input').bind("change paste keyup", function () {
//                 console.log("change");
//                 clearTimeout(wto);
//                 wto = setTimeout(function () {
//                     update();
//                 }, 500);
//             });
//         };
//         fr.readAsText(file);
//     });
// }