package sicAssembler;

import java.io.File;

public class Controller {

	private String folderPath;
	private String sourcePath;
	private String fileName;

	public Controller(String folderPath, String sourcePath, String fileName) {
		this.folderPath = folderPath;
		this.sourcePath = sourcePath;
		this.fileName = fileName;

	}

	public void Assemble() throws Exception {
		SourceCode s = new SourceCode();
		Assembler a = new Assembler(s, Parser.Parse(sourcePath, s));
		boolean error = a.AssemblePass1();
		IntermediateFile.createIntrFile(folderPath + File.separator + fileName + ".out", s, a);
		if (!error) {
			error = a.AssemblePass2();
			ListingFile.createListingFile(folderPath + File.separator + fileName + ".lst", s, a);
		}
		if (!error) {
			ObjectFile.createObjectFile(folderPath + File.separator + fileName + ".obj", s, a);
		}
	}

}
