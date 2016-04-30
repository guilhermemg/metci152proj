package metci.weka.classifier.mail.preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ARFFCreator {
	public static final String OUTPUT_DATASET_PATH = "/tmp/training_dataset.arff";

	public void writeARFFFileFromFolder(String hamsDir, String spamsDir, int percent, int minNumLines) throws IOException {
		writeARFFHeader();
		writeARFFContent(hamsDir, spamsDir, percent, minNumLines);
	}

	public void writeARFFHeader() throws IOException {
		String relationName = "@relation spamOrHam\n\n";
		String classAttr = "@attribute spamclass {spam,ham}\n";
		String attrType = "@attribute text String\n\n";
		String dataAnnotation = "@data\n";

		File file = new File(OUTPUT_DATASET_PATH);

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(relationName + classAttr + attrType + dataAnnotation);
		bw.close();

		// System.out.println("ARFF Header Wrote with Success!");
	}

	public void writeARFFContent(String hamsDir, String spamsDir, int percent, int minNumLines) throws IOException {

		File file = new File(OUTPUT_DATASET_PATH);

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);

		List<String> hams = readHams(hamsDir, percent, minNumLines);
		List<String> spams = readSpams(spamsDir, percent, minNumLines);

		for (String ham : hams) {
			bw.write("ham,'" + ham + "'\n");
		}
		for (String spam : spams) {
			bw.write("spam,'" + spam + "'\n");
		}
		bw.close();

		// System.out.println("ARFF Content Wrote with Success!");
	}

	private List<String> readSpams(String spamsDir, int percent, int minNumLines) {
		String directory = spamsDir;
		List<String> spams = new LinkedList<String>();

		try {
			List<File> filesInFolder = Files.walk(Paths.get(directory)).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());

			System.out.println("Percent: " + percent);
			int N = filesInFolder.size();
			System.out.println("N: " + N);
			double max_i = N * percent / (double) 100;
//			System.out.println("max_i: " + max_i);
			int max_i_int = (int) max_i;
			System.out.println("max_i_int: " + max_i_int);
			
			System.out.println("#total spams files = " + filesInFolder.size());
			
			for (int i = 0; i < max_i_int; i++) {
				File f = filesInFolder.get(i);
				List<String> allLines = Files.readAllLines(f.toPath());
				
				if(minNumLines == 10 && allLines.size() >= 10) {
					String spam = "";
					for (String line : allLines) {
						spam += line.replaceAll("\\P{Alnum}", " ") + " ";
					}
					spams.add(spam);
				}
				if(minNumLines == 100 && allLines.size() >= 100) {
					String spam = "";
					for (String line : allLines) {
						spam += line.replaceAll("\\P{Alnum}", " ") + " ";
					}
					spams.add(spam);
				}
			}
			System.out.println("#spams files in the end: " + spams.size());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return spams;

	}

	private List<String> readHams(String hams_dir, int percent, int minNumLines) {
		String directory = hams_dir;
		List<String> hams = new LinkedList<String>();

		try {
			List<File> filesInFolder = Files.walk(Paths.get(directory)).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());

			System.out.println("Percent: " + percent);
			int N = filesInFolder.size();
			System.out.println("N: " + N);
			double max_i = N * percent / (double) 100;
//			System.out.println("max_i: " + max_i);
			int max_i_int = (int) max_i;
			System.out.println("max_i_int: " + max_i_int);
			
			System.out.println("#total hams files = " + filesInFolder.size());
			
			for (int i = 0; i < max_i_int; i++) {
				File f = filesInFolder.get(i);
				List<String> allLines = Files.readAllLines(f.toPath());
				
				if(minNumLines == 10 && allLines.size() >= 10) {
					String ham = "";
					for (String line : allLines) {
						ham += line.replaceAll("\\P{Alnum}", " ") + " ";
					}
					hams.add(ham);
				}
				if(minNumLines == 100 && allLines.size() >= 100) {
					String ham = "";
					for (String line : allLines) {
						ham += line.replaceAll("\\P{Alnum}", " ") + " ";
					}
					hams.add(ham);
				}
			}
			
			System.out.println("#hams files in the end: " + hams.size());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return hams;
	}

	// public static void main(String[] args) {
	// try {
	// if(args.length < 3) {
	// throw new IllegalArgumentException();
	// }
	//
	// String hamsDir = args[1], spamsDir = args[2];
	//
	// writeARFFHeader();
	// writeARFFContent(hamsDir, spamsDir);
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}