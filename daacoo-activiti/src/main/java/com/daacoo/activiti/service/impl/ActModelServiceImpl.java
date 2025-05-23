package com.daacoo.activiti.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.daacoo.activiti.entity.ModelEntityDto;
import com.daacoo.activiti.service.ActModelService;
import com.daacoo.common.core.entity.AjaxResult;
import com.daacoo.common.core.page.TableDataInfo;
import com.daacoo.common.core.text.Convert;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.rest.editor.model.ModelEditorJsonRestResource;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_DESCRIPTION;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_ID;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_NAME;
/**
 * 模型管理 服务层实现层
 * @author daacoo
 */
@Service
public class ActModelServiceImpl implements ActModelService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 查询模型列表
     *
     * @param modelEntityDto 模型信息
     * @return 模型集合
     */
    @Override
    public TableDataInfo selectModelList(ModelEntityDto modelEntityDto) {
        TableDataInfo data = new TableDataInfo();
        ModelQuery modelQuery = repositoryService.createModelQuery();
        data.setTotal(modelQuery.count());
        data.setRows(modelQuery.orderByModelId().desc().listPage(modelEntityDto.getPageNum(), modelEntityDto.getPageSize()));
        return data;

    }

    /**
     * 修改模型信息
     *
     * @param model    模型信息
     * @param json_xml json参数
     * @param svg_xml  xml参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Model model,String name,String description, String json_xml, String svg_xml) {
        try {

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put(MODEL_NAME, name);
            modelJson.put(MODEL_DESCRIPTION, description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);

            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));
            InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();
        } catch (Exception e) {
            LOGGER.error("保存模型失败");
            throw new ActivitiException("保存模型失败，模型ID=" + model.getId(), e);

        }
    }

    /**
     * 查询模型编辑器
     *
     * @param modelId 模型ID
     * @return json信息
     */
    @Override
    public ObjectNode selectWrapModelById(String modelId) {
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(MODEL_NAME, model.getName());
                }
                modelNode.put(MODEL_ID, model.getId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                modelNode.set("model", editorJsonNode);

            } catch (Exception e) {
                LOGGER.error("创建模型json失败", e);
                throw new ActivitiException("创建模型json失败，模型ID=" + modelId, e);
            }
        }
        return modelNode;
    }

    /**
     * 查询模型信息
     *
     * @param modelId 模型ID
     * @return 模型信息
     */
    @Override
    public Model selectModelById(String modelId) {
        return repositoryService.getModel(modelId);
    }

    /**
     * 根据模型ID批量删除
     *
     * @param ids 需要删除的数据ID
     * @return
     */
    @Override
    public boolean deleteModelIds(String ids) {
        boolean result = true;
        try {
            String[] modelIds = Convert.toStrArray(ids);
            for (String modelId : modelIds) {
                repositoryService.deleteModel(modelId);
            }
        } catch (Exception e) {
            result = false;
            throw new ActivitiException("删除模型失败，模型ID=" + ids, e);
        }
        return result;
    }

    /**
     * 获取资源文件信息
     *
     * @param modelId 模型ID
     * @return 资源文件信息
     */
    @Override
    public byte[] getModelEditorSource(String modelId) {
        return repositoryService.getModelEditorSource(modelId);
    }

    /**
     * 我的模型列表
     */
    @Override
    public List<Map<String, Object>> modelList(Map<String, Object> paramMap) {
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();
        if (StringUtils.isNotEmpty(paramMap.get("category").toString())) {
            modelQuery.modelCategory(paramMap.get("category").toString());
        }
        int firstResult = (Integer.parseInt(paramMap.get("pageNum").toString()) - 1) * Integer.parseInt(paramMap.get("pageSize").toString());
        if (firstResult >= modelQuery.count()) {
            firstResult = 0;
        }
        int maxResults = Integer.parseInt(paramMap.get("pageSize").toString());
        List<Model> list = modelQuery.listPage(firstResult, maxResults);

        List<Map<String, Object>> modelListMap = new ArrayList<>();
        for (Model model : list) {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("id", model.getId());
            modelMap.put("category", model.getCategory());
            modelMap.put("createTime", model.getCreateTime());
            modelMap.put("deploymentId", model.getDeploymentId());
            modelMap.put("key", model.getKey());
            modelMap.put("lastUpdateTime", model.getLastUpdateTime());
            modelMap.put("metaInfo", model.getMetaInfo());
            modelMap.put("name", model.getName());
            modelMap.put("tenantId", model.getTenantId());
            modelMap.put("version", model.getVersion());
            modelListMap.add(modelMap);
        }
        return modelListMap;
    }

    /**
     * 部署流程
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult deployModel(Map<String, Object> paramMap) {
        try {
            org.activiti.engine.repository.Model modelData = repositoryService.getModel(paramMap.get("id").toString());
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            String processName = modelData.getName();
            if (!StringUtils.endsWith(processName, ".bpmn20.xml")) {
                processName += ".bpmn20.xml";
            }
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addInputStream(processName, in).deploy();

            // 设置流程分类
            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
            for (ProcessDefinition processDefinition : list) {
                repositoryService.setProcessDefinitionCategory(processDefinition.getId(), modelData.getCategory());
            }
            if (CollectionUtils.isEmpty(list)) {
                return AjaxResult.error("部署失败，没有流程。");
            }
            return AjaxResult.success("部署成功");
        } catch (Exception e) {
            LOGGER.error("设计模型图不正确，请检查模型正确性，模型ID=" + paramMap.get("id").toString());
            throw new ActivitiException("设计模型图不正确，请检查模型正确性，模型ID=" + paramMap.get("id").toString(), e);
        }
    }

    /**
     * 创建模型
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createModle(Map<String, Object> paramMap){
        Model model = repositoryService.newModel();
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, paramMap.get("name").toString());
        String description = StringUtils.defaultString(paramMap.get("description").toString());
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        model.setName(paramMap.get("name").toString());
        model.setKey(StringUtils.defaultString(paramMap.get("key").toString()));
        model.setMetaInfo(modelNode.toString());
        model.setCategory(paramMap.get("category").toString());
        // 存入表act_re_model
        repositoryService.saveModel(model);

        // 创建模型时完善ModelEditorSource，这里是对画布的相关设置
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("process_author", "sys");
        editorNode.put("properties", properties);
        try {
            repositoryService.addModelEditorSource(model.getId(),editorNode.toString().getBytes("utf-8"));
        } catch (Exception e) {
            throw new ActivitiException("创建模型时完善ModelEditorSource服务异常：{}",e);
        }
    }
}
