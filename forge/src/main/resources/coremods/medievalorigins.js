var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var InsnList = Java.type('org.objectweb.asm.tree.InsnList')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode')
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')

function initializeCoreMod() {
    return {
        'medievalorigins_edible_item_stack': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraftforge.common.extensions.IForgeItemStack',
                'methodName': 'getFoodProperties',
                'methodDesc': '(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;'
            },
            'transformer': function(node) {
                // if (EdibleItemUtil.doEdibleItemPowersApply(this.self(), entity)) { return EdibleItemUtil.getEdibleItemPowerFoodProperties(this.self(), entity) }
                var ls = new InsnList();
                // this.self()
                ls.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeItemStack", "self", "()Lnet/minecraft/world/item/ItemStack;", true));
                // entity
                ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
                ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dev/muon/medievalorigins/util/EdibleItemUtil", "doEdibleItemPowersApply", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z", false));
                var label = new LabelNode();
				ls.add(new JumpInsnNode(Opcodes.IFEQ, label));
                // this.self()
                ls.add(new VarInsnNode(Opcodes.ALOAD, 0));
                ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeItemStack", "self", "()Lnet/minecraft/world/item/ItemStack;", true));
                // entity
                ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
                ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dev/muon/medievalorigins/util/EdibleItemUtil", "getEdibleItemPowerFoodProperties", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;", false));
				ls.add(new InsnNode(Opcodes.ARETURN));
				ls.add(label);
				node.instructions.insert(ls);
                return node;
            }
        },
        'medievalorigins_edible_item': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraftforge.common.extensions.IForgeItem',
                'methodName': 'getFoodProperties',
                'methodDesc': '(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;'
            },
            'transformer': function(node) {
                // if (EdibleItemUtil.doEdibleItemPowersApply(stack, entity)) { return EdibleItemUtil.getEdibleItemPowerFoodProperties(stack, entity) }
                var ls = new InsnList();
                // stack (first parameter)
                ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
                // entity (second parameter)
                ls.add(new VarInsnNode(Opcodes.ALOAD, 2));
                ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dev/muon/medievalorigins/util/EdibleItemUtil", "doEdibleItemPowersApply", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z", false));
                var label = new LabelNode();
				ls.add(new JumpInsnNode(Opcodes.IFEQ, label));
                // stack (first parameter)
                ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
                // entity (second parameter)
                ls.add(new VarInsnNode(Opcodes.ALOAD, 2));
                ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dev/muon/medievalorigins/util/EdibleItemUtil", "getEdibleItemPowerFoodProperties", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;", false));
				ls.add(new InsnNode(Opcodes.ARETURN));
				ls.add(label);
				node.instructions.insert(ls);
                return node;
            }
        }
    }
}