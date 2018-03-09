package sicAssembler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class Assembler {

	private final int indexConstant = 32768; // 8000 hexadecimal
	private final int byteLowerBound = -128;
	private final int byteUpperBound = 255;
	private final int maxProgramCounter = 32768;
	private final String noOperandCommands[] = { "rsub" };
	private Hashtable<String, Integer> symbolTable;
	private LiteralTable literalTable;
	private LinkedList<String> objectCode;
	private LinkedList<Integer> address;
	private LinkedList<Integer> lenghtOfInstruction;
	private ArrayList<Integer> DirectiveLines;
	private SourceCode source;
	private int startingAddress = 0;
	private int programCounter;
	private String programName;
	private boolean error = false;
	private boolean end = false;

	public Assembler(SourceCode source, boolean error) {
		objectCode = new LinkedList<String>();
		address = new LinkedList<Integer>();
		symbolTable = new Hashtable<String, Integer>();
		lenghtOfInstruction = new LinkedList<Integer>();
		DirectiveLines = new ArrayList<Integer>();
		literalTable = new LiteralTable();
		this.source = source;
		this.error = error;
	}

	public boolean AssemblePass1() throws Exception {
		pass1();
		return error;
	}

	public boolean AssemblePass2() throws Exception {
		pass2();
		return error;
	}

	private void pass1() throws Exception {
		boolean start = false;
		for (int i = 0; i < source.getSize(); i++) {
			if (!isBlanck(i)) {
				if (!start) {
					// if it is not a start line, error is generated
					checkStart(i);
					start = true;
				} else {
					if (source.getLabel(i) != null && source.getLabel(i).trim().equals("*")) {
						continue;
					}
					if (end) {
						GenerateError(i, "no instructions after end");
					}
					if (source.getOpCode(i) == null) {
						GenerateError(i, "there is no instruction");
					} else {
						if (!isDirectives(i)) {
							DirectInstruction(i);
						}
					}
					if (programCounter > maxProgramCounter) {
						GenerateError(i, "address out of range");
					}
				}
			}
		}
	}

	// check if the line is directive and do the instruction
	private boolean isDirectives(int i) throws Exception {
		String command = source.getOpCode(i).trim().toLowerCase();
		int l = 0;
		switch (command) {
		case "start":
			GenerateError(i, "duplicate start");
		case "word":
			l = Directives.AssWORD(source, this, i);
			break;
		case "byte":
			l = Directives.AssBYTE(source, this, i);
			break;
		case "resw":
			l = Directives.AssRESW(source, this, i);
			break;
		case "resb":
			l = Directives.AssRESB(source, this, i);
			break;
		case "end":
			Directives.AssEND(source, this, i);
			end = true;
			CheckLabel(i);
			AddLine(programCounter, l);
			DirectiveLines.add(i);
			programCounter += Directives.AssLTORG(source, this, i + 1);
			return true;
		case "org":
			AddLine(programCounter, l);
			Directives.AssORG(source, this, i);
			CheckLabel(i);
			DirectiveLines.add(i);
			return true;
		case "equ":
			Directives.AssEQU(source, this, i);
			AddLine(programCounter, l);
			DirectiveLines.add(i);
			return true;
		case "ltorg":
			AddLine("", programCounter, 0);
			l = Directives.AssLTORG(source, this, i + 1);
			CheckLabel(i);
			programCounter += l;
			DirectiveLines.add(i);
			return true;
		default:
			return false;
		}
		CheckLabel(i);
		AddLine(programCounter, l);
		programCounter += l;
		DirectiveLines.add(i);
		return true;
	}

	private void DirectInstruction(int i) {
		CheckLiteral(i);
		CheckLabel(i);
		CheckOperand(i); // check if there is operand or generate error
		AddLine(null, programCounter, 3);
		programCounter += 3;
	}

	private void CheckOperand(int i) {
		String operand = source.getOperand(i);
		String instruction = source.getOpCode(i).trim().toLowerCase();
		boolean noOperand = false;
		for (int j = 0; j < noOperandCommands.length; j++) {
			if (instruction.equals(noOperandCommands[j])) {
				noOperand = true;
			}
		}
		if (operand == null && !noOperand) {
			GenerateError(i, "there is no operand");
		} else if (operand != null && noOperand) {
			GenerateError(i, "operand shouldn't exist");
		}
	}

	private boolean CheckLabel(int i) {
		String label = source.getLabel(i);
		if (label != null) {
			label = label.trim();
			if (symbolTable.containsKey(label)) {
				GenerateError(i, "duplicate label");
			}
			if (label.equals("*")) {
				GenerateError(i, "label can't be *");
			}
			symbolTable.put(label.trim(), programCounter);
			return true;
		}
		return false;
	}

	private void CheckLiteral(int i) {
		String operand = source.getOperand(i);
		int length = 0;
		if (operand != null) {
			operand = operand.trim();
			if (operand.charAt(0) == '=') {
				if (literalTable.contains(operand)) {
					return;
				}
				operand = operand.substring(1);
				// check if the variable is number
				String objCode = VariablesCode.checkNumbers(operand, byteLowerBound, byteUpperBound, 2);
				if (objCode == null) {
					// check if the variable is hexadecimal
					objCode = VariablesCode.checkHexadecimal(operand);
				}
				if (objCode == null) {
					// check if the variable is characters
					objCode = VariablesCode.checkCharacters(operand);
				}
				if (objCode != null) {
					length = objCode.length() / 2;
					literalTable.addLiteral(source.getOperand(i).trim(), objCode, length);
				} else {
					GenerateError(i, "literal is not correct");
				}
			}
		}
	}

	// check if the line is commented
	private boolean isBlanck(int i) {
		if (source.getOpCode(i) == null && source.getLabel(i) == null && source.getOperand(i) == null) {
			// the line is commented
			AddLine(null, null, null);
			return true;
		}
		return false;
	}

	private void checkStart(int i) throws Exception {
		if (source.getOpCode(i).toLowerCase().trim().equals("start")) {
			try {
				// there must be a hexadecimal operand as address
				String operand = source.getOperand(i).trim();
				// address with leading 0X
				if (operand.charAt(0) == '0' && (operand.charAt(1) == 'X') || (operand.charAt(1) == 'x')) {
					operand = operand.substring(2);
				}
				startingAddress = Integer.parseInt(operand, 16);

				programName = source.getLabel(i).trim(); // there must be label
				if (programName.length() > 6) {
					GenerateError(i, "program name must be less than 7 characters");
				}
				programCounter = startingAddress;
			} catch (Exception e) {
				GenerateError(i, "Start sentence not found or isn't correct");
			}
		} else {
			GenerateError(i, "Start sentence found");
		}
		AddLine("", startingAddress, 0);
	}

	private void pass2() throws Exception {
		StringBuilder code;
		int i = 0;

		for (i = 1; i < source.getSize(); i++) {
			try {
				if (!DirectiveLines.contains(i)) { // not directive
					code = new StringBuilder();
					String operationCode = OperationCode.GetCode(source.getOpCode(i));
					if (operationCode == null) {
						GenerateError(i, "instruction is not correct");
						continue;
					}
					code.append(operationCode);
					String operand = source.getOperand(i);
					if (operand != null) {
						operand = operand.trim();
						if (operand.charAt(0) == '=') {
							code.append(ToHex(literalTable.getaddress(operand)));
						} else if (operand.charAt(0) == '0' && (operand.charAt(1) == 'X')
								|| (operand.charAt(1) == 'x')) { // hexa address
							operand = operand.substring(2);
							Integer.parseInt(operand, 16);
							code.append(operand);
						} else if (operand.indexOf(",x") > 0 || operand.indexOf(",X") > 0) { // index
							code.append(
									ToHex(symbolTable.get(operand.substring(0, operand.length() - 2)) + indexConstant));
						} else {
							code.append(ToHex(symbolTable.get(operand)));
						}
					} else if (!isBlanck(i)) {
						while (code.length() < 6) {
							code.append('0');
						}
					}
					objectCode.remove(i);
					objectCode.add(i, code.toString());
				}
			} catch (Exception e) {
				GenerateError(i, "variables are not correct ");
			}

		}

	}

	// if line is comment (null, null, null)
	private void AddLine(String objectCode, Integer address, Integer length) {
		this.address.add(address);
		this.objectCode.add(objectCode);
		this.lenghtOfInstruction.add(length);
	}

	private void AddLine(Integer address, Integer length) {
		this.address.add(address);
		this.lenghtOfInstruction.add(length);
	}

	public void GenerateError(int i, String s) {
		error = true;
		source.setError(i, s);
	}

	private static String ToHex(int i) {
		return String.format("%04X", i);
	}

	public void setProgramCounter(int i) {
		programCounter = i;
	}

	public String getProgramName() {
		return programName;
	}

	public LinkedList<String> getObjCode() {
		return objectCode;
	}

	public LinkedList<Integer> getAddress() {
		return address;
	}

	public Hashtable<String, Integer> getSymbols() {
		return symbolTable;
	}

	public ArrayList<Integer> getDirectiveLines() {
		return DirectiveLines;
	}

	public LinkedList<Integer> getLengthOfInstruction() {
		return lenghtOfInstruction;
	}

	public LiteralTable getLiteralTable() {
		return literalTable;
	}

	public int getProgramCounter() {
		return programCounter;
	}

}
