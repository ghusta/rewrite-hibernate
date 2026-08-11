package org.openrewrite.jpa;

import org.openrewrite.*;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.*;
import org.openrewrite.marker.Markers;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class AddAllocationSizeToSequenceGenerator extends Recipe {

    @Override
    public String getDisplayName() {
        return "Add allocationSize = 1 to `@SequenceGenerator`";
    }

    @Override
    public String getDescription() {
        return "Add allocationSize = 1 to `@SequenceGenerator`.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
                new UsesType<>("javax.persistence.SequenceGenerator", false),
                new JavaIsoVisitor<ExecutionContext>() {

                    @Override
                    public J.Annotation visitAnnotation(
                            J.Annotation annotation,
                            ExecutionContext ctx) {

                        J.Annotation a = super.visitAnnotation(annotation, ctx);

                        if (!"SequenceGenerator".equals(a.getSimpleName())) {
                            return a;
                        }

                        if (a.getArguments() == null) {
                            List<Expression> arguments = new ArrayList<>();
                            arguments.add(new J.Assignment(
                                    Tree.randomId(),
                                    Space.EMPTY,
                                    Markers.EMPTY,
                                    new J.Identifier(
                                            Tree.randomId(),
                                            Space.EMPTY,
                                            Markers.EMPTY,
                                            emptyList(),
                                            "allocationSize",
                                            null,
                                            null
                                    ),
                                    JLeftPadded.build(
                                            new J.Literal(
                                                    Tree.randomId(),
                                                    Space.EMPTY,
                                                    Markers.EMPTY,
                                                    1,
                                                    "1",
                                                    null,
                                                    JavaType.Primitive.Int
                                            )
                                    ),
                                    JavaType.Primitive.Int
                            ));
                            return a.withArguments(
                                    arguments
                            );
                        }

                        boolean allocationSizePresent = a.getArguments()
                                .stream()
                                .filter(J.Assignment.class::isInstance)
                                .map(J.Assignment.class::cast)
                                .anyMatch(assignment ->
                                        ((J.Identifier) assignment.getVariable())
                                                .getSimpleName()
                                                .equals("allocationSize")
                                );

                        if (allocationSizePresent) {
                            return a;
                        }

                        return a.withArguments(
                                ListUtils.concat(
                                        a.getArguments(),
                                        new J.Assignment(
                                                Tree.randomId(),
                                                Space.EMPTY,
                                                Markers.EMPTY,
                                                new J.Identifier(
                                                        Tree.randomId(),
                                                        Space.EMPTY,
                                                        Markers.EMPTY,
                                                        emptyList(),
                                                        "allocationSize",
                                                        null,
                                                        null
                                                ),
                                                JLeftPadded.build(
                                                        new J.Literal(
                                                                Tree.randomId(),
                                                                Space.EMPTY,
                                                                Markers.EMPTY,
                                                                1,
                                                                "1",
                                                                null,
                                                                JavaType.Primitive.Int
                                                        )
                                                ),
                                                JavaType.Primitive.Int
                                        ).withPrefix(Space.SINGLE_SPACE)
                                )
                        );
                    }
                }
        );
    }
}
