package tellit.com.tellit.tools.log;

import android.util.Log;

import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.U;

public class TraceHelper {
    private static boolean isPrintToLog = true;
//    private static final String TRACER = "Tracer";
    private static final int CLIENT_CODE_STACK_INDEX;

    static {
        int i = 0;
        try {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                i++;
                if (ste.getClassName().equals(TraceHelper.class.getName())) {
                    break;
                }
            }
        } catch (Throwable t) {
        }
        CLIENT_CODE_STACK_INDEX = ++i;
    }


    public static void setEnable(boolean isPrintToLog) {
        TraceHelper.isPrintToLog = isPrintToLog;
    }

    public static void log(String message) {
        if (!isPrintToLog) return;
        Log.i(getFileName(1), message != null ? message : "null");
    }

    public static void log(String message, int depthOffset) {
        if (!isPrintToLog) return;
        Log.i(getFileName(depthOffset), message != null ? message : "null");
    }

    public static void printWithDepthOffset(int depthOffset, Object... msg) {
        printMain(1, ++depthOffset, null, false, Log.INFO, msg);
    }

    public static void printWithDepthWith(int depthWith, Object... msg) {
        printMain(depthWith, 0, null, false, Log.INFO, msg);
    }

    public static void printType(int type, Object... msg) {
        printMain(1, 0, null, false, type, msg);
    }

    public static void print(Object... msg) {
        printMain(1, 0, null, false, Log.INFO, msg);
    }

    public static void printClause(boolean assertion, Object... msg) {
        if(assertion)
            printMain(1, 0, null, false, Log.INFO, msg);
    }

    public static void error(Object... msg) {
        printMain(1, 0, null, false, Log.ERROR, msg);
    }

    public static void printInfo(Object... msg) {
        printMain(1, 0, null, true, Log.INFO, msg);
    }

    public static void printWithMark(String csn, Object... msg) {
        printMain(1, 0, csn, false, Log.INFO, msg);
    }

    /**
     * @param depthWith - глубина мониторинга стэка вызовов
     * @param depthOffset - для отбрасывания верхнего дерева стэка
     */
    private static void printMain(int depthWith, int depthOffset, String csn, boolean printThreadInfo, int type, Object... msg) {
        if(!isPrintToLog) return;

        if(TextUtil.isEmpty(csn))
            csn = getFileName(depthOffset - 1);
        String steInfo = getSteInfo(depthWith, depthOffset);

        String additionalMsg = DumpHelper.join(", ", msg);

//        String additionalMsg = msg == null ? null : msg.toString();
        StringBuilder sb = new StringBuilder();
        additionalMsg = TextUtil.substring(additionalMsg, 0); // C.LOG_MAXLENGTH
        additionalMsg = additionalMsg.replace(C.NEXT_LINE, ' ');
        int additionalMsgLines = additionalMsg != null ? additionalMsg.length() / 1000 + 1 : 0;

        if (depthWith > 1 || additionalMsgLines > 1) {
            sb.append("=====start=====  ");
        }

        sb.append(steInfo);

        if (printThreadInfo) {
            final Thread currentThread = Thread.currentThread();
            sb.append(" tid = ").append(currentThread.getId()).append(" tname = ");
            sb.append(currentThread.getName());
        }

        sb.append(" ");
        if (additionalMsg.length() < 1000) {
            sb.append(additionalMsg);

            if (depthWith > 1 || additionalMsgLines > 1) {
                sb.append(C.NEXT_LINE).append("=====end=====");
            }

            U.log(csn, sb.toString(), type);
        } else {
            U.log(csn, sb.toString(), type);
            int i = 0;
            while (i < additionalMsg.length()) {
                int substringEnd = i + 1000;
                if (substringEnd > additionalMsg.length()) {
                    substringEnd = additionalMsg.length();
                }
                U.log(csn, additionalMsg.substring(i, substringEnd), type);
                sb.append(C.NEXT_LINE).append(additionalMsg.substring(i, substringEnd));
                i = substringEnd;
            }
            U.log(csn, "=====end=====", type);
        }
    }

    private static String getSteInfo(int depthWith, int depthOffset) {
        String result = null;
        if (depthWith == 1) {
            result = getMethodAndFileName(depthOffset, true);
        } else if (depthWith > 1) {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final int lastNeedfulIndex = CLIENT_CODE_STACK_INDEX + depthOffset + depthWith;
            final StringBuilder sb = new StringBuilder();
            for (int i = CLIENT_CODE_STACK_INDEX + depthOffset + 1; i < stackTrace.length && i < lastNeedfulIndex; i++) {
                sb.append(stackTrace[i]).append(C.NEXT_LINE);
            }
            result = sb.toString();
        }
        return result;
    }

    private static String getFileName(int depthOffset) {
        String result = getElement(depthOffset).getFileName();
        return TextUtil.substring(result, ".java");
    }

    private static String getMethodName(int depthOffset) {
        return getElement(depthOffset).getMethodName();
    }

    private static String getMethodAndFileName(int depthOffset, boolean onlyMethod) {
        final StackTraceElement element = getElement(depthOffset);
        StringBuilder buf = new StringBuilder(80);
        String className = element.getClassName();
        if(!onlyMethod) buf.append(className.substring(className.lastIndexOf(".")+1));
        if(!onlyMethod) buf.append('.');
        buf.append(element.getMethodName());

        if (element.isNativeMethod()) {
            buf.append("(Native Method)");
        } else {
            String fName = element.getFileName();

            if (fName == null) {
                buf.append("(Unknown Source)");
            } else {
                int lineNum = element.getLineNumber();

                buf.append('(');
                buf.append(fName);
                if (lineNum >= 0) {
                    buf.append(':');
                    buf.append(lineNum);
                }
                buf.append(')');
            }
        }
        return buf.toString();
    }

    /**
     * @param depthOffset - без учета CLIENT_CODE_STACK_INDEX
     */
    private static StackTraceElement getElement(int depthOffset){
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[CLIENT_CODE_STACK_INDEX + depthOffset + 3]; //TODO коррекция на 3
    }


}