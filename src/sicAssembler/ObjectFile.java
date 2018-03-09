package sicAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

public class ObjectFile {

	private static String path;
	private static SourceCode source;
	private static Assembler assembler;
	private static final int maxRecordLen = 30;
	private static Formatter f;

	private ObjectFile() {

	}

	public static void createObjectFile(String p, SourceCode s, Assembler a) throws FileNotFoundException {
		path = p;
		source = s;
		assembler = a;
		makeFile();
		printHeaderRecord();
		printTextRecords();
		printEndRecord();
		f.close();
	}

	private static void makeFile() throws FileNotFoundException {
		File file = new File(path);
		f = new Formatter(file);

	}

	private static void printHeaderRecord() {
		String programName = assembler.getProgramName();
		int startingAddress = assembler.getAddress().getFirst();
		int length = assembler.getAddress().getLast() - startingAddress;
		length += assembler.getLengthOfInstruction().getLast();
		f.format("H%-6s%06X%06X\n", programName, startingAddress, length);
	}

	private static void printEndRecord() {
		int startingAddress = assembler.getAddress().getFirst();
		f.format("E%06X\n", startingAddress);
	}

	private static void printTextRecords() {
		for (int i = 1; i < assembler.getAddress().size();) {
			StringBuilder record = new StringBuilder();
			int length = 0, start = assembler.getAddress().get(i);
			// if it is directive end the record except for byte and word
			for (; i < assembler.getAddress().size(); i++) {
				if (assembler.getDirectiveLines().contains(i)) {
					String s = source.getOpCode(i).trim().toLowerCase();
					if (!s.equals("byte") && !s.equals("word")) {
						i++;
						break;
					}
				}
				int LOI = assembler.getLengthOfInstruction().get(i);
				if (length + LOI > maxRecordLen) {
					break;
				}
				length += LOI;
				// record.append("^" + assembler.getObjCode().get(i));
				record.append(assembler.getObjCode().get(i));

			}
			if (length > 0) {
				// f.format("T%06X^%02X%s\n", start, length, record.toString());
				f.format("T%06X%02X%s\n", start, length, record.toString());
			}
		}
	}

}
