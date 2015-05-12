package org.axway.sonar;

import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;

import java.util.List;

/**
 * Axway SonarQube Plugin.
 */
public class AxwaySonarRulesPlugin extends SonarPlugin {



    @Override
    public List getExtensions() {
        return ImmutableList.of(
                AxwayRules.class,
                AxwayJavaFileScannersFactory.class);
    }
}
