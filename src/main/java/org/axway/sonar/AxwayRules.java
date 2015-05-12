package org.axway.sonar;

import org.axway.sonar.rules.CaughtExceptionTouchedCheck;
import org.axway.sonar.rules.UnusedNotAnnotatedPrivateFieldCheck;
import org.axway.sonar.rules.UnusedNotAnnotatedPrivateMethodCheck;
import org.sonar.api.server.rule.RulesDefinition;

/**
 * The set of rules managed by the plugin.
 * <p/>
 * Adding a rules has to be done in two steps:
 * 1) add the files in the {@link #getRuleClasses()} array
 * 2) Call its init method to load the rules metadata
 */
public class AxwayRules implements RulesDefinition {

    public static final String REPOSITORY_KEY = "axway-rules";

    public static Class[] getRuleClasses() {
        return new Class[]{
                UnusedNotAnnotatedPrivateMethodCheck.class,
                UnusedNotAnnotatedPrivateFieldCheck.class,
                CaughtExceptionTouchedCheck.class
        };
    }

    /**
     * This method is executed when server is started.
     *
     * @param context
     */
    @Override
    public void define(Context context) {
        NewRepository repo = context.createRepository(REPOSITORY_KEY, "java");
        repo.setName("Axway Java Rules");

        UnusedNotAnnotatedPrivateMethodCheck.init(repo);
        UnusedNotAnnotatedPrivateFieldCheck.init(repo);
        CaughtExceptionTouchedCheck.init(repo);

        repo.done();
    }
}
