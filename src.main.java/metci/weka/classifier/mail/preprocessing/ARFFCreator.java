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

	public void writeARFFFileFromFolder(String hamsDir, String spamsDir) throws IOException {
		writeARFFHeader();
		writeARFFContent(hamsDir, spamsDir);
	}
	
	public void writeARFFHeader() throws IOException {
		String relationName = "@relation spamOrHam\n\n";
		String classAttr = "@attribute spamclass {spam,ham}\n";
		String attrType = "@attribute text String\n\n";
		String dataAnnotation = "@data\n";

		File file = new File(OUTPUT_DATASET_PATH);

		if (!file.exists()) {
			file.createNewFile();
		}
		else {
			file.delete();
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(relationName + classAttr + attrType + dataAnnotation);
		bw.close();

//		System.out.println("ARFF Header Wrote with Success!");
	}

	public void writeARFFContent(String hamsDir, String spamsDir) throws IOException {

		File file = new File(OUTPUT_DATASET_PATH);

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);

		List<String> hams = readHams(hamsDir);
		List<String> spams = readSpams(spamsDir);

		for (String ham : hams) {
			bw.write("ham,'" + ham + "'\n");
		}
		for (String spam : spams) {
			bw.write("spam,'" + spam + "'\n");
		}
		bw.close();

//		System.out.println("ARFF Content Wrote with Success!");
	}

	private List<String> readSpams(String spamsDir) {
		String directory = spamsDir;
		List<String> spams = new LinkedList<String>();

		try {
			List<File> filesInFolder = Files.walk(Paths.get(directory)).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());
			int controller = 0;
			for (File f : filesInFolder) {
				if(controller > 500)
					break;
				
				List<String> allLines = Files.readAllLines(f.toPath());
				String spam = "";
				for(String line : allLines) {
					spam += line.replaceAll("\\P{Alnum}", " ")+" ";
				}
				spams.add(spam);
				controller++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return spams;

	}

	private List<String> readHams(String hams_dir) {
		String directory = hams_dir;
		List<String> hams = new LinkedList<String>();

		try {
			List<File> filesInFolder = Files.walk(Paths.get(directory))
											.filter(Files::isRegularFile)
											.map(Path::toFile)
											.collect(Collectors.toList());
			int controller = 0;
			for (File f : filesInFolder) {
				if(controller > 500)
					break;
				
				List<String> allLines = Files.readAllLines(f.toPath());
				String ham = "";
				for(String line : allLines) {
					ham += line.replaceAll("\\P{Alnum}", " ")+" ";
				}
				hams.add(ham);
				controller++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return hams;
	}
	
//	public static void main(String[] args) {
//		try {
//			if(args.length < 3) {
//				throw new IllegalArgumentException();
//			}
//			
//			String hamsDir = args[1], spamsDir = args[2];
//			
//			writeARFFHeader();
//			writeARFFContent(hamsDir, spamsDir);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}