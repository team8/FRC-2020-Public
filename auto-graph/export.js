var strRoutines = '';

function RoutineParser() {
    strRoutines = '';
    var i;
    for (i = 0; i < routines.length; i++) {
        if (typeof routines[i][0] === 'object') {
            strRoutines +=
                `\n        List<Pose2d> ${routines[i][0].name} = new ArrayList<>();`;
            var a;
            for (a = 0; a < routines[i].length; a++) {
                strRoutines +=
                    `\n        ${routines[i][a].name}.add(new Pose2d(${routines[i][a].x}, ${routines[i][a].y}, Rotation2d.fromDegrees(${routines[i][a].rotation})));`
            }
            if (routines[i][0].inverted) {
                strRoutines +=
                    `\n        routines.add(new DrivePathRoutine(${routines[i][0].inverted}, ${routines[i][0].name}))`
            } else {
                strRoutines +=
                    `\n        routines.add(new DrivePathRoutine(${routines[i][0].name}))`
            }
        } else {
            strRoutines +=
                `\n        //${routines[i][0]}`;
            // switch (routines[i][0]) {
            //     case "Intake Routine":
            //         strRoutines +=
            //             `\n        intake code not implemented yet`;
            //         break;
            //     case "Expel Routine":
            //         strRoutines +=
            //             `\n        expel code not implemented yet`;
            //         break;
            //     case "Elevator Routine":
            //         strRoutines +=
            //             `\n        elevator code not implemented yet`;
            //         break;
            // }
        }
    }
}


function getDataString() {
    var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";

    RoutineParser();

    var strStart = `package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.List;

public class ${title} extends AutoModeBase {

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void preStart() {
    }

    @Override
    public Routine getRoutine() {
        ArrayList<Routine> routines = new ArrayList<>();
`;
    var strEnd = `
    }
    @Override
    public String getKey() {
        return null;
    }
}
`;
    return strStart + strRoutines + strEnd;
}

function exportData() {
    update();
    var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
    var blob = new Blob([getDataString()], {type: "text/plain;charset=utf-8"});
    saveAs(blob, title + ".java");
}

function showData() {
    update();
    var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
    $("#modalTitle").html(title + ".java");
    $(".modal > pre").text(getDataString());
    showModal();
}
