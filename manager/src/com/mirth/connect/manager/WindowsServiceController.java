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


package com.mirth.connect.manager;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindowsServiceController implements com.mirth.connect.manager.ServiceController {

    private final String WINDOWS_PATH_SERVER_MANAGER_EXE = "blmanager.exe";
    private final String WINDOWS_SERVICE_NAME = "BridgeLink Service";
    private final String WINDOWS_CMD_START = "net start \"";
    private final String WINDOWS_CMD_STOP = "net stop \"";
    private final String WINDOWS_CMD_STATUS = "net continue \"";
    private final int WINDOWS_STATUS_RUNNING = 2191;
    private final int WINDOWS_STATUS_STOPPED = 2184;
    private final String WINDOWS_STATUS_CHANGING = "2189";
    private final String WINDOWS_CMD_QUERY_REGEX = "NET HELPMSG ([0-9]{4})";
    private final String WINDOWS_CMD_REG_QUERY = "REG QUERY HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"BridgeLink Server Manager\"";
    private final String WINDOWS_CMD_REG_DELETE = "REG DELETE HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /f /v \"BridgeLink Server Manager\"";
    private final String WINDOWS_CMD_REG_ADD = "REG ADD HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /f /v \"BridgeLink Server Manager\" /d ";

    @Override
    public int checkService() {
        Pattern pattern = Pattern.compile(WINDOWS_CMD_QUERY_REGEX);
        Matcher matcher;
        String key = "-1";
        do {
            try {
                matcher = pattern.matcher(com.mirth.connect.manager.CmdUtil.execCmdWithErrorOutput(WINDOWS_CMD_STATUS + WINDOWS_SERVICE_NAME + "\"").replace('\n', ' ').replace('\r', ' '));
                while (matcher.find()) {
                    key = matcher.group(1);
                }

                if (key.equals(WINDOWS_STATUS_CHANGING)) {
                    Thread.sleep(100);
                } else {
                    if (Integer.parseInt(key) == WINDOWS_STATUS_STOPPED) {
                        return 0;
                    } else if (Integer.parseInt(key) == WINDOWS_STATUS_RUNNING) {
                        return 1;
                    }
                }
            } catch (Exception e) {
            }
        } while (key.equals(WINDOWS_STATUS_CHANGING));

        return -1;
    }

    @Override
    public boolean startService() {
        try {
            if (com.mirth.connect.manager.CmdUtil.execCmd(WINDOWS_CMD_START + WINDOWS_SERVICE_NAME + "\"", true) == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean stopService() {
        try {
            if (com.mirth.connect.manager.CmdUtil.execCmd(WINDOWS_CMD_STOP + WINDOWS_SERVICE_NAME + "\"", true) == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isStartupPossible() {
        return true;
    }

    @Override
    public void setStartup(boolean enabled) {
        if (enabled) {
            try {
                String absolutePath = new File(com.mirth.connect.manager.PlatformUI.MIRTH_PATH).getAbsolutePath();
                com.mirth.connect.manager.CmdUtil.execCmd(WINDOWS_CMD_REG_ADD + "\"\\\"" + absolutePath + System.getProperty("file.separator") + WINDOWS_PATH_SERVER_MANAGER_EXE + "\\\"\"", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                com.mirth.connect.manager.CmdUtil.execCmd(WINDOWS_CMD_REG_DELETE, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isStartup() {
        int keyQueryResult = 1;
        try {
            keyQueryResult = com.mirth.connect.manager.CmdUtil.execCmd(WINDOWS_CMD_REG_QUERY, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (keyQueryResult == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCommand() {
        return "cmd /c";
    }

    @Override
    public boolean isShowTrayIcon() {
        return true;
    }

    @Override
    public boolean isShowServiceTab() {
        return true;
    }

    @Override
    public void migrate() {
        // If the old value exists in the registry, then we should remove and re-add it
        try {
            String output = com.mirth.connect.manager.CmdUtil.execCmdWithOutput(WINDOWS_CMD_REG_QUERY);
            if (output.indexOf("BridgeLink Server Manager.exe") != -1) {
                setStartup(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
