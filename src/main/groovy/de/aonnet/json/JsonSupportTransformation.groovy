/*
 * Copyright (c) 2012, Thomas Westphal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
