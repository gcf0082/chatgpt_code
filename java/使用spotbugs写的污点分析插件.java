import edu.umd.cs.findbugs.BugInstance;

import edu.umd.cs.findbugs.BugReporter;

import edu.umd.cs.findbugs.ba.AbstractIntrospectionVisitor;

import edu.umd.cs.findbugs.ba.AnalysisContext;

import edu.umd.cs.findbugs.ba.ClassContext;

import edu.umd.cs.findbugs.ba.DataflowAnalysisException;

import edu.umd.cs.findbugs.ba.DataflowValue;

import edu.umd.cs.findbugs.ba.XMethod;

import edu.umd.cs.findbugs.bcel.OpcodeStack.Item;

import edu.umd.cs.findbugs.bcel.OpcodeStack.MutableItem;

import edu.umd.cs.findbugs.bcel.OpcodeStack.StackVisitor;

import edu.umd.cs.findbugs.bcel.OpcodeStack.Type;

import edu.umd.cs.findbugs.classfile.FieldDescriptor;

import edu.umd.cs.findbugs.classfile.MethodDescriptor;

import edu.umd.cs.findbugs.classfile.analysis.ControlFlowGraph;

import edu.umd.cs.findbugs.classfile.analysis.MethodInfo;

import edu.umd.cs.findbugs.classfile.analysis.ProgramPoint;

import edu.umd.cs.findbugs.classfile.analysis.SourceInfo;

import edu.umd.cs.findbugs.util.Util;

import java.util.HashMap;

import java.util.Map;

import java.util.Stack;

public class TaintAnalysisPlugin extends AbstractIntrospectionVisitor {

    private BugReporter bugReporter;

    private Map<ProgramPoint, MutableItem> taintValues;

    private Stack<MutableItem> stack;

    public TaintAnalysisPlugin(BugReporter bugReporter) {

        this.bugReporter = bugReporter;

        this.taintValues = new HashMap<>();

        this.stack = new Stack<>();

    }

    @Override

    public void visitClassContext(ClassContext classContext) {

        taintValues.clear();

    }

    @Override

    public void visitMethod(MethodDescriptor methodDescriptor) {

        taintValues.clear();

        stack.clear();

    }

    @Override

    public void visitXMethod(XMethod xMethod) {

        try {

            ControlFlowGraph cfg = xMethod.getControlFlowGraph();

            if (cfg == null) {

                return;

            }

            for (int i = 0; i < cfg.getNumberOfNodes(); i++) {

                ProgramPoint programPoint = cfg.getProgramPoint(i);

                // 分析方法参数和本地变量的污点信息

                for (int j = 0; j < programPoint.getNumberOfLocalVariables(); j++) {

                    DataflowValue localVariable = xMethod.getLocalVariable(j, programPoint);

                    if (localVariable != null && localVariable instanceof MutableItem) {

                        taintValues.put(programPoint.createChild(j), (MutableItem) localVariable);

                    }

                }

                // 分析操作数栈的污点信息

                stack.clear();

                for (int j = 0; j < programPoint.getStackSize(); j++) {

                    DataflowValue operand = xMethod.getStackItem(j, programPoint);

                    if (operand != null && operand instanceof MutableItem) {

                        stack.push((MutableItem) operand);

                    }

                }

                programPoint.accept(new StackVisitor() {

                    @Override

                    public void visitPush(Type type) {

                        if (type.getBasicType() != Type.BT_REFERENCE) {

                            return;

                        }

                        stack.push(new MutableItem(new DataflowValue() {

                            @Override

                            public boolean equals(Object obj) {

                                if (!(obj instanceof MutableItem)) {

                                    return false;

                                }

                                MutableItem other = (MutableItem) obj;

                                return Util.equalObjects(other.getConstant(), getConstant());

                            }

                                                    @Override

                        public Object getConstant() {

                            return null;

                        }

                        @Override

                        public Type getType() {

                            return type;

                        }

                    }));

                }

                @Override

                public void visitLoad(Type type, int index) {

                    if (type.getBasicType() != Type.BT_REFERENCE) {

                        return;

                    }

                    MutableItem value = taintValues.get(programPoint.createChild(index));

                    if (value != null) {

                        stack.push(value);

                    }

                }

                @Override

                public void visitStore(Type type, int index) {

                    if (type.getBasicType() != Type.BT_REFERENCE) {

                        return;

                    }

                    MutableItem value = stack.pop();

                    taintValues.put(programPoint.createChild(index), value);

                }

                @Override

                public void visitBinOp(Type type, int opcode) {

                    if (type.getBasicType() != Type.BT_REFERENCE) {

           
