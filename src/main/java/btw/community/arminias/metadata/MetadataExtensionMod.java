package btw.community.arminias.metadata;

import net.devtech.grossfabrichacks.entrypoints.PrePreLaunch;
import net.devtech.grossfabrichacks.instrumentation.InstrumentationApi;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import net.devtech.grossfabrichacks.transformer.asm.AsmClassTransformer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.src.Packet52MultiBlockChange;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.MixinEnvironment;

import static org.objectweb.asm.Opcodes.*;

public class MetadataExtensionMod implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Hello Metadata world!");
        InstrumentationApi.retransform(Packet52MultiBlockChange.class, (name, node) -> {
            int count = 0;
            boolean done = false;
            for (MethodNode method : node.methods) {
                if ("<init>".equals(method.name) && method.desc.startsWith("(II[SI")) {
                    for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                        if (!done && insn.getOpcode() == ICONST_4) {
                            AbstractInsnNode newInsn = new LdcInsnNode(8);
                            method.instructions.insertBefore(insn, newInsn);
                            method.instructions.remove(insn);
                            insn = newInsn;
                            done = true;
                        }
                        if (insn.getOpcode() == INVOKEVIRTUAL) {
                            if (((MethodInsnNode) insn).owner.equals("java/io/DataOutputStream") && ((MethodInsnNode) insn).name.equals("writeShort") && count++ == 1) {
                                // Does this: var9.writeInt(((ChunkExtension) var7).getBlockExtraMetadata(var11, var13, var12));
                                InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 9));
                                list.add(new VarInsnNode(ALOAD, 7));
                                list.add(new TypeInsnNode(CHECKCAST, "btw/community/arminias/metadata/extension/ChunkExtension"));
                                list.add(new VarInsnNode(ILOAD, 11));
                                list.add(new VarInsnNode(ILOAD, 13));
                                list.add(new VarInsnNode(ILOAD, 12));
                                list.add(new MethodInsnNode(INVOKEINTERFACE, "btw/community/arminias/metadata/extension/ChunkExtension", "getBlockExtraMetadata", "(III)I", true));
                                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/DataOutputStream", "writeInt", "(I)V", false));

                                method.instructions.insert(insn, list);
                                break;
                            }
                        }
                    }
                }
            }
            if (!done) {
                throw new RuntimeException("Failed to find target method");
            }
            else {
                System.out.println("Successfully transformed Packet52MultiBlockChange");
            }
            if (count != 2) {
                throw new RuntimeException("Failed to find target instruction");
            }
            else {
                System.out.println("Successfully transformed Packet52MultiBlockChange");
            }
        });
    }
}
