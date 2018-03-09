package sicAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class Parser {

	private final static int stLabel = 1;
	private final static int endLabel = 8;
	private final static int stOpCode = 10;
	private final static int endOpCode = 15;
	private final static int stOperand = 18;
	private final static int endOperand = 35;
	private final static int stComment = 36;
	private final static int endComment = 66;
	private final static int[] blankIndex = { 9, 16, 17 };

	private static boolean error = false;

	public Parser() {

	}

	// parse the code from the file add it to source code object
	// and check blank spaces
	public static boolean Parse(String filePath, SourceCode instructions) throws FileNotFoundException {
		error = false;
		File f = new File(filePath);
		Scanner scan = new Scanner(f);
		while (scan.hasNext()) {
			String line = scan.nextLine();
			line = line.replace("\t", "    ");
			if (CheckComment(line)) {
				instructions.AddInstruction(null, null, null, line);
			} else {
				AddInstruction(line, instructions);
				if (!checkBlankSpaces(line)) {
					GenerateError("blank spaces are full", instructions.getSize() - 1, instructions);
				}				
			}
		}
		scan.close();
		return error;
	}

	private static boolean checkBlankSpaces(String line) {
		for (int i = 0; i < blankIndex.length; i++) {
			int x = blankIndex[i] - 1;
			if (line.length() > x && line.charAt(x) != ' ') {
				return false;
			}
		}
		return true;
	}

	private static void GenerateError(String errorMessage, int i, SourceCode s) {
		error = true;
		s.setError(i, errorMessage);
	}

	private static void AddInstruction(String line, SourceCode instructions) {
		if (line == null || line.isEmpty()) { // if line is empty ignore it
			return;
		}

		// add these components and if not found add null
		String label = GetLabel(line);
		String opCode = GetOpCode(line);
		String operand = GetOperand(line);
		String comment = GetComment(line);
		instructions.AddInstruction(label, opCode, operand, comment);

	}

	private static String GetLabel(String line) {
		String s = "";
		if (line.length() >= endLabel) {
			s = line.substring(stLabel - 1, endLabel - 1);
		} else if (line.length() > stLabel) {
			s = line.substring(stLabel - 1, line.length());
		}
		if (!s.trim().isEmpty()) {
			return s;
		}
		return null;
	}

	private static String GetOpCode(String line) {
		String s = "";
		if (line.length() >= endOpCode) {
			s = line.substring(stOpCode - 1, endOpCode - 1);
		} else if (line.length() > stOpCode) {
			s = line.substring(stOpCode - 1, line.length());
		}
		if (!s.trim().isEmpty()) {
			return s;
		}
		return null;
	}

	private static String GetOperand(String line) {
		String s = "";
		if (line.length() >= endOperand) {
			s = line.substring(stOperand - 1, endOperand - 1);
		} else if (line.length() >= stOperand) {
			s = line.substring(stOperand - 1, line.length());
		}
		if (!s.trim().isEmpty()) {
			return s;
		}

		return null;
	}

	private static String GetComment(String line) {
		String s = "";
		if (line.length() >= endComment) {
			s = line.substring(stComment - 1, endComment - 1);
		} else if (line.length() >= stComment) {
			s = line.substring(stComment - 1, line.length());
		}
		if (!s.trim().isEmpty()) {
			return s;
		}
		return null;
	}

	private static boolean CheckComment(String line) {
		if (line.charAt(0) == '.') {
			return true;
		}
		return false;
	}

}
