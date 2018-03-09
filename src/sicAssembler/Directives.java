package sicAssembler;

public class Directives {

	private final static int integerLowerBound = -16777216;
	private final static int integerUpperBound = 16777215;
	private final static int byteLowerBound = -128;
	private final static int byteUpperBound = 255;
	private final static int addressLowerBound = 0;
	private final static int addressUpperBound = 32767; // should be less than
														// 0X8000 = 2^15

	public Directives() {

	}

	// word : number (-16777216 => 16777215) OR Characters OR hexadecimal number
	public static int AssWORD(SourceCode s, Assembler a, int i) {
		String operand = s.getOperand(i).trim();
		int address = 0;
		// check if the variable is number
		String objCode = VariablesCode.checkNumbers(operand, integerLowerBound, integerUpperBound, 6);
		if (objCode == null) {
			// check if the variable is hexadecimal
			objCode = VariablesCode.checkHexadecimal(operand);
			if (objCode == null) {
				// check if the variable is characters
				objCode = VariablesCode.checkCharacters(operand);
			}
		}
		if (objCode != null) {
			objCode = CompleteAsWord(objCode).toString();
			address = objCode.length() / 2;
			a.getObjCode().add(objCode);
		} else {
			GenerateError(a, i, "variable is not correct");
		}
		return address;
	}

	// byte : number (-128 => 255) OR Characters OR hexadecimal number
	public static int AssBYTE(SourceCode s, Assembler a, int i) {
		String operand = s.getOperand(i).trim();
		int address = 0;
		// check if the variable is number
		String objCode = VariablesCode.checkNumbers(operand, byteLowerBound, byteUpperBound, 2);
		if (objCode == null) {
			// check if the variable is hexadecimal
			objCode = VariablesCode.checkHexadecimal(operand);
			if (objCode == null) {
				// check if the variable is characters
				objCode = VariablesCode.checkCharacters(operand);
			}
		}
		if (objCode != null) {
			address = objCode.length() / 2;
			a.getObjCode().add(objCode);
		} else {
			GenerateError(a, i, "variable is not correct");
		}
		return address;
	}

	// add zeros to the end of object code to complete it as a word
	private static StringBuilder CompleteAsWord(String code) {
		StringBuilder s = new StringBuilder(code);
		for (int i = s.length(); i % 6 != 0; i++) {
			s.append('0');
		}
		return s;
	}

	public static int AssRESW(SourceCode s, Assembler a, int i) {
		try {
			int n = Integer.parseInt(s.getOperand(i)) * 3;
			if (n <= 0) {
				GenerateError(a, i, "number couldn't be less than 1");
			} else {
				StringBuilder opCode = new StringBuilder();
				for (int j = 0; j < 2 * n; j++) {
					opCode.append("0"); // object code to be written in the
										// listing file
				}
				a.getObjCode().add(opCode.toString());
				return n;
			}
		} catch (Exception e) {
			GenerateError(a, i, "this operand should be number");
		}
		return 0;
	}

	public static int AssRESB(SourceCode s, Assembler a, int i) {
		try {
			int n = Integer.parseInt(s.getOperand(i));
			if (n <= 0) {
				GenerateError(a, i, "number couldn't be less than 1");
			} else {
				StringBuilder opCode = new StringBuilder();
				for (int j = 0; j < 2 * n; j++) {
					opCode.append("0");
				}
				a.getObjCode().add(opCode.toString());
				return n;
			}
		} catch (Exception e) {
			GenerateError(a, i, "this operand should be number");
		}
		return 0;
	}

	// operand of end statement should be the starting address or program
	// name
	public static void AssEND(SourceCode s, Assembler a, int i) {
		a.getObjCode().add(" ");
		String operand = s.getOperand(i);
		try {
			int x;
			if (operand.charAt(0) == '0' && (operand.charAt(1) == 'x') || (operand.charAt(1) == 'X')) {
				operand = operand.substring(2);
				x = Integer.parseInt(operand, 16); // hexadecimal address
													// with leading 0X
			} else {
				x = Integer.parseInt(operand, 16); // hexadecimal number
			}
			if (x == a.getAddress().get(0)) {
				return;
			}
			throw new Exception();
		} catch (Exception e) {
			// label already exist in the symbol table or the program name
			if (!operand.trim().toLowerCase().equals(a.getProgramName().toLowerCase())) {
				if (!a.getSymbols().containsKey(operand.trim())) {
					GenerateError(a, i, "End statement is not correct");
				}
			}
		}
	}

	// there should be operand number or address or hexadecimal address
	// or it can be a variable already in the symbol table
	public static void AssORG(SourceCode s, Assembler a, int i) {
		// check if it is a numerical value
		String operand = s.getOperand(i);
		if (operand == null) {
			GenerateError(a, i, "operand must exist");
			return;
		}
		Integer x;
		operand = operand.trim();
		try {
			if (operand.charAt(0) == '0' && (operand.charAt(1) == 'X') || (operand.charAt(1) == 'x')) {
				operand = operand.substring(2);
				x = Integer.parseInt(operand, 16);
			} else {
				x = Integer.parseInt(operand);
			}
			if (x < addressLowerBound || x > addressUpperBound) {
				GenerateError(a, i, "the number is not correct");
				return;
			}
		} catch (Exception e) {
			// check if it is in the symbol table
			x = a.getSymbols().get(operand);
			if (x == null) {
				GenerateError(a, i, "this variable is not correct");
				return;
			}
		}
		a.getObjCode().add(" ");
		a.setProgramCounter(x);
	}

	// only accept integers and previously assigned variables
	public static void AssEQU(SourceCode s, Assembler a, int i) {
		String operand = s.getOperand(i);
		String label = s.getLabel(i);
		if (operand == null || label == null) {
			GenerateError(a, i, "operand and label must exist");
			return;
		}
		Integer x;
		operand = operand.trim();
		label = label.trim();
		if (operand.equals("*")) {
			x = a.getProgramCounter();
		} else {
			try {
				x = Integer.parseInt(operand);
				if (x < addressLowerBound || x > addressUpperBound) {
					GenerateError(a, i, "the number is not correct");
					return;
				}
			} catch (Exception e) {
				// check if it is in the symbol table
				x = a.getSymbols().get(operand);
				if (x == null) {
					GenerateError(a, i, "this variable is not correct");
					return;
				}
			}
		}
		a.getObjCode().add(" ");
		a.getSymbols().put(label, x);
	}

	public static int AssLTORG(SourceCode s, Assembler a, int i) {
		LiteralTable table = a.getLiteralTable();
		int length = 0;
		int address = a.getProgramCounter();
		for (int j = table.getStartingIndex(); j < table.size(); i++, j++) {
			s.AddInstruction("*", "BYTE", table.getName(j).substring(1), i);
			a.getObjCode().add(i, table.getValue(j));
			a.getAddress().add(i, address + length);
			table.setAddress(j, address + length);
			length += table.getLength(j);
			a.getLengthOfInstruction().add(i, table.getLength(j));
			a.getDirectiveLines().add(i);
		}
		table.setStartingAddress(table.size());
		return length;
	}

	private static void GenerateError(Assembler a, int i, String s) {
		a.GenerateError(i, s);
	}

}
