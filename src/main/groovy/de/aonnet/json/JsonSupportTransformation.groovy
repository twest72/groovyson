package de.aonnet.json

import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit

import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.ast.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.transform.ASTTransformation
import org.objectweb.asm.Opcodes

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class JsonSupportTransformation implements ASTTransformation {

    def annotationType = JsonSupport.class.name

    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {

        if (!checkClassNode(astNodes, annotationType)) {
            // add an error message or a warning
            return
        }

        addMethods((ClassNode) astNodes[1]);
    }

    private boolean checkClassNode(astNodes, annotationType) {

        if (! astNodes)    return false
        if (! astNodes[0]) return false
        if (! astNodes[1]) return false

        if (!(astNodes[0] instanceof AnnotationNode))        return false
        if (! astNodes[0].classNode?.name == annotationType) return false
        if (!(astNodes[1] instanceof ClassNode))             return false

        true
    }

    private void addMethods(ClassNode classNode) {

        def phase = CompilePhase.SEMANTIC_ANALYSIS
        List<ASTNode> ast = new AstBuilder().buildFromString(phase, false, """

         package ${classNode.packageName}

         class ${classNode.nameWithoutPackage} {

             String toJsonString() {
                return JsonConverter.toJsonString(this)
             }

             Map toJsonMap() {
                return JsonConverter.toJsonMap(this)
             }

             static def newInstanceFromJsonString(String json) {
                 return JsonConverter.newInstanceFromJsonString(json)
             }

             static def newInstanceFromJsonMap(Map map) {
                 return JsonConverter.newInstanceFromJsonMap(map)
             }
         }
         """)

        classNode.addMethod(ast[1].methods[0])
        classNode.addMethod(ast[1].methods[1])
        classNode.addMethod(ast[1].methods[2])
        classNode.addMethod(ast[1].methods[3])
    }
}
