package com.totallytot.services

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
import com.totallytot.ao.AuditReport
import com.totallytot.ao.UserName
import com.totallytot.ao.IgnoredSpace
import com.totallytot.ao.MonitoredGroup

import javax.inject.Inject
import javax.inject.Named
import java.sql.SQLException

import org.apache.log4j.Logger

import static com.google.common.base.Preconditions.checkNotNull

@ExportAsService([PluginDataService])
@Named("pluginDataService")
class PluginDataServiceImpl implements PluginDataService {

    private static final Logger log = Logger.getLogger(PluginDataServiceImpl.class)
    private static final String PLUGIN_STORAGE_KEY = "com.totallytot"

    @ComponentImport
    private final ActiveObjects ao
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory

    @Inject
    PluginDataServiceImpl(ActiveObjects ao, PluginSettingsFactory pluginSettingsFactory) {
        this.ao = checkNotNull(ao)
        this.pluginSettingsFactory = pluginSettingsFactory
    }

    @Override
    void setIgnoredSpace(String spaceKey) {
        final IgnoredSpace ignoredSpace = ao.create(IgnoredSpace.class)
        ignoredSpace.setIgnoredSpaceKey(spaceKey)
        ignoredSpace.save()
    }

    @Override
    Set<String> getIgnoredSpaces() { ao.find(IgnoredSpace.class).collect { it.ignoredSpaceKey } }

    @Override
    void removeIgnoredSpace(String spaceKey) {
        ao.find(IgnoredSpace.class, "IGNORED_SPACE_KEY = ?", spaceKey).each {
            try {
                it.entityManager.delete(it)
            } catch (SQLException e) {
                log.error(e.message, e)
            }
        }
    }

    @Override
    void setMonitoredGroup(String group) {
        final MonitoredGroup monitoredGroup = ao.create(MonitoredGroup.class)
        monitoredGroup.setMonitoredGroup(group)
        monitoredGroup.save()
    }

    @Override
    Set<String> getMonitoredGroups() { ao.find(MonitoredGroup.class).collect { it.monitoredGroup } }

    @Override
    void removeMonitoredGroup(String group) {
        ao.find(MonitoredGroup.class, "MONITORED_GROUP = ?", group).each {
            try {
                it.entityManager.delete(it)
            } catch (SQLException e) {
                log.error(e.message, e)
            }
        }
    }

    @Override
    void setNotificationReceiver(String userName) {
        final UserName mail = ao.create(UserName.class)
        mail.setUserName(userName)
        mail.save()
    }

    @Override
    Set<String> getNotificationReceivers() { ao.find(UserName.class).collect { it.userName } }

    @Override
    void removeNotificationReceiver(String userName) {
        ao.find(UserName.class, "USER_NAME = ?", userName).each {
            try {
                it.entityManager.delete(it)
            } catch (SQLException e) {
                log.error(e.message, e)
            }
        }
    }

    @Override
    Boolean isEmailActive() {
        def pluginSettings = pluginSettingsFactory.createGlobalSettings()
        if (!pluginSettings.get(PLUGIN_STORAGE_KEY + ".email")) pluginSettings.put(PLUGIN_STORAGE_KEY + ".email", "false")
        Boolean.parseBoolean(pluginSettings.get(PLUGIN_STORAGE_KEY + ".email").toString())
    }

    @Override
    void activateEmail(Boolean active) {
        def pluginSettings = pluginSettingsFactory.createGlobalSettings()
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".email", active.toString())
    }

    @Override
    Boolean isPermissionRemovalActive() {
        def pluginSettings = pluginSettingsFactory.createGlobalSettings()
        if (!pluginSettings.get(PLUGIN_STORAGE_KEY + ".permission")) pluginSettings.put(PLUGIN_STORAGE_KEY + ".permission", "false")
        Boolean.parseBoolean(pluginSettings.get(PLUGIN_STORAGE_KEY + ".permission").toString())
    }

    @Override
    void activatePermissionRemoval(Boolean active) {
        def pluginSettings = pluginSettingsFactory.createGlobalSettings()
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".permission", active.toString())
    }

    @Override
    void addRecordToAuditReport(String spaceKey, String group, String permission, String userName, String date) {
        final AuditReport row = ao.create(AuditReport.class)
        row.setSpaceKey(spaceKey)
        row.setGroup(group)
        row.setPermission(permission)
        row.setViolator(userName)
        row.setDate(date)
        row.save()
    }

    @Override
    List<AuditReport> getAuditReportEntities() { ao.find(AuditReport.class) }

    @Override
    void removeAllAuditReportEntities() {
        auditReportEntities.each {
            try {
                it.entityManager.delete(it)
            } catch (SQLException e) {
                log.error(e.message, e)
            }
        }
    }
}