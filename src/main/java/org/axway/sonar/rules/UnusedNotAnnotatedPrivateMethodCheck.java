package org.axway.sonar.rules;

import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MethodTree;

import java.util.List;

public class UnusedNotAnnotatedPrivateMethodCheck extends BaseTreeVisitor implements JavaFileScanner {

    public static final String RULE_KEY = "UnusedNotAnnotatedPrivateMethod";
    private JavaFileScannerContext context;

    /**
     * Initialization.
     *
     * @param repo the repository in which the rules is registered
     */
    public static void init(RulesDefinition.NewRepository repo) {
        RulesDefinition.NewRule rule = repo.createRule(UnusedNotAnnotatedPrivateMethodCheck.RULE_KEY)
                .setName("Unused private method should be removed")
                .setHtmlDescription("The method does not seem to be called, so you can remove it.")
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
    public void visitMethod(MethodTree tree) {
        if (isPrivateAndNotAnnotated(tree)) {
            if (!tree.symbol().usages().isEmpty()) {
                context.addIssue(tree, this, "The method is not used - it should be removed");
            }
        }
        super.visitMethod(tree);
    }

    private boolean isPrivateAndNotAnnotated(MethodTree tree) {
        final boolean isPrivate = tree.symbol().isPrivate();
        final List<SymbolMetadata.AnnotationInstance> annotations = tree.symbol().metadata().annotations();
        return isPrivate && annotations.isEmpty();
    }

}
