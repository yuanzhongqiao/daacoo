package com.daacoo.web.core.ckfinder;


import com.daacoo.common.config.GlobalConfig;
import com.daacoo.common.core.entity.sys.SysUser;
import com.daacoo.common.utils.ShiroUtils;
import com.daacoo.common.utils.file.FileUtils;
import com.daacoo.file.manager.connector.configuration.Configuration;
import com.daacoo.file.manager.connector.configuration.Events;
import com.daacoo.file.manager.connector.errors.ConnectorException;
import com.daacoo.file.manager.connector.utils.PathUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * CKFinder配置
 * 注意：在生产服务器上，建议使用更细粒度的身份验证方法，checkAuthentication方法是从Configuration类中重写此方法，以确保请求来自经过身份验证的用户。
 *
 * 集成CKFinder插件，主要有以下几个注意点：
 * 1、resources下添加 ckfinder.xml
 * 2、ShiroConfig.java 添加过滤路径
 * 3、ResourcesConfig.java  addResourceHandlers添加路径转发
 * 4、OnlineSessionFilter.java 设置文件权限
 *
 * @author daacoo
 * @date 2019/11/29
 */
public class CKFinderConfig extends Configuration {

    public CKFinderConfig(ServletConfig servletConfig) {
        super(servletConfig);
    }

