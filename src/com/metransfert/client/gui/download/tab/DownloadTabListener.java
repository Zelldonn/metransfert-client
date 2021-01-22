package com.metransfert.client.gui.download.tab;

import java.nio.file.Path;

public interface DownloadTabListener {
    void onDownloadButtonClicked();
    void onPathTextFieldChanged(Path p);
    void onPathChooserClicked();
    void onIDTextFieldChanged();
}
