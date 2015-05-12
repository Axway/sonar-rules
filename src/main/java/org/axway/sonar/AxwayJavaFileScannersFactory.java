package org.axway.sonar;

import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;

import java.util.Arrays;

/**
 * Instantiates the "checks" (implementations of rules) that are executed during
 * source code analysis.
 */
public class AxwayJavaFileScannersFactory implements CheckRegistrar {

    /**
     * Lists all the checks provided by the plugin
     */
    public static Class<? extends JavaCheck>[] checkClasses() {
        return AxwayRules.getRuleClasses();
    }

    @Override
    public void register(RegistrarContext registrarContext) {
        registrarContext.registerClassesForRepository(AxwayRules.REPOSITORY_KEY, Arrays.asList(checkClasses()));
    }
}