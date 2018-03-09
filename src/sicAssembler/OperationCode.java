package sicAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class OperationCode  {

	private final static String filePath = "operationCodes.txt";
	private static Hashtable<String, Integer> opCode = null;

	public OperationCode() {
	}

	private static void LoadOpCode() {
		File f = new File(filePath);
		opCode = new Hashtable<>();
		try {
			Scanner scan = new Scanner(f);
			while (scan.hasNext()) {
				String operation = scan.next();
				int code = scan.nextInt();
				opCode.put(operation, code);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String GetCode(String operation) {
		if (opCode == null) {
			LoadOpCode();
		}
		if (operation != null) {
			Integer code = opCode.get(operation.trim().toLowerCase());
			if (code == null) {
				return null;
			}
			return String.format("%02X", opCode.get(operation.trim().toLowerCase()));
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		String sHex = "22";
		System.out.println(Integer.parseInt(sHex, 16));
		System.out.println(String.format("%X", Integer.parseInt(sHex, 16) + 1));
	}
}
