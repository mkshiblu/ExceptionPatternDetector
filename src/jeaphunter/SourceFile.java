package jeaphunter;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Class containing information about the source files under considerations
 */
public class SourceFile {
	private CompilationUnit compilationUnit;
	private String filePath;
	private String projectName;

	public SourceFile(CompilationUnit compilationUnit, String filePath) {
		this.compilationUnit = compilationUnit;
		this.filePath = filePath;
	}

	public SourceFile(CompilationUnit compilationUnit, String filePath, String projectName) {
		this(compilationUnit, filePath);
		this.projectName = projectName;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getProjectName() {
		return projectName;
	}
}
