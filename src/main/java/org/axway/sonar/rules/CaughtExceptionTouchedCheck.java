package org.axway.sonar.rules;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.CatchTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.TypeTree;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A rule checking that exception are touched in catch block.
 */
public class CaughtExceptionTouchedCheck extends BaseTreeVisitor implements JavaFileScanner {

    public static final String RULE_KEY = "CaughtExceptionTouched";

    public static final String EXCLUDED_EXCEPTION_TYPE = "java.lang.InterruptedException, " +
            "java.lang.NumberFormatException, " +
            "java.text.ParseException, " +
            "java.net.MalformedURLException";

    public String exceptionsCommaSeparated = EXCLUDED_EXCEPTION_TYPE;

    private JavaFileScannerContext context;

    private Collection<String> exceptions;

    /**
     * Initialization.
     *
     * @param repo the repository in which the rules is registered
     */
    public static void init(RulesDefinition.NewRepository repo) {
        final RulesDefinition.NewRule rule = repo.createRule(RULE_KEY)
                .setName("The exception should be used in the catch block")
                .setHtmlDescription("The exception is not used, this is rather bad. You should use the exception " +
                        "object in your log, or rethrow it.")
                .addTags("error-handling")
                .setSeverity(Severity.MAJOR)
                .setDebtSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY);

        rule.createParam("exceptions").setType(RuleParamType.STRING)
                .setDescription("List of exceptions which should not be checked")
                .setDefaultValue("" + CaughtExceptionTouchedCheck.EXCLUDED_EXCEPTION_TYPE);

        rule.setDebtRemediationFunction(rule.debtRemediationFunctions().constantPerIssue("10min"));
    }

    @Override
    public String toString() {
        return RULE_KEY + " rule";
    }

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        exceptions = new ArrayList<>();
        Iterables.addAll(exceptions, Splitter.on(",").trimResults().split(exceptionsCommaSeparated));
        scan(context.getTree());
    }

    @Override
    public void visitCatch(CatchTree tree) {
        if (!isExcludedType(tree.parameter().type())) {
            Symbol exception = tree.parameter().symbol();
            super.visitCatch(tree);
            Collection<IdentifierTree> usages = exception.usages();
            if (usages.isEmpty()) {
                context.addIssue(tree, this, "You should use the exception somehow (in a log, rethrow it...)");
            }
        }
    }

    private boolean isExcludedType(TypeTree type) {
        return exceptions.contains(type.symbolType().fullyQualifiedName());
    }

}
