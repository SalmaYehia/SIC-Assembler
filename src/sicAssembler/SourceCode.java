package sicAssembler;

import java.util.LinkedList;

public class SourceCode {

	private LinkedList<String> labels;
	private LinkedList<String> opCodes;
	private LinkedList<String> operands;
	private LinkedList<String> comments;
	private LinkedList<String> errors;

	public SourceCode() {
		labels = new LinkedList<String>();
		opCodes = new LinkedList<String>();
		operands = new LinkedList<String>();
		comments = new LinkedList<String>();
		errors = new LinkedList<String>();
	}

	public void AddInstruction(String label, String opCode, String operand, String comment) {
		labels.add(label);
		opCodes.add(opCode);
		operands.add(operand);
		comments.add(comment);
		errors.add(null);
	}
	
	//to add literals to source code
	public void AddInstruction(String label, String opCode, String operand, int i) {
		labels.add(i, label);
		opCodes.add(i, opCode);
		operands.add(i, operand);
		comments.add(i, null);
		errors.add(i, null);
	}

	//get label or null if not found
	public String getLabel(int i) {
		return labels.get(i);
	}

	public String getOpCode(int i) {
		return opCodes.get(i);
	}

	public String getOperand(int i) {
		return operands.get(i);
	}

	public String getComment(int i) {
		return comments.get(i);
	}

	public String getError(int i) {
		return errors.get(i);
	}

	public void setError(int i, String error) {
		String e = getError(i);
		errors.remove(i);
		if (e == null) {
			errors.add(i, error);	
		} else {
			errors.add(i, e + " " + error);
		}
	}

	public int getSize() {
		return labels.size();
	}

}
