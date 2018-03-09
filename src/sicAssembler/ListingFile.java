package sicAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;

public class ListingFile {

	private static String path;
	private static SourceCode source;
	private static Assembler assembler;
	private static Formatter f;

	private ListingFile() {

	}

	public static void createListingFile(String p, SourceCode s, Assembler a) throws FileNotFoundException {
		path = p;
		source = s;
		assembler = a;
		makeFile();
		print();
	}

	private static void makeFile() throws FileNotFoundException {
		File file = new File(path);
		f = new Formatter(file);

	}

	private static void print() {
		for (int i = 0; i < source.getSize(); i++) {
			Integer address = assembler.getAddress().get(i);
			if (address == null) {
				// the line is comment
				f.format("\t\t\t\t  %s\n", source.getComment(i));
			} else {
				// adding address
				String addressHex = String.format("%04X  ", address);
				StringBuilder line = new StringBuilder(addressHex);

				// adding object code
				addObjCode(line, i);

				// adding label if exists
				addLabel(line, i);

				// adding operation
				addOperation(line, i);

				// adding operand if exists
				addOperand(line, i);

				// adding comment if exists
				addComment(line, i);

				// adding error if exists
				addErrors(line, i);

				f.format("%s", line.toString());

			}

		}
		f.close();
	}

	private static void addObjCode(StringBuilder line, int i) {
		String objCode = assembler.getObjCode().get(i);
		if (objCode == null) {
			return;
		}
		int l = objCode.length();
		if (l <= 6) {
			line.append(String.format("%-10s", objCode));
		} else {
			line.append(objCode.substring(0, 2) + "...." + objCode.substring(l - 2, l) + "\t");
		}
	}

	private static void addLabel(StringBuilder line, int i) {
		String label = source.getLabel(i);
		if (label == null) {
			label = "";
		}
		line.append(String.format("%-9s ", label));
	}

	private static void addOperation(StringBuilder line, int i) {
		String operation = source.getOpCode(i);
		line.append(String.format("%-6s ", operation)); // in 6 places
	}

	private static void addOperand(StringBuilder line, int i) {
		String operand = source.getOperand(i);
		if (operand == null) {
			operand = "";
		}
		line.append(String.format("%-18s ", operand));
	}

	private static void addComment(StringBuilder line, int i) {
		String comment = source.getComment(i);
		if (comment == null) {
			comment = "";
		}
		line.append(String.format("%-33s ", comment));
	}

	private static void addErrors(StringBuilder line, int i) {
		String error = source.getError(i);
		if (error != null) {
			line.append(error);
		}
		line.append("\n");
	}

}
