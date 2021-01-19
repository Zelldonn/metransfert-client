package com.metransfert.client.gui.download;

import java.nio.file.Path;

public interface DownloadTabListener {
    void onDownloadButtonClicked();
    void onPathTextFieldChanged(Path p);
    void onPathChooserClicked();
    void onIDTextFieldChanged();
}
