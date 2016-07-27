package net.sandius.rembulan.compiler;

import net.sandius.rembulan.core.ChunkLoader;
import net.sandius.rembulan.core.Function;
import net.sandius.rembulan.core.LoaderException;
import net.sandius.rembulan.core.Variable;
import net.sandius.rembulan.parser.ParseException;
import net.sandius.rembulan.util.Check;

public class CompilerChunkLoader extends ChunkLoader {

	private final ChunkClassLoader chunkClassLoader;
	private final Compiler compiler;

	private int idx;

	public CompilerChunkLoader(ClassLoader classLoader, Compiler compiler) {
		this.chunkClassLoader = new ChunkClassLoader(classLoader);
		this.compiler = Check.notNull(compiler);
		this.idx = 0;
	}

	public CompilerChunkLoader(
			ClassLoader classLoader,
			CompilerSettings compilerSettings) {
		this(classLoader, new Compiler(compilerSettings));
	}

	public CompilerChunkLoader(ClassLoader classLoader) {
		this(classLoader, CompilerSettings.defaultSettings());
	}

	@Override
	public Function loadTextChunk(Variable env, String chunkName, String sourceText) throws LoaderException {
		try {
			CompiledModule result = compiler.compile(sourceText, "stdin", "f" + (idx++));  // FIXME

			String mainClassName = chunkClassLoader.install(result);
			Class<?> clazz = chunkClassLoader.loadClass(mainClassName);

			return (Function) clazz.getConstructor(Variable.class).newInstance(env);
		}
		catch (ParseException | RuntimeException | LinkageError | ReflectiveOperationException ex) {
			throw new LoaderException(ex);
		}
	}

	@Override
	public Function loadBinaryChunk(Variable env, String chunkName, byte[] bytes, int offset, int len) throws LoaderException {
		throw new UnsupportedOperationException();  // TODO
	}

}
