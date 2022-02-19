package top.canyie.pine.entry;

import android.os.Build;

import top.canyie.pine.Pine;
import top.canyie.pine.PineConfig;
import top.canyie.pine.utils.Primitives;
import top.canyie.pine.utils.Three;

/**
 * @author canyie
 */
public final class Arm32Entry {
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    private static final int CR_SIZE = 3; // r1~r3, r0 is used as callee
    private static final int FPR_SIZE = 16; // s0~s15

    // hardfp is enabled by default in Android 6.0+.
    // https://android-review.googlesource.com/c/platform/art/+/109033
    // TODO: Use different entries for hardfp and softfp
    private static final boolean USE_HARDFP = PineConfig.sdkLevel >= Build.VERSION_CODES.M;
    private Arm32Entry() {
    }

    private static void voidBridge(int artMethod, int extras, int sp) throws Throwable {
        handleBridge(artMethod, extras, sp);
    }

    private static int intBridge(int artMethod, int extras, int sp) throws Throwable {
        return (int) handleBridge(artMethod, extras, sp);
    }

    private static long longBridge(int artMethod,int extras, int sp) throws Throwable {
        return (long) handleBridge(artMethod, extras, sp);
    }

    private static double doubleBridge(int artMethod,int extras, int sp) throws Throwable {
        return (double) handleBridge(artMethod, extras, sp);
    }

    private static float floatBridge(int artMethod, int extras, int sp) throws Throwable {
        return (float) handleBridge(artMethod, extras, sp);
    }

    private static boolean booleanBridge(int artMethod, int extras, int sp) throws Throwable {
        return (boolean) handleBridge(artMethod, extras, sp);
    }

    private static byte byteBridge(int artMethod, int extras, int sp) throws Throwable {
        return (byte) handleBridge(artMethod, extras, sp);
    }

    private static char charBridge(int artMethod, int extras, int sp) throws Throwable {
        return (char) handleBridge(artMethod, extras, sp);
    }

    private static short shortBridge(int artMethod, int extras, int sp) throws Throwable {
        return (short) handleBridge(artMethod, extras, sp);
    }

    private static Object objectBridge(int artMethod, int extras, int sp) throws Throwable {
        return handleBridge(artMethod, extras, sp);
    }

