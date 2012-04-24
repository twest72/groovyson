package de.aonnet.json;
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


import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.util.List;

import static org.codehaus.groovy.control.CompilePhase.SEMANTIC_ANALYSIS;

@GroovyASTTransformation(phase = SEMANTIC_ANALYSIS)
public class JsonSupportTransformation implements ASTTransformation {

    private String annotationType = JsonSupport.class.getName();

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {

        if (!checkClassNode(astNodes, sourceUnit)) {

            // TODO better error message or a warning
            sourceUnit.getErrorCollector().addErrorAndContinue(new SimpleMessage("wrong class", sourceUnit));
            return;
        }

        addMethods((ClassNode) astNodes[1], sourceUnit);
    }

    private boolean checkClassNode(ASTNode[] astNodes, SourceUnit sourceUnit) {

        if (astNodes == null) return false;
        if (astNodes[0] == null) return false;
        if (astNodes[1] == null) return false;

        if (!(astNodes[0] instanceof AnnotationNode)) return false;

        AnnotationNode annotationNode = (AnnotationNode) astNodes[0];
        if (!annotationNode.getClassNode().getName().equals(annotationType)) return false;
        if (!(astNodes[1] instanceof ClassNode)) return false;

        return true;
    }

    private void addMethods(ClassNode classNode, SourceUnit sourceUnit) {

        CompilePhase phase = SEMANTIC_ANALYSIS;

        List<ASTNode> ast = new AstBuilder().buildFromString(phase, false,
                "        package " + classNode.getPackageName() + "\n" +
                        "\n" +
                        "        class " + classNode.getNameWithoutPackage() + " {\n" +
                        "\n" +
                        "            String toJsonString() {\n" +
                        "                return  de.aonnet.json.JsonConverter.toJsonString(this)\n" +
                        "            }\n" +
                        "\n" +
                        "            Map toJsonMap() {\n" +
                        "                return  de.aonnet.json.JsonConverter.toJsonMap(this)\n" +
                        "            }\n" +
                        "\n" +
                        "            static def newInstanceFromJsonString(String json) {\n" +
                        "                return  de.aonnet.json.JsonConverter.newInstanceFromJsonString(json)\n" +
                        "            }\n" +
                        "\n" +
                        "            static def newInstanceFromJsonMap(Map map) {\n" +
                        "                return  de.aonnet.json.JsonConverter.newInstanceFromJsonMap(map)\n" +
                        "            }\n" +
                        "        }\n");

        for (MethodNode methodNode : ((ClassNode) ast.get(1)).getMethods()) {
            classNode.addMethod(methodNode);
        }
    }
}
