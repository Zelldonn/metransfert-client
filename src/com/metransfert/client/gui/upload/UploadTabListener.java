package com.metransfert.client.gui.upload;

import java.io.File;
import java.util.List;

public interface UploadTabListener {
    void onUploadButtonClicked();
    void openPathChooserButtonClicked();
    void onFilesDragged(File[] fileDraggedList);
}
