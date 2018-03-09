package sicAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Hashtable;

public class IntermediateFile {

	private static String path;
	private static SourceCode source;
	private static Assembler assembler;
	private static Formatter f;

	public IntermediateFile() {

	}

	public static void createIntrFile(String p, SourceCode s, Assembler a) throws FileNotFoundException {
		path = p;
		source = s;
		assembler = a;
		makeFile();
		printCode();
		printSymbols();
		f.close();
	}

	private static void makeFile() {
		File file = new File(path);
		try {
			f = new Formatter(file);
		} catch (FileNotFoundException e) {
			System.out.println("cannot make a file");
		}

	}

	private static void printCode() {
		for (int i = 0; i < assembler.getAddress().size(); i++) {
			Integer address = assembler.getAddress().get(i);
			if (address == null) {
				// the line is comment
				f.format("\t\t\t\t  %s\n", source.getComment(i));
			} else {
				// adding address
				String addressHex = String.format("%04X  ", address);
				StringBuilder line = new StringBuilder(addressHex);

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
	}

	private static void addErrors(StringBuilder line, int i) {
		String error = source.getError(i);
		if (error != null) {
			line.append(error);
		}
		line.append("\n");
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

	private static void printSymbols() {
		Enumeration<String> symbols = assembler.getSymbols().keys();
		Hashtable<String, Integer> hash = assembler.getSymbols();
		f.format("\n\n symbol table \n\n");
		for (int i = 0; symbols.hasMoreElements(); i++) {
			String key = symbols.nextElement();
			f.format("%-9s\t\t%4X\n", key, hash.get(key));
		}
	}

}
