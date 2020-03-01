package shitter;

import java.io.File;
import java.io.OutputStream;

/**
 * The shitter is a "virus" that reads files, shatters and torn apart them in
 * random tiny pieces and spread those pieces in the folder they are located,
 * removing then the original file.<br>
 * Each "file-type handler" must be capable of re-assembling back the pieces
 * into the original file.<br>
 * One working example is {@link TextDisassembler}.
 */
public class Shitter {
	public static final int MAX_SIZE_BYTE = 64;

	/**
	 * Could generalise the output defining a factory for the instance of
	 * {@link OutputStream}.
	 */
	public static abstract class FileDisassemblerHandler {
		public static final String SEPARATOR_NAME_INDEX = " - ";

		public abstract boolean canHandle(File f);

		public final void disassemble(File f) {
			if (f == null)// || indexesStream == null)
				return;
			if (canHandle(f))
				disassembleImpl(f);
		}

		public abstract void disassembleImpl(File f);

		public final void reAssemble(String originalPathAndFilename) {
			File f;
			if (originalPathAndFilename == null)// || indexesStream == null)
				return;
			f = new File(originalPathAndFilename);
			if (canHandle(f))
				reAssembleImpl(originalPathAndFilename, f);
		}

		public abstract void reAssembleImpl(String originalPathAndFilename, File f);
	}

	public static enum DisassemblerImplemented {
		PlainText(new TextDisassembler()), Image(null), RawBinary(new BinaryDisassembler());
		DisassemblerImplemented(FileDisassemblerHandler d) {
			disassembler = d;
		}

		public final FileDisassemblerHandler disassembler;
	}

	//

	//

	public Shitter() {
	}

	//

	//

}