package cn.edu.scu.jiangpeyton.graph;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodsLocal {
    // 传入参数, 局部变量, 调用方法, 返回值
    public Type returnType;
    public List<Type> parameterTypes;
    public List<SootMethod> callees;
    public List<Type> localVarType; // 仅包括立即数形式, 以防与形参和返回值重复
    public Set<String> localStr;

    public MethodsLocal(SootMethod method) {
        parameterTypes = method.getParameterTypes();
        returnType = method.getReturnType();
        callees = new ArrayList<>();
        localVarType = new ArrayList<>();
        localStr = new HashSet<>();

        if (!method.hasActiveBody()) {
            return;
        }
        UnitPatchingChain chain = method.getActiveBody().getUnits();
        for (Unit unit : chain) {
            try {
                if (JInvokeStmt.class.equals(unit.getClass())) {
                    findCallee((JInvokeStmt) unit);
                } else if (JAssignStmt.class.equals(unit.getClass())) {
                    findLocalVar((JAssignStmt) unit);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                //e.printStackTrace();
            }
        }
    }

    public void findCallee(JInvokeStmt stmt) throws RuntimeException {
        ValueBox exprBox = stmt.getInvokeExprBox();
        AbstractInvokeExpr value = (AbstractInvokeExpr) exprBox.getValue();
        NumberedString sig = value.getMethodRef().getSubSignature();
        SootMethod method = value.getMethodRef().getDeclaringClass().getMethod(sig);
        callees.add(method);
    }

    public void findLocalVar(JAssignStmt stmt) throws ClassCastException{
        /*
        Value value = stmt.getRightOp();
        if (StringConstant.class.equals(value.getClass())) {
            localStr.add(((StringConstant) value).value);
            localVarType.add(value.getType());
        } else if (IntConstant.class.equals(value.getClass())
                || LongConstant.class.equals(value.getClass())
                || DoubleConstant.class.equals(value.getClass())
                || FloatConstant.class.equals(value.getClass())
                || ClassConstant.class.equals(value.getClass())) {
            localVarType.add(value.getType());
        }
        */
        Value left = stmt.getLeftOp();
        Value right = stmt.getRightOp();
        Type type = left.getType();
        if (JimpleLocal.class.equals(left.getClass())) {
            localVarType.add(left.getType());
        }
        if (type.toString().equals("java.lang.String")) {
            if (StringConstant.class.equals(right.getClass())) {
                localStr.add(((StringConstant) right).value);
                localVarType.add(right.getType());
            }
        }
        if(JVirtualInvokeExpr.class.equals(right.getClass())){
            JVirtualInvokeExpr virtualInvoke=(JVirtualInvokeExpr)right;
            NumberedString sig=virtualInvoke.getMethodRef().getSubSignature();
            callees.add(virtualInvoke.getMethodRef().getDeclaringClass().getMethod(sig));
            for(Value arg:virtualInvoke.getArgs()){
                if(StringConstant.class.equals(arg.getClass())){
                    localStr.add(((StringConstant) arg).value);
                }
            }
        }
    }
}
