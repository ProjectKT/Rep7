package planner;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.Segment;

import utils.CharArrayStreamTokenizer;

public class OperatorCompiler {
	
	private CharArrayStreamTokenizer st;
	
	public Result compile(String txt) {
		Result result = new Result();
		boolean hasError = false;
		try {
			int offset = 0;
			int token, nextToken;
			char[] charArray = txt.toCharArray();
			st = new CharArrayStreamTokenizer(charArray);
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				try {
					switch (token) {
					case StreamTokenizer.TT_WORD:
						String name = null;
						ArrayList<String> ifList = null;
						ArrayList<String> addList = null;
						ArrayList<String> deleteList = null;
						if ("operator".equalsIgnoreCase(st.sval)) {
							offset = st.getCurrentPosition() - "operator".length() - 1;
							safeNextToken();
							name = st.sval;
							safeNextToken();
							if ("if".equalsIgnoreCase(st.sval)) {
								ifList = new ArrayList<String>();
								safeNextToken();
								while (!"then".equalsIgnoreCase(st.sval)) {
									if ("operator".equalsIgnoreCase(st.sval) || "if".equalsIgnoreCase(st.sval)) {
										throw new IllegalEndException(st.getCurrentPosition() - st.sval.length() - 1);
									}
									ifList.add(st.sval);
									safeNextToken();
								}
								if ("then".equalsIgnoreCase(st.sval)) {
									addList = new ArrayList<String>();
									deleteList = new ArrayList<String>();
									nextToken = safeNextToken();
									while (!"operator".equalsIgnoreCase(st.sval) && nextToken != StreamTokenizer.TT_EOF) {
										if ("add".equalsIgnoreCase(st.sval)) {
											safeNextToken();
											addList.add(st.sval);
										} else if ("delete".equalsIgnoreCase(st.sval)) {
											safeNextToken();
											deleteList.add(st.sval);
										} else {
											throw new IllegalEndException(st.getCurrentPosition() - st.sval.length() - 1);
										}
										nextToken = st.nextToken();
									}
								} else {
									throw new IllegalTokenException(st.sval);
								}
							} else {
								throw new IllegalTokenException(st.sval);
							}
						} else {
							throw new IllegalTokenException(st.sval);
						}
						Operator op = new Operator(name, ifList, addList, deleteList);
						Segment seg = new Segment(charArray, offset, st.getCurrentPosition() - offset);
						result.operators.put(op, seg);
						break;
					default:
						System.out.println(token);
						throw new UnknownTokenException(token);
					}
				} catch (Exception e) {
					hasError = true;
					result.errors.add(e);
					System.out.println(e);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (st != null) {
				st.close();
			}
		}
		
		result.succeeded = !hasError;
		return result;
	}
	
	private int safeNextToken() throws IllegalEndException, IOException {
		int i = st.nextToken();
		if (i == StreamTokenizer.TT_EOF) {
			throw new IllegalEndException(st.getCurrentPosition() - 1);
		}
		return i;
	}
	
	
	public class Result {
		public boolean succeeded = false;
		public HashMap<Operator,Segment> operators = new HashMap<Operator,Segment>();
		public ArrayList<Exception> errors = new ArrayList<Exception>();
		
		@Override
		public String toString() {
			return "scceeded="+succeeded+", operators="+operators;
		}
	}
	
	public class UnknownTokenException extends Exception {
		public int type;
		public UnknownTokenException(int n) {
			super(String.valueOf(n));
			this.type = n;
		}
	}
	public class IllegalTokenException extends Exception {
		public String token;
		public IllegalTokenException(String s) {
			super(s);
			this.token = s;
		}
	}
	public class IllegalEndException extends Exception {
		public int offset;
		public IllegalEndException(int offset) {
			super(String.valueOf(offset));
			this.offset = offset;
		}
	}
	
	public static void main(String[] args) {
		String s = ""
				+ "operator 	\"Z1\" "
				+ "if 	\"?x has hair\" "
				+ "then 	add \"?x is a mammal\"\n delete \"?x has hair\"";
		
		System.out.println(new OperatorCompiler().compile(s));
	}
}
