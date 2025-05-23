/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.daacoo.file.manager.connector.handlers.command;

import com.daacoo.file.manager.connector.configuration.Constants;
import com.daacoo.file.manager.connector.configuration.IConfiguration;
import com.daacoo.file.manager.connector.errors.ConnectorException;
import com.daacoo.file.manager.connector.utils.AccessControlUtil;
import com.daacoo.file.manager.connector.utils.FileUtils;
import com.daacoo.file.manager.connector.utils.ImageUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Class to handle
 * <code>Thumbnail</code> command.
 */
public class ThumbnailCommand extends Command {

    /**
     * File name.
     */
    private String fileName;
    /**
     * Thumbnail file.
     */
    private File thumbFile;
    /**
     * Field holding If-None-Match header value.
     */
    private String ifNoneMatch;
    /**
     * Field holding If-Modified-Since header value.
     */
    private long ifModifiedSince;
    /**
     * current {code HttpServletResponse} object.
     */
    private HttpServletResponse response;
    /**
     * Full path to the thumbnail.
     */
    private String fullCurrentPath;
    /**
     * Backup map holding mime types for images just in case if they aren't set in a request
     */
    private static final HashMap<String, String> IMG_MIME_TYPE_MAP = new HashMap<String, String>(57);

    static {
        IMG_MIME_TYPE_MAP.put(".art", "image/x-jg");
        IMG_MIME_TYPE_MAP.put(".bm", "image/bmp");
        IMG_MIME_TYPE_MAP.put(".bmp", "image/bmp");
        IMG_MIME_TYPE_MAP.put(".dwg", "image/vnd.dwg");
        IMG_MIME_TYPE_MAP.put(".dxf", "image/vnd.dwg");
        IMG_MIME_TYPE_MAP.put(".fif", "image/fif");
        IMG_MIME_TYPE_MAP.put(".flo", "image/florian");
        IMG_MIME_TYPE_MAP.put(".fpx", "image/vnd.fpx");
        IMG_MIME_TYPE_MAP.put(".g3", "image/g3fax");
        IMG_MIME_TYPE_MAP.put(".gif", "image/gif");
        IMG_MIME_TYPE_MAP.put(".ico", "image/x-icon");
        IMG_MIME_TYPE_MAP.put(".ief", "image/ief");
        IMG_MIME_TYPE_MAP.put(".iefs", "image/ief");
        IMG_MIME_TYPE_MAP.put(".jut", "image/jutvision");
        IMG_MIME_TYPE_MAP.put(".mcf", "image/vasa");
        IMG_MIME_TYPE_MAP.put(".nap", "image/naplps");
        IMG_MIME_TYPE_MAP.put(".naplps", "image/naplps");
        IMG_MIME_TYPE_MAP.put(".nif", "image/x-niff");
        IMG_MIME_TYPE_MAP.put(".niff", "image/x-niff");
        IMG_MIME_TYPE_MAP.put(".pct", "image/x-pict");
        IMG_MIME_TYPE_MAP.put(".pcx", "image/x-pcx");
        IMG_MIME_TYPE_MAP.put(".pgm", "image/x-portable-graymap");
        IMG_MIME_TYPE_MAP.put(".pic", "image/pict");
        IMG_MIME_TYPE_MAP.put(".pict", "image/pict");
        IMG_MIME_TYPE_MAP.put(".pm", "image/x-xpixmap");
        IMG_MIME_TYPE_MAP.put(".png", "image/png");
        IMG_MIME_TYPE_MAP.put(".pnm", "image/x-portable-anymap");
        IMG_MIME_TYPE_MAP.put(".ppm", "image/x-portable-pixmap");
        IMG_MIME_TYPE_MAP.put(".ras", "image/x-cmu-raster");
        IMG_MIME_TYPE_MAP.put(".rast", "image/cmu-raster");
        IMG_MIME_TYPE_MAP.put(".rf", "image/vnd.rn-realflash");
        IMG_MIME_TYPE_MAP.put(".rgb", "image/x-rgb");
        IMG_MIME_TYPE_MAP.put(".rp", "  image/vnd.rn-realpix");
        IMG_MIME_TYPE_MAP.put(".svf", "image/vnd.dwg");
        IMG_MIME_TYPE_MAP.put(".svf", "image/x-dwg");
        IMG_MIME_TYPE_MAP.put(".tiff", "image/tiff");
        IMG_MIME_TYPE_MAP.put(".turbot", "image/florian");
        IMG_MIME_TYPE_MAP.put(".wbmp", "image/vnd.wap.wbmp");
        IMG_MIME_TYPE_MAP.put(".xif", "image/vnd.xiff");
        IMG_MIME_TYPE_MAP.put(".xpm", "image/x-xpixmap");
        IMG_MIME_TYPE_MAP.put(".x-png", "image/png");
        IMG_MIME_TYPE_MAP.put(".xwd", "image/x-xwindowdump");
    }

