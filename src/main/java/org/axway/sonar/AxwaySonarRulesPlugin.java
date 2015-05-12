package org.axway.sonar;

import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;

import java.util.List;

/**
 * Created by clement on 17/03/15.
 */
public class AxwaySonarRulesPlugin extends SonarPlugin {



    @Override
    public List getExtensions() {
        return ImmutableList.of(
                AxwayRules.class,
                MyJavaFileScannersFactory.class);
    }
}