    /**
     * Bridge handler for arm32.
     * Note: This method should never be inlined to
     * the direct bridge method (intBridge, objectBridge, etc.),
     * otherwise, it will crash when executing a hooked proxy method (it's an unknown bug).
     * More info about the bug:
     * App crash caused by SIGSEGV, fault addr 0x0, pc=lr=0,
     * but the lr register is not 0 at the entry/exit of the proxy method.
     * Is the lr register assigned to 0 after the proxy method returns?
     */
    private static Object handleBridge(int artMethod, int originExtras, int sp) throws Throwable {
        // Clone the extras and unlock to minimize the time we hold the lock
        int extras = (int) Pine.cloneExtras(originExtras);
        Pine.log("handleBridge: artMethod=%#x originExtras=%#x extras=%#x sp=%#x", artMethod, originExtras, extras, sp);
        Pine.HookRecord hookRecord = Pine.getHookRecord(artMethod);
        Three<int[], int[], float[]> three = getArgs(hookRecord, extras, sp);
        int[] coreRegisters = three.a;
        int[] stack = three.b;
        float[] fpRegisters = three.c;
        long thread = Pine.currentArtThread0();

        Object receiver;
        Object[] args;

        int crIndex = 0;
        int stackIndex = 0;
        int floatIndex = 0;
        int doubleIndex = 0;

        if (hookRecord.isStatic) {
            receiver = null;
        } else {
            receiver = Pine.getObject(thread, coreRegisters[0]);
            crIndex = 1;
            stackIndex = 1;
        }

        if (hookRecord.paramNumber > 0) {
            args = new Object[hookRecord.paramNumber];
            for (int i = 0;i < hookRecord.paramNumber;i++) {
                Class<?> paramType = hookRecord.paramTypes[i];
                Object value;
                if (paramType == double.class) {
                    // These "double registers" overlap with "single registers".
                    // Double should not overlap with float.
                    doubleIndex = Math.max(doubleIndex, Primitives.evenUp(floatIndex));
                    // If we don't use hardfp, the fpArgs.length is always 0.
                    if (doubleIndex < fpRegisters.length) {
                        float l = fpRegisters[doubleIndex++];
                        float h = fpRegisters[doubleIndex++];
                        value = Primitives.floats2Double(l, h);
                        stackIndex++;
                    } else {
                        int l, h;
                        if (crIndex < coreRegisters.length && !USE_HARDFP) {
                            l = coreRegisters[crIndex++];
                        } else {
                            l = stack[stackIndex];
                        }
                        stackIndex++;
                        if (crIndex < coreRegisters.length && !USE_HARDFP) {
                            h = coreRegisters[crIndex++];
                        } else {
                            h = stack[stackIndex];
                        }
                        value = Primitives.ints2Double(l, h);
                    }
                } else if (paramType == float.class) {
                    // These "single registers" overlap with "double registers".
                    // If we use an odd number of single registers, then we can continue to use the next
                    // but if we don’t, the next single register may be occupied by a double
                    if (floatIndex % 2 == 0) {
                        floatIndex = Math.max(doubleIndex, floatIndex);
                    }

                    // If we don't use hardfp, the fpArgs.length is always 0.
                    if (floatIndex < fpRegisters.length) {
                        value = fpRegisters[floatIndex++];
                    } else {
                        int asInt;
                        if (crIndex < coreRegisters.length && !USE_HARDFP) {
                            asInt = coreRegisters[crIndex++];
                        } else {
                            asInt = stack[stackIndex];
                        }
                        value = Float.intBitsToFloat(asInt);
                    }
                } else if (paramType == long.class) {
                    int l, h;
                    // For hardfp, if first argument is long, then the r1 register
                    // will be skipped, move to r2-r3 instead. Use r2, r3, sp + 12.
                    // See art::quick_invoke_reg_setup (in quick_entrypoints_cc_arm.cc)
                    if (crIndex == 0 && hookRecord.isStatic && USE_HARDFP) {
                        // We can know this argument is saved in r2-r3, just use them directly.
                        l = coreRegisters[1];
                        h = coreRegisters[2];
                        args[i] = Primitives.ints2Long(l, h);
                        crIndex = CR_SIZE;
                        stackIndex += 2;
                        continue;
                    }
                    if (crIndex < coreRegisters.length) {
                        l = coreRegisters[crIndex++];
                    } else {
                        l = stack[stackIndex];
                    }
                    stackIndex++;
                    if (crIndex < coreRegisters.length) {
                        h = coreRegisters[crIndex++];
                    } else {
                        h = stack[stackIndex];
                    }
                    value = Primitives.ints2Long(l, h);
                } else {
                    int asInt = crIndex < coreRegisters.length ? coreRegisters[crIndex++] : stack[stackIndex];
                    if (paramType.isPrimitive()) {
                        if (paramType == int.class) {
                            value = asInt;
                        } else if (paramType == boolean.class) {
                            value = asInt != 0;
                        } else if (paramType == short.class) {
                            value = (short) asInt;
                        } else if (paramType == char.class) {
                            value = (char) asInt;
                        } else if (paramType == byte.class) {
                            value = (byte) asInt;
                        } else {
                            throw new AssertionError("Unknown primitive type: " + paramType);
                        }
                    } else {
                        value = Pine.getObject(thread, asInt);
                    }
                }
                args[i] = value;
                stackIndex++;

            }
        } else {
            args = Pine.EMPTY_OBJECT_ARRAY;
        }

        return Pine.handleCall(hookRecord, receiver, args);
    }

    private static Three<int[], int[], float[]> getArgs(Pine.HookRecord hookRecord, int extras, int sp) {
        // TODO: Cache these values
        int crLength = hookRecord.isStatic ? 0 : 1/*this*/;
        int stackLength = crLength;
        int floatLength = 0, doubleLength = 0;
        Class<?>[] paramTypes = hookRecord.paramTypes;
        for (Class<?> paramType : paramTypes) {
            if (paramType == double.class) {
                doubleLength++;
                stackLength++;
            } else if (paramType == float.class) {
                floatLength++;
            } else {
                if (paramType == long.class) {
                    if (crLength == 0) crLength++; // first non-fp arg, r1 will be skipped
                    if (crLength < CR_SIZE) crLength++;
                    stackLength++;
                    // Fall-through to take of the high part.
                }
                if (crLength < CR_SIZE) crLength++;
            }
            stackLength++;
        }
        int fpLength = (doubleLength * 2) + floatLength;
        float[] fpRegisters = EMPTY_FLOAT_ARRAY;
        if (USE_HARDFP) {
            if (fpLength != 0) {
                // Floating point arguments are stored in floating point registers.
                fpRegisters = new float[Math.min(Primitives.evenUp(fpLength), FPR_SIZE)];
            }
        } else {
            crLength = Math.min(crLength + fpLength, CR_SIZE);
        }
        int[] coreRegisters = crLength != 0 ? new int[crLength] : EMPTY_INT_ARRAY;
        int[] stack = stackLength != 0 ? new int[stackLength] : EMPTY_INT_ARRAY;
        Pine.getArgsArm32(extras, sp, coreRegisters, stack, fpRegisters);
        return new Three<>(coreRegisters, stack, fpRegisters);
    }
}
