package org.axway.sonar.rules;

import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.*;

/**
 * Check that non annotated private fields are used.
 */
public class UnusedNotAnnotatedPrivateFieldCheck extends BaseTreeVisitor implements JavaFileScanner {

    public static final String RULE_KEY = "UnusedNotAnnotatedPrivateField";
    private JavaFileScannerContext context;

    /**
     * Initialization.
     *
     * @param repo the repository in which the rules is registered
     */
    public static void init(RulesDefinition.NewRepository repo) {
        RulesDefinition.NewRule rule = repo.createRule(UnusedNotAnnotatedPrivateFieldCheck.RULE_KEY)
                .setName("Unused private field should be removed")
                .setHtmlDescription("The field does not seem to be used, so you can remove it.")
                .addTags("unused")
                .setSeverity(Severity.MAJOR)
                .setDebtSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY);

        rule.setDebtRemediationFunction(rule.debtRemediationFunctions().constantPerIssue("5min"));
    }

    @Override
    public String toString() {
        return RULE_KEY + " rule";
    }

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitClass(ClassTree tree) {
        super.visitClass(tree);
        for (Tree member : tree.members()) {
            if (member.is(Tree.Kind.VARIABLE)) {
                VariableTree var = ((VariableTree) member);
                checkIfUnused(var);
            }
        }
    }


    public void checkIfUnused(VariableTree tree) {
        if (tree.modifiers().modifiers().contains(Modifier.PRIVATE) && !"serialVersionUID".equals(tree.simpleName().name())) {
            Symbol symbol = tree.symbol();
            if (symbol.usages().isEmpty() && symbol.metadata().annotations().isEmpty()) {
                context.addIssue(tree, this, "Remove this unused \"" + tree.simpleName() + "\" private field.");
            }
        }
    }

}