    @Override
    public void init() throws Exception {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource(this.xmlFilePath);
        Class<?> clazz = getClass().getSuperclass();
        Field field = clazz.getDeclaredField("lastCfgModificationDate");
        Method method = clazz.getDeclaredMethod("clearConfiguration");
        method.setAccessible(true);
        method.invoke(this);
        field.setAccessible(true);
        field.set(this, System.currentTimeMillis());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(resource.getInputStream());
        doc.normalize();
        Node node = doc.getFirstChild();
        if (node != null) {
            NodeList nodeList = node.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node childNode = nodeList.item(i);
                //是否启用CKFinder。修改为true，表示开启ckfinder功能。默认是false
                if (CKFinderConstant.NODE_ENABLED.equals(childNode.getNodeName())) {
                    if (servletConf.getInitParameter("enabled") == null) {
                        this.enabled = Boolean.valueOf(childNode.getTextContent().trim()).booleanValue();
                    } else {
                        this.enabled = Boolean.parseBoolean(servletConf.getInitParameter("enabled"));
                    }
                }
                /* baseDir和baseURL都应该指向服务器上相同的位置*/
                //提供服务器(物理机器)上目录的绝对路径
                if (CKFinderConstant.NODE_BASE_DIR.equals(childNode.getNodeName())) {
                    if (servletConf.getInitParameter("baseDir") == null) {
                        this.baseDir = childNode.getTextContent().trim();
                    } else {
                        this.baseDir = servletConf.getInitParameter("baseDir");
                    }
                    this.baseDir = PathUtils.escape(this.baseDir);
                    this.baseDir = PathUtils.addSlashToEnd(this.baseDir);
                }
                //提供用户文件目录的完整URL或相对于域的路径
                if (CKFinderConstant.NODE_BASE_URL.equals(childNode.getNodeName())) {
                    if (servletConf.getInitParameter("baseURL") == null) {
                        this.baseURL = childNode.getTextContent().trim();
                    } else {
                        this.baseURL = servletConf.getInitParameter("baseURL");
                    }
                    this.baseURL = PathUtils.escape(this.baseURL);
                    this.baseURL = PathUtils.addSlashToEnd(this.baseURL);
                }

                if (CKFinderConstant.NODE_LICENSE_NAME.equals(childNode.getNodeName())) {
                    this.licenseName = childNode.getTextContent().trim();
                }
                if (CKFinderConstant.NODE_LICENSE_KEY.equals(childNode.getNodeName())) {
                    this.licenseKey = childNode.getTextContent().trim();
                }

                String value;
                //设置上传图像的最大大小。如果上传的图像更大，它就会更大。按比例缩小。设置为0则禁用此功能
                if (CKFinderConstant.NODE_IMG_WIDTH.equals(childNode.getNodeName())) {
                    value = childNode.getTextContent().trim();
                    value = value.replaceAll("//D", "");

                    try {
                        this.imgWidth = Integer.valueOf(value);
                    } catch (NumberFormatException var13) {
                        this.imgWidth = null;
                    }
                }

                //设置上传图像的质量
                if (CKFinderConstant.NODE_IMG_QUALITY.equals(childNode.getNodeName())) {
                    value = childNode.getTextContent().trim();
                    value = value.replaceAll("//D", "");
                    method = clazz.getDeclaredMethod("adjustQuality", new Class[]{String.class});
                    method.setAccessible(true);
                    this.imgQuality = Float.parseFloat(method.invoke(this, value).toString());
                }

                //设置上传图像的高度
                if (CKFinderConstant.NODE_IMG_HEIGHT.equals(childNode.getNodeName())) {
                    value = childNode.getTextContent().trim();
                    value = value.replaceAll("//D", "");
                    try {
                        this.imgHeight = Integer.valueOf(value);
                    } catch (NumberFormatException var12) {
                        this.imgHeight = null;
                    }
                }

                //设置缓存目录
                if (CKFinderConstant.NODE_THUMBS.equals(childNode.getNodeName())) {
                    method = clazz.getDeclaredMethod("setThumbs", new Class[]{NodeList.class});
                    method.setAccessible(true);
                    method.invoke(this, childNode.getChildNodes());
                }

                //设置访问权限控制(已使用session进行文件控制权限控制：请查看OnlineSessionFilter.java类)
                if (CKFinderConstant.NODE_ACCESS_CONTROLS.equals(childNode.getNodeName())) {
                    method = clazz.getDeclaredMethod("setACLs", new Class[]{NodeList.class});
                    method.setAccessible(true);
                    method.invoke(this, childNode.getChildNodes());
                }

                //设置不允许创建以"xxx"字符开头的文件夹
                if (CKFinderConstant.NODE_HIDE_FOLDERS.equals(childNode.getNodeName())) {
                    method = clazz.getDeclaredMethod("setHiddenFolders", new Class[]{NodeList.class});
                    method.setAccessible(true);
                    method.invoke(this, childNode.getChildNodes());
                }

                //设置 文件 不显示/上传到 CKFinder
                if (CKFinderConstant.NODE_HIDE_FILES.equals(childNode.getNodeName())) {
                    method = clazz.getDeclaredMethod("setHiddenFiles", new Class[]{NodeList.class});
                    method.setAccessible(true);
                    method.invoke(this, childNode.getChildNodes());
                }

                //如果启用了CheckDoubleExtension，则.后文件名的每个部分都进行检查，不仅仅是最后一部分。
                // 比如，上传foo.php.rar就是拒绝的，因为“php”在拒绝的扩展列表中。
                if (CKFinderConstant.NODE_CHECK_DOUBLE_EXTENSION.equals(childNode.getNodeName())) {
                    this.doubleExtensions = Boolean.valueOf(childNode.getTextContent().trim()).booleanValue();
                }

                //增加IIS web服务器上的安全性。
                //如果启用，CKFinder将不允许创建文件夹和上传名称包含特殊字符的文件
                if (CKFinderConstant.NODE_DISALLOW_UNSAFE_CHARACTERS.equals(childNode.getNodeName())) {
                    this.disallowUnsafeCharacters = Boolean.valueOf(childNode.getTextContent().trim()).booleanValue();
                }

                //强制为文件和文件夹使用ASCII名称。如果启用，字符与diactric标记将自动转换为ASCII字母。
                if (CKFinderConstant.NODE_FORCE_ASCII.equals(childNode.getNodeName())) {
                    this.forceASCII = Boolean.valueOf(childNode.getTextContent().trim()).booleanValue();
                }

                //是否检查缩放后文件大小。否则，会在上传后立即检查。
                if (CKFinderConstant.NODE_CHECK_SIZE_AFTER_SCALING.equals(childNode.getNodeName())) {
                    this.checkSizeAfterScaling = Boolean.valueOf(childNode.getTextContent().trim()).booleanValue();
                }

                //为了安全起见,对于具有以下扩展名的文件，在第一个Kb的数据中允许使用HTML
                Scanner sc;
                if (CKFinderConstant.NODE_HTML_EXTENSIONS.equals(childNode.getNodeName())) {
                    value = childNode.getTextContent();
                    sc = (new Scanner(value)).useDelimiter(",");
                    while (sc.hasNext()) {
                        String val = sc.next();
                        if (val != null && !"".equals(val)) {
                            this.htmlExtensions.add(val.trim().toLowerCase());
                        }
                    }
                }

                //对图像文件执行其他检查.如果设置为true，则验证图像大小
                if (CKFinderConstant.NODE_SECURE_IMAGE_UPLOADS.equals(childNode.getNodeName())) {
                    this.secureImageUploads = Boolean.valueOf(childNode.getTextContent().trim()).booleanValue();
                }

                //设置编码
                if (CKFinderConstant.NODE_URI_ENCODING.equals(childNode.getNodeName())) {
                    this.uriEncoding = childNode.getTextContent().trim();
                }

                //设置CKFinder检索时必须使用的会话变量名. 当前用户的“role”,可以在daacoo-web/src/main/resources/ckfinder.xml的“accessControls”中设置
                if (CKFinderConstant.NODE_USER_ROLE_SESSION_VAR.equals(childNode.getNodeName())) {
                    this.userRoleSessionVar = childNode.getTextContent().trim();
                }

                //默认的资源类型
                if (CKFinderConstant.NODE_DEFAULT_RESOURCE_TYPES.equals(childNode.getNodeName())) {
                    value = childNode.getTextContent().trim();
                    sc = (new Scanner(value)).useDelimiter(",");
                    while (sc.hasNext()) {
                        this.defaultResourceTypes.add(sc.next());
                    }
                }

                //插件
                if (CKFinderConstant.NODE_PLUGINS.equals(childNode.getNodeName())) {
                    method = clazz.getDeclaredMethod("setPlugins", new Class[]{Node.class});
                    method.setAccessible(true);
                    method.invoke(this, childNode);

                }
                //基本路径构建器的实现
                if (CKFinderConstant.NODE_BASE_PATH_BUILDER_IMPL.equals(childNode.getNodeName())) {
                    method = clazz.getDeclaredMethod("setBasePathImpl", new Class[]{String.class});
                    method.setAccessible(true);
                    method.invoke(this, childNode.getTextContent().trim());
                }
            }
        }
        method = clazz.getDeclaredMethod("setTypes", new Class[]{Document.class});
        method.setAccessible(true);
        method.invoke(this, doc);
        field = clazz.getDeclaredField("events");
        field.setAccessible(true);
        field.set(this, new Events());
        this.registerEventHandlers();
    }

    /**
     * 创建当前配置类实例。在每个子类中，都应该重写这个方法并返回新的配置实例
     */
    @Override
    protected Configuration createConfigurationInstance() {//创建配置实例（重写）
        //获取当前登录的用户
        SysUser principal = ShiroUtils.getSysUser();
        if (principal == null) {
            return new CKFinderConfig(this.servletConf);
        }
        try {
            this.baseDir = FileUtils.path(servletConf.getInitParameter("baseDir") + GlobalConfig.USERFILES_BASE_URL + principal.getLoginName() + "/");
            this.baseURL = FileUtils.path(getRequest().getContextPath() + GlobalConfig.USERFILES_BASE_URL + principal.getLoginName() + "/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return super.createConfigurationInstance();
    }

    /**
     * @Description 检查用户是否经过身份验证
     * @Author daacoo
     * @Date 2020/12/14
     */
    @Override
    public boolean checkAuthentication(HttpServletRequest request) {

        return super.checkAuthentication(request);
    }

    /**
     * 获取当前请求对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取项目路径
     *
     * @return
     */
    private String getUserfilesBaseDir() {
        return getRequest().getSession().getServletContext().getRealPath("/");
    }

    @Override
    public boolean checkIfReloadConfig() throws ConnectorException {
        return false;
    }
}
