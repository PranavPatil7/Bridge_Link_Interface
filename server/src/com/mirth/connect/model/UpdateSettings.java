/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 *
 * http://www.mirthcorp.com
 *
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 *
 * Copyright (c) NextGen Healthcare. All rights reserved.
 * https://www.nextgen.com/products-and-services/integration-engine
 *
 * Copyright (c) 2025 Innovar Healthcare. All rights reserved
 * This project is a fork of Mirth Connect by Nextgen Healthcare.
 * It has been modified and maintained independently by Innovar Healthcare.
 */


package com.mirth.connect.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mirth.connect.donkey.util.DonkeyElement;
import com.mirth.connect.donkey.util.migration.Migratable;
import com.mirth.connect.donkey.util.purge.Purgable;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("updateSettings")
public class UpdateSettings extends com.mirth.connect.model.AbstractSettings implements Serializable, com.mirth.connect.model.Auditable, Purgable, Migratable {

    private static final String STATS_ENABLED = "stats.enabled";
    private static final String LAST_STATS_TIME = "stats.time";

    private Boolean statsEnabled = false;
    private Long lastStatsTime;

    public UpdateSettings() {

    }

    public UpdateSettings(Properties properties) {
        setProperties(properties);
    }

    public Properties getProperties() {
        Properties properties = new Properties();

        if (getStatsEnabled() != null) {
            properties.put(STATS_ENABLED, BooleanUtils.toIntegerObject(getStatsEnabled()).toString());
        }
        if (getLastStatsTime() != null) {
            properties.put(LAST_STATS_TIME, getLastStatsTime().toString());
        }

        return properties;
    }

    public void setProperties(Properties properties) {
        setStatsEnabled(intToBooleanObject(properties.getProperty(STATS_ENABLED)));
        setLastStatsTime(toLongObject(properties.getProperty(LAST_STATS_TIME)));
    }

    public Boolean getStatsEnabled() {
        return statsEnabled;
    }

    public void setStatsEnabled(Boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
    }

    public Long getLastStatsTime() {
        return lastStatsTime;
    }

    public void setLastStatsTime(Long lastStatsTime) {
        this.lastStatsTime = lastStatsTime;
    }

    @Override
    public String toAuditString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    @Override
    public Map<String, Object> getPurgedProperties() {
        Map<String, Object> purgedProperties = new HashMap<String, Object>();
        return purgedProperties;
    }

    // @formatter:off
    @Override public void migrate3_0_1(DonkeyElement element) {}
    @Override public void migrate3_0_2(DonkeyElement element) {} // @formatter:on

    @Override
    public void migrate3_1_0(DonkeyElement element) {
        element.removeChild("firstLogin");
        element.removeChild("updatesEnabled");
        element.removeChild("updateUrl");
    }

    // @formatter:off
    @Override public void migrate3_2_0(DonkeyElement element) {}
    @Override public void migrate3_3_0(DonkeyElement element) {}
    @Override public void migrate3_4_0(DonkeyElement element) {}
    @Override public void migrate3_5_0(DonkeyElement element) {}    
    @Override public void migrate3_6_0(DonkeyElement element) {}    
    @Override public void migrate3_7_0(DonkeyElement element) {}
    @Override public void migrate3_9_0(DonkeyElement element) {} 
    @Override public void migrate3_11_0(DonkeyElement element) {}
    @Override public void migrate3_11_1(DonkeyElement element) {}
    @Override public void migrate3_12_0(DonkeyElement element) {} // @formatter:on
}
