package com.sapienza.jimpossiblemission.model;

public class TerminalMenu {
    private int menuIndex = 2;
    private boolean active;
    private boolean showPasswordRequired;
    public TerminalMenu() {
        active = false;
        showPasswordRequired = false;
    }

    public void open() {
        menuIndex = 2;
        active = true;
    }

    public void logoff() {
        active = false;
    }

    public void up() {
        if (menuIndex > 0 && !showPasswordRequired) menuIndex--;
    }

    public void down() {
        if (menuIndex < 2 && !showPasswordRequired) menuIndex++;
    }

    public int select(Inventory inventory) {
        if (menuIndex == 2) {
            logoff();
        } else if (menuIndex == 1) {
            boolean hasCode = inventory.useCode(Code.CodeType.SNOOZE);
            if (!hasCode) {
                showPasswordRequired = true;
                return -1;
            } else {
                logoff();
            }
        } else if (menuIndex == 0) {
            boolean hasCode = inventory.useCode(Code.CodeType.PLATFORM_RESET);
            if (!hasCode) {
                showPasswordRequired = true;
                return -1;
            } else {
                logoff();
            }
        }
        return menuIndex;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isShowPasswordRequired() {
        return showPasswordRequired;
    }

    public void setShowPasswordRequired(boolean showPasswordRequired) {
        this.showPasswordRequired = showPasswordRequired;
    }

    public int getMenuIndex() {
        return menuIndex;
    }
}
