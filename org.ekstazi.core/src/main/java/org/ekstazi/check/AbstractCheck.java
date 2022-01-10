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
        return isAffectedByReg(mStorer.loadRegData(dirName, className, methodName), className)
                || isAffectedByConfig(mStorer.loadConfigData(dirName, className, methodName), className);
    }

    protected boolean isAffectedByReg(Set<RegData> regData, String className) {
        return regData == null || regData.size() == 0 || hasHashChanged(regData, className);
    }

    protected boolean isAffectedByConfig(Map<String, String> configMap, String className) {
        return configMap != null && !configMap.isEmpty() && hasConfigChanged(configMap, className);
    }

    /**
     * Check if any element of the given set has changed.
     */
    private boolean hasHashChanged(Set<RegData> regData, String className) {
        for (RegData el : regData) {
            if (hasHashChanged(mHasher, el)) {
                Log.codeDiffLog(el.getURLExternalForm(), className);
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
        Log.d2f("Compare configuration diff in AbstractCheck.java");
        Map<String, String> userConfig = ConfigLoader.getUserConfigMap();
        if (userConfig == null) {
            Log.configDiffLog("", "", "", "Failed to get user configuration", className);
            Log.d2f("Failed to get user configuration");
            return true;
        }

        // For evaluation
        Boolean diff = false;
        // For every configuration pairs, compare with user's setting.
//        for(Map.Entry<String, String> entry : configMap.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//
//            // (1) If user doesn't set, return true;
//            if (!userConfig.containsKey(key)) {
//                Log.configDiffLog(key, value, "", "User didn't set this config", className);
//                diff = true;
//            }
//
//            // (2) If user's setting is different, return true;
//            if (!userConfig.get(key).equals(value)) {
//                Log.configDiffLog(key, value, userConfig.get(key), "Value different!", className);
//                Log.d2f("Diff!! Key = " + key + " value = " + value + " / " + userConfig.get(key));
//                diff = true;
//            }
//        }

        for (Map.Entry<String, String> entry : userConfig.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // (1) If this config is not used by test, continue
            if (!configMap.containsKey(key)) {
                continue;
            }

            // (2) If user's setting is different, return true
            else if (!configMap.get(key).equals(value)) {
                Log.configDiffLog(key, configMap.get(key), value, "Value different!", className);
                diff = true;
            }
        }

        if (diff) {
            return true;
        }
        // (3) else return false;
        return false;
    }
}
