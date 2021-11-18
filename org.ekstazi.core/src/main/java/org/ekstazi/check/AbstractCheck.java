/*
 * Copyright 2014-present Milos Gligoric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ekstazi.check;

import java.util.Map;
import java.util.Set;

import org.ekstazi.configAware.ConfigLoader;
import org.ekstazi.data.RegData;
import org.ekstazi.data.Storer;
import org.ekstazi.hash.Hasher;
import org.ekstazi.log.Log;

abstract class AbstractCheck {

    /** Storer */
    protected final Storer mStorer;

    /** Hasher */
    protected final Hasher mHasher;

    /**
     * Constructor.
     */
    public AbstractCheck(Storer storer, Hasher hasher) {
        this.mStorer = storer;
        this.mHasher = hasher;
    }

    public abstract String includeAll(String fileName, String fileDir);

    public abstract void includeAffected(Set<String> affectedClasses);

    public abstract void includeAffectedFromCurRound(Set<String> affectedClasses, String curRoundDirName);

    protected boolean isAffected(String dirName, String className, String methodName) {
        //Log.d2f("line49: Compare diff!");
        return isAffectedByReg(mStorer.loadRegData(dirName, className, methodName))
                || isAffectedByConfig(mStorer.loadConfigData(dirName, className, methodName), className);
    }

    protected boolean isAffectedByReg(Set<RegData> regData) {
        return regData == null || regData.size() == 0 || hasHashChanged(regData);
    }

    protected boolean isAffectedByConfig(Map<String, String> configMap, String className) {
        return configMap != null && !configMap.isEmpty() && hasConfigChanged(configMap, className);
    }

    /**
     * Check if any element of the given set has changed.
     */
    private boolean hasHashChanged(Set<RegData> regData) {
        for (RegData el : regData) {
            if (hasHashChanged(mHasher, el)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the given datum has changed using the given hasher.
     */
    protected final boolean hasHashChanged(Hasher hasher, RegData regDatum) {
        String urlExternalForm = regDatum.getURLExternalForm();
        // Check hash.
        String newHash = hasher.hashURL(urlExternalForm);
        boolean anyDiff = !newHash.equals(regDatum.getHash());
        return anyDiff;
    }

    /**
     * Check if the configuration has changed.
     *
     */
    private boolean hasConfigChanged(Map<String, String> configMap, String className) {
        Log.d2f("Compare configuration diff!");
        if (!ConfigLoader.hasConfigFile()) {
            return false;
        }
        Map<String, String> userConfig = ConfigLoader.getUserConfigMap();
//        if (userConfig.isEmpty() || userConfig == null) {
//            Log.d2f("Failed to get user configuration");
//        } else {
//            Log.printConfig(userConfig, className);
//            Log.printConfig(configMap, className);
//        }
        for(Map.Entry<String, String> entry : configMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
//            if ((userConfig.containsKey(key))){
//                Log.d2f("Config from last round :<" + key + " " + value + ">; From user: <" + key + " " + value + ">");
//            }
            if ((userConfig.containsKey(key) && !userConfig.get(key).equals(value))) {
                Log.d2f("Diff!! Key = " + key + " value = " + value + " / " + userConfig.get(key));
                return true;
            }
        }
        return false;
    }
}
