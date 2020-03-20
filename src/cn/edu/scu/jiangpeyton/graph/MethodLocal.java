package cn.edu.scu.jiangpeyton.graph;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodLocal {
    public List<Type> parameterTypes; //形参类型
    public List<Type> localVarTypes; //局部变量类型
    public List<SootMethod> callees; //本地调用函数
    public Type returnType; //返回类型
    public Set<String> localStr; //局部变量中的字符串

    public MethodLocal(SootMethod method) {
        parameterTypes = method.getParameterTypes();
        returnType = method.getReturnType();
        callees = new ArrayList<>();
        localVarTypes = new ArrayList<>();
        localStr = new HashSet<>();

        if (!method.hasActiveBody()) {
            return;
        }
        UnitPatchingChain chain = method.getActiveBody().getUnits();
        for (Unit unit : chain) {
            try {
                if (JInvokeStmt.class.equals(unit.getClass())) {
                    // Invoke指令为Smali语法中的方法调用指令
                    findCallee((JInvokeStmt) unit);
                } else if (JAssignStmt.class.equals(unit.getClass())) {
                    // 若为变量声明指令
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
        /**
         * 提取Invoke指令中被调用的方法
         */
        ValueBox exprBox = stmt.getInvokeExprBox();
        AbstractInvokeExpr value = (AbstractInvokeExpr) exprBox.getValue();
        NumberedString sig = value.getMethodRef().getSubSignature();
        SootMethod method = value.getMethodRef().getDeclaringClass().getMethod(sig);
        callees.add(method);
    }

    public void findLocalVar(JAssignStmt stmt) throws ClassCastException {
        /**
         * 提取局部变量类型及其中的字符串常量
         */
        Value left = stmt.getLeftOp();
        Value right = stmt.getRightOp();
        // 获取左值类型
        Type type = left.getType();
        if (JimpleLocal.class.equals(left.getClass())) {
            localVarTypes.add(left.getType());
        }
        if (type.toString().equals("java.lang.String")) {
            // 若左值类型为字符串
            if (StringConstant.class.equals(right.getClass())) {
                // 若右值为字符串常量, 则加入localStr集合
                localStr.add(((StringConstant) right).value);
                //localVarTypes.add(right.getType());
            }
        }
        // 若右值为Invoke指令, 加入callees列表
        if (JVirtualInvokeExpr.class.equals(right.getClass())) {
            JVirtualInvokeExpr virtualInvoke = (JVirtualInvokeExpr) right;
            NumberedString sig = virtualInvoke.getMethodRef().getSubSignature();
            callees.add(virtualInvoke.getMethodRef().getDeclaringClass().getMethod(sig));
            // 对于右值调用方法的参数, 提取其类型及字符常量
            for (Value arg : virtualInvoke.getArgs()) {
                if (StringConstant.class.equals(arg.getClass())) {
                    localStr.add(((StringConstant) arg).value);
                    //localVarTypes.add(arg.getType());
                }
            }
        }
    }
}