    @Override
    public void setResponseHeader(final HttpServletResponse response, final ServletContext sc) {
        response.setHeader("Cache-Control", "public");
        String mimetype = getMimeTypeOfImage(sc, response);
        if (mimetype != null) {
            response.setContentType(mimetype);
        }
        response.addHeader("Content-Disposition", "attachment; filename=\"" + this.fileName + "\"");
        // set to fill some params later.
        this.response = response;

    }

    /**
     * Gets mime type of image.
     *
     * @param sc       the {@code ServletContext} object.
     * @param response currect response object
     * @return mime type of the image.
     */
    private String getMimeTypeOfImage(final ServletContext sc, final HttpServletResponse response) {
        if (this.fileName == null || this.fileName.length() == 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        String tempFileName = this.fileName.substring(0,
                this.fileName.lastIndexOf('.') + 1).concat(
                FileUtils.getFileExtension(this.fileName).toLowerCase());
        String mimeType = sc.getMimeType(tempFileName);
        if (mimeType == null || mimeType.length() == 0) {
            mimeType = ThumbnailCommand.IMG_MIME_TYPE_MAP.get(fileName.toLowerCase().substring(fileName.lastIndexOf(".")));
        }

        if (mimeType == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        return mimeType;
    }

    @Override
    public void execute(final OutputStream out) throws ConnectorException {
        validate();
        createThumb();
        if (setResponseHeadersAfterCreatingFile()) {
            try {
                FileUtils.printFileContentToResponse(thumbFile, out);
            } catch (IOException e) {
                if (configuration.isDebugMode()) {
                    throw new ConnectorException(e);
                }
                try {
                    this.response.sendError(HttpServletResponse.SC_FORBIDDEN);
                } catch (IOException e1) {
                    throw new ConnectorException(e1);
                }
            }
        } else {
            try {
                this.response.reset();
                this.response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            } catch (IOException e1) {
                throw new ConnectorException(e1);
            }
        }
    }

    @Override
    public void initParams(final HttpServletRequest request,
                           final IConfiguration configuration, final Object... params)
            throws ConnectorException {
        super.initParams(request, configuration, params);
        this.fileName = getParameter(request, "FileName");
        try {
            this.ifModifiedSince = Long.valueOf(request.getDateHeader("If-Modified-Since"));
        } catch (IllegalArgumentException e) {
            this.ifModifiedSince = 0;
        }
        this.ifNoneMatch = request.getHeader("If-None-Match");
    }

    /**
     * Validates thumbnail file properties and current user access rights.
     *
     * @throws ConnectorException when validation fails.
     */
    private void validate() throws ConnectorException {
        if (!this.configuration.getThumbsEnabled()) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_THUMBNAILS_DISABLED);
        }
        if (!checkIfTypeExists(this.type)) {
            this.type = null;
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE, false);
        }

        if (!AccessControlUtil.getInstance().checkFolderACL(
                this.type, this.currentFolder, this.userRole,
                AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_VIEW)) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED);
        }

        if (!FileUtils.checkFileName(this.fileName)) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST);
        }

        if (FileUtils.checkIfFileIsHidden(this.fileName, this.configuration)) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND);
        }

        File typeThumbDir = new File(configuration.getThumbsPath()
                + File.separator + this.type);

        try {
            this.fullCurrentPath = typeThumbDir.getAbsolutePath()
                    + currentFolder;
            if (!typeThumbDir.exists()) {
                typeThumbDir.mkdir();
            }
        } catch (SecurityException e) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
        }

    }

    /**
     * Creates thumbnail file if thumbnails are enabled
     * and thumb file doesn't exists.
     *
     * @throws ConnectorException when thumbnail creation fails.
     */
    private void createThumb() throws ConnectorException {
        this.thumbFile = new File(fullCurrentPath, this.fileName);
        try {
            if (!thumbFile.exists()) {
                File orginFile = new File(configuration.getTypes().get(this.type).getPath()
                        + this.currentFolder, this.fileName);
                if (!orginFile.exists()) {
                    throw new ConnectorException(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND);
                }
                try {
                    ImageUtils.createThumb(orginFile, thumbFile, configuration);
                } catch (Exception e) {
                    thumbFile.delete();
                    throw new ConnectorException(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
                            e);
                }
            }
        } catch (SecurityException e) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
        }

    }

    /**
     * Fills in response headers after creating file.
     *
     * @return true if continue returning thumb or false if break and send
     * response code.
     * @throws ConnectorException when access is denied.
     */
    private boolean setResponseHeadersAfterCreatingFile() throws ConnectorException {
        // Set content size
        File file = new File(this.fullCurrentPath, this.fileName);
        try {
            String etag = Long.toHexString(file.lastModified()).concat("-").concat(Long.toHexString(file.length()));
            if (etag.equals(this.ifNoneMatch)) {
                return false;
            } else {
                response.setHeader("Etag", etag);
            }

            if (file.lastModified() <= this.ifModifiedSince) {
                return false;
            } else {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat df = new SimpleDateFormat(
                        "EEE, dd MMMM yyyy HH:mm:ss z");
                response.setHeader("Last-Modified", df.format(date));
            }
            response.setContentLength((int) file.length());
        } catch (SecurityException e) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
        }
        return true;
    }
}
