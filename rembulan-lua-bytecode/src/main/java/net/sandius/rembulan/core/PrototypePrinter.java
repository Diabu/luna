package net.sandius.rembulan.core;

import net.sandius.rembulan.util.Check;
import net.sandius.rembulan.util.IntVector;
import net.sandius.rembulan.util.ReadOnlyArray;

import java.io.PrintStream;

public class PrototypePrinter {

	private static String spaces(int num) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < num; i++) {
			out.append(' ');
		}
		return out.toString();
	}

	public static String opcodeName(int opcode) {
		switch (opcode) {
			case OpCode.MOVE: return "MOVE";
			case OpCode.LOADK: return "LOADK";
			case OpCode.LOADKX: return "LOADKX";
			case OpCode.LOADBOOL: return "LOADBOOL";
			case OpCode.LOADNIL: return "LOADNIL";
			case OpCode.GETUPVAL: return "GETUPVAL";
			case OpCode.GETTABUP: return "GETTABUP";
			case OpCode.GETTABLE: return "GETTABLE";
			case OpCode.SETTABUP: return "SETTABUP";
			case OpCode.SETUPVAL: return "SETUPVAL";
			case OpCode.SETTABLE: return "SETTABLE";
			case OpCode.NEWTABLE: return "NEWTABLE";
			case OpCode.SELF: return "SELF";
			case OpCode.ADD: return "ADD";
			case OpCode.SUB: return "SUB";
			case OpCode.MUL: return "MUL";
			case OpCode.MOD: return "MOD";
			case OpCode.POW: return "POW";
			case OpCode.DIV: return "DIV";
			case OpCode.IDIV: return "IDIV";
			case OpCode.BAND: return "BAND";
			case OpCode.BOR: return "BOR";
			case OpCode.BXOR: return "BXOR";
			case OpCode.SHL: return "SHL";
			case OpCode.SHR: return "SHR";
			case OpCode.UNM: return "UNM";
			case OpCode.BNOT: return "BNOT";
			case OpCode.NOT: return "NOT";
			case OpCode.LEN: return "LEN";
			case OpCode.CONCAT: return "CONCAT";
			case OpCode.JMP: return "JMP";
			case OpCode.EQ: return "EQ";
			case OpCode.LT: return "LT";
			case OpCode.LE: return "LE";
			case OpCode.TEST: return "TEST";
			case OpCode.TESTSET: return "TESTSET";
			case OpCode.CALL: return "CALL";
			case OpCode.TAILCALL: return "TAILCALL";
			case OpCode.RETURN: return "RETURN";
			case OpCode.FORLOOP: return "FORLOOP";
			case OpCode.FORPREP: return "FORPREP";
			case OpCode.TFORCALL: return "TFORCALL";
			case OpCode.TFORLOOP: return "TFORLOOP";
			case OpCode.SETLIST: return "SETLIST";
			case OpCode.CLOSURE: return "CLOSURE";
			case OpCode.VARARG: return "VARARG";
			case OpCode.EXTRAARG: return "EXTRAARG";
			default: throw new IllegalArgumentException("Unknown opcode: " + opcode);
		}
	}

	private static String plural(int n, String sg, String pl) {
		return n + " " + (n == 0 || n > 1 ? pl : sg);
	}

	private static String plural(int n, boolean orMore, String sg, String pl) {
		return n + (orMore ? "+" : "") + " " + (orMore || n == 0 || n > 1 ? pl : sg);
	}

	public static void print(Prototype proto, PrintStream out) {
		print(proto, out, true);
	}

	public static String pseudoAddr(Prototype proto) {
		Check.notNull(proto);
		return "0x" + Integer.toHexString(System.identityHashCode(proto));
	}

	private static String escape(String s) {
		return "\"" + s + "\"";  // FIXME!
	}

	private static String constantToString(Constants constants, int idx) {
		Check.notNull(constants);

		if (constants.isNil(idx)) return LuaFormat.NIL;
		else if (constants.isBoolean(idx)) return LuaFormat.toString(constants.getBoolean(idx));
		else if (constants.isInteger(idx)) return LuaFormat.toString(constants.getInteger(idx));
		else if (constants.isFloat(idx)) return LuaFormat.toString(constants.getFloat(idx));
		else if (constants.isString(idx)) return escape(constants.getString(idx));
		else throw new IllegalArgumentException("Unknown constant #" + idx);
	}

	public static String instructionInfo(int insn) {
		StringBuilder out = new StringBuilder();

		int opcode = OpCode.opCode(insn);
		int a = OpCode.arg_A(insn);
		int b = OpCode.arg_B(insn);
		int c = OpCode.arg_C(insn);
		int bx = OpCode.arg_Bx(insn);
		int sbx = OpCode.arg_sBx(insn);
		int ax = OpCode.arg_Ax(insn);

		String name = opcodeName(opcode);
		out.append(name);  // opcode

		out.append(spaces(9 - name.length()));
		out.append('\t');

		StringBuilder hint = new StringBuilder();

		// instruction arguments
		switch (OpCode.getOpMode(opcode)) {
			case OpCode.iABC:
				out.append(a);
				switch (OpCode.getBMode(opcode)) {
					case OpCode.OpArgN: break;
					case OpCode.OpArgU: out.append(" ").append(b); break;
					case OpCode.OpArgR: out.append(" ").append(b); break;
						case OpCode.OpArgK:
						out.append(" ");
						if (OpCode.isK(b)) {
							out.append(-1 - OpCode.indexK(b));
						}
						else {
							out.append(b);
						}
						break;
				}

				switch (OpCode.getCMode(opcode)) {
					case OpCode.OpArgN: break;
					case OpCode.OpArgU: out.append(" ").append(c); break;
					case OpCode.OpArgR: out.append(" ").append(c); break;
						case OpCode.OpArgK:
						out.append(" ");
						out.append(OpCode.isK(c) ? -1 - OpCode.indexK(c) : c);
						break;
				}
				break;

			case OpCode.iABx:
				out.append(a);
				switch (OpCode.getBMode(opcode)) {
					case OpCode.OpArgN: break;
					case OpCode.OpArgU: out.append(" ").append(bx); break;
					case OpCode.OpArgR: out.append(" ").append(bx); break;
					case OpCode.OpArgK:
						out.append(" ").append(-1 - OpCode.indexK(bx));
						break;
				}
				break;

			case OpCode.iAx:
				out.append(ax);
				break;

			case OpCode.iAsBx:
				out.append(a).append(" ").append(sbx);
				break;
			default:
		}

		return out.toString();
	}

	public static String instructionInfoWithHints(Prototype proto, int pc) {
		Check.notNull(proto);
		Check.nonNegative(pc);

		return instructionInfoWithHints(proto.getCode().get(pc), proto.getConstants(), proto.getNestedPrototypes());
	}

	private static String instructionInfoHints(int insn, Constants constants, ReadOnlyArray<Prototype> children) {
		int opcode = OpCode.opCode(insn);
		int a = OpCode.arg_A(insn);
		int b = OpCode.arg_B(insn);
		int c = OpCode.arg_C(insn);
		int bx = OpCode.arg_Bx(insn);
		int sbx = OpCode.arg_sBx(insn);
		int ax = OpCode.arg_Ax(insn);

		StringBuilder hint = new StringBuilder();

		// instruction arguments
		switch (OpCode.getOpMode(opcode)) {
			case OpCode.iABC:
				if (OpCode.getBMode(opcode) == OpCode.OpArgK) {
					hint.append(OpCode.isK(b) ? constantToString(constants, OpCode.indexK(b)) : "-");
				}

				if (OpCode.getCMode(opcode) == OpCode.OpArgK) {
					if (OpCode.isK(c)) {
						(hint.length() > 0 ? hint : hint.append("-")).append(" ").append(constantToString(constants, OpCode.indexK(c)));
					}
					else {
						(hint.length() > 0 ? hint : hint.append("-")).append(" ").append("-");
					}
				}
				break;

			case OpCode.iABx:
				if (OpCode.getBMode(opcode) == OpCode.OpArgK) {
					hint.append(constantToString(constants, OpCode.indexK(bx)));
				}
				break;

			default:
				break;
		}

		// additional hints
		switch (opcode) {
			case OpCode.CLOSURE:
				hint.append(pseudoAddr(children.get(bx)));
				break;
		}

		return hint.toString();
	}

	public static String instructionInfoWithHints(int insn, Constants constants, ReadOnlyArray<Prototype> children) {
		Check.notNull(constants);
		Check.notNull(children);

		String instrInfo = instructionInfo(insn);
		String hint = instructionInfoHints(insn, constants, children);
		return instrInfo + (!hint.isEmpty() ? "\t; " + hint : "");
	}

	public static void print(Prototype proto, PrintStream out, boolean isMain) {
		Check.notNull(proto);
		Check.notNull(out);

		IntVector code = proto.getCode();
		Constants constants = proto.getConstants();
		ReadOnlyArray<LocalVariable> locals = proto.getLocalVariables();
		ReadOnlyArray<Upvalue.Desc> upvalues = proto.getUpValueDescriptions();
		ReadOnlyArray<Prototype> nested = proto.getNestedPrototypes();

		// 1st line
		out.println(
				(isMain ? "main" : "function")
				+ " <" + proto.getShortSource() + ":" + proto.getBeginLine() + "," + proto.getEndLine() + ">"
				+ " (" + code.length() + " instructions at " + pseudoAddr(proto) + ")"
		);

		// 2nd line
		out.println(
				plural(proto.getNumberOfParameters(), proto.isVararg(), "param", "params")
				+ ", " + plural(proto.getMaximumStackSize(), "slot", "slots")
				+ ", " + plural(upvalues.size(), "upvalue", "upvalues")
				+ ", " + plural(locals.size(), "local", "locals")
				+ ", " + plural(constants.size(), "constant", "constants")
				+ ", " +  plural(nested.size(), "function", "functions")
		);

		// code listing
		for (int i = 0; i < code.length(); i++) {
			out.print('\t');
			out.print(i + 1);  // program counter
			out.print('\t');

			int line = proto.getLineAtPC(i);
			out.print("[" + (line >= 0 ? line : "-") + "]");  // line number

			out.print('\t');

			out.println(instructionInfoWithHints(proto, i));
		}

		// constants
		out.println("constants (" + constants.size() + ") for " + pseudoAddr(proto) + ":");
		for (int i = 0; i < constants.size(); i++) {
			out.print('\t');
			out.print(i + 1);  // index
			out.print('\t');
			out.print(constantToString(constants, i));
			out.println();
		}

		// locals
		out.println("locals (" + locals.size() + ") for " + pseudoAddr(proto) + ":");
		for (int i = 0; i < locals.size(); i++) {
			out.print('\t');
			out.print(i);  // index
			out.print('\t');

			LocalVariable lv = locals.get(i);
			out.print(lv.variableName + "\t" + (lv.beginPC + 1) + "\t" + (lv.endPC + 1));
			out.println();
		}

		// upvalues
		out.println("upvalues (" + upvalues.size() + ") for " + pseudoAddr(proto) + ":");
		for (int i = 0; i < upvalues.size(); i++) {
			out.print('\t');
			out.print(i);  // index
			out.print('\t');

			Upvalue.Desc uvd = upvalues.get(i);
			out.print(uvd.name + "\t" + (uvd.inStack ? 1 : 0) + "\t" + uvd.index);
			out.println();
		}

		// nested prototypes
		for (Prototype np : nested) {
			out.println();
			print(np, out, false);
		}

	}

}
