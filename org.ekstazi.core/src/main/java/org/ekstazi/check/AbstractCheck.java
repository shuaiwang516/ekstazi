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

    protected boolean isAffected(String dirName, String className, String methodName) {
        return isAffectedByReg(mStorer.loadRegData(dirName, className, methodName));
    }

    protected boolean isAffectedByReg(Set<RegData> regData) {
        return regData == null || regData.size() == 0 || hasHashChanged(regData);
    }

    protected boolean isAffectedByConfig(Map<String, String> configMap) {
        return configMap != null && !configMap.isEmpty() && hasConfigChanged(configMap);
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
     *
     */
    private boolean hasConfigChanged(Map<String, String> configMap) {
        Map<String, String> userConfig = ConfigLoader.getUserConfigMap();
        for(Map.Entry<String, String> entry : userConfig.entrySet()) {
            String uKey = entry.getKey();
            String uValue = entry.getValue();
            if (configMap.containsKey(uKey) && !configMap.get(uKey).equals(uValue)) {
                return true;
            }
        }
        return false;
    }
}
