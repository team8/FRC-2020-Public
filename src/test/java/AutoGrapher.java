import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AutoGrapher {

	@Test
	public void showAuto() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException, InterruptedException {
		if (!System.getProperty("auto").isEmpty()) {
			String autoPath = "com.palyrobotics.frc2020.auto.modes." + System.getProperty("auto");

			//enter automode here
			Class autoClass = Class.forName(autoPath);

			AutoModeBase auto = (AutoModeBase) autoClass.newInstance();

			ArrayList<String> routineExport = new ArrayList<>();
			autoCompiler(auto.getRoutine(), routineExport);

			//make export string that is readable in js
			String routineString = routineExport.toString();
			routineString = routineString.replace("Pose2d(Translation2d", "");
			routineString = routineString.replace("), Rotation2d(", ", ");
			routineString = routineString.replace("))", ")");
			routineString = routineString.replace("(", "{");
			routineString = routineString.replace(")", "}");

			String[] replace = {"X", "Y", "Rads", "Deg"};
			routineString = addQuotes(replace, routineString);

			String os = System.getProperty("os.name");

			//export string as a text file
			File file = new File("auto-graph/automode.js");
			FileWriter fr = new FileWriter(file);
			fr.write("var importedAuto = " + routineString);
			fr.close();

			if (os.startsWith("Mac")) {
				Runtime.getRuntime().exec(new String[]{"open", "-a", "google chrome", System.getProperty("user.dir") + "/auto-graph/index.html"});
			} else if (os.startsWith("Windows")) {
				Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome " + "file://" + System.getProperty("user.dir") + "/auto-graph/index.html"});
			}

			//clear automode file
			TimeUnit.SECONDS.sleep(5);
			fr = new FileWriter(file);
			fr.write("");
			fr.close();
		}
	}

	public void autoCompiler(Routine routines, ArrayList<String> export) {
		if (routines instanceof SequentialRoutine) {
			ArrayList<Routine> decomposedRoutine = routines.getEnclosingSequentialRoutine();
			for (Routine current : decomposedRoutine) {
				autoCompiler(current, export);
			}
		} else if (routines instanceof DrivePathRoutine) {
			export.add(routines.toString());
		} else {
			export.add("\"" + routines.getName() + "\"");
		}
	}
	private String addQuotes(String[] query, String str) {
		for (String i : query) {
			str = str.replace(i, "\"" + i + "\"");
		}
		return str;
	}
}
