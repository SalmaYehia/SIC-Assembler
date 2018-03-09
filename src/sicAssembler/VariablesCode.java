package sicAssembler;

public class VariablesCode {

	public VariablesCode() {

	}

	public static String checkCharacters(String operand) {
		if (operand.charAt(0) == 'C' || operand.charAt(0) == 'c') {
			StringBuilder objCode = new StringBuilder();
			if (CheckQuotes(operand)) {
				objCode = CharObjCode(operand);
				if (objCode.toString().length() != 0) {
					return objCode.toString();
				}	
			}
		}
		return null;
	}

	public static String checkHexadecimal(String operand) {
		if (operand.charAt(0) == 'X' || operand.charAt(0) == 'x') {
			if (CheckQuotes(operand)) {
				try {
					StringBuilder objCode = new StringBuilder();
					if (objCode.toString().length() != 0) {
						return objCode.toString();
					}
				} catch (Exception e) {
					//
				}
			}
		}
		return null;
	}

	public static String checkNumbers(String operand, int lower, int upper, int d) {
		try {
			StringBuilder objCode = new StringBuilder();
			int x = Integer.parseInt(operand.trim());
			if (x >= lower && x <= upper) {
				String num = String.format("%0" + d + "X", x);
				objCode.append(num.substring(num.length() - 2));
				return objCode.toString();
			}
		} catch (Exception e) {
			//
		}
		return null;
	}

	// check the presence of quotes in X'' and C''
	private static boolean CheckQuotes(String s) {
		if (s.charAt(1) == '\'') {
			if (s.charAt(s.length() - 1) == '\'') {
				return true;
			}
		}
		return false;
		// GenerateError(a, i, "format of data isn't corret");
	}

	// calculate the object code of characters in C''
	private static StringBuilder CharObjCode(String word) {
		StringBuilder objCode = new StringBuilder();
		for (int j = 2; j < word.length() - 1; j++) {
			String hex = ToHex(GetAscii(word.charAt(j)));
			if (hex.length() != 2) {
				// GenerateError(a, i, "charachters aren't in the range");
			}
			objCode.append(hex);
		}
		return objCode;
	}

	// calculate the object code of hexadecimal numbers in X''
	private static StringBuilder HexObjCode(String word) throws Exception {
		String hexN;
		StringBuilder objCode = new StringBuilder();
		for (int j = 2; j < word.length() - 2; j += 2) {
			hexN = word.substring(j, j + 2);
			String num = String.format("%02X", Integer.parseInt(hexN, 16));
			objCode.append(num.substring(num.length() - 2));
		}
		if (word.length() % 2 == 0) {
			throw new Exception();
		}
		return objCode;
	}

	// get the ascii code
	private static int GetAscii(char c) {
		return c;
	}

	// convert decimal numbers to hexadecimal in 2 digits
	private static String ToHex(int i) {
		return String.format("%02X", i);
	}
}
