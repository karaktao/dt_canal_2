<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="荷兰文: Gemeentecode; 中文: 市政代码" prop="municipalityCode">
        <el-input
          v-model="queryParams.municipalityCode"
          placeholder="请输入荷兰文: Gemeentecode; 中文: 市政代码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: GemeentecodeGM; 中文: 市政代码GM" prop="municipalityCodeGm">
        <el-input
          v-model="queryParams.municipalityCodeGm"
          placeholder="请输入荷兰文: GemeentecodeGM; 中文: 市政代码GM"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: Gemeentenaam; 中文: 市政名称" prop="municipalityName">
        <el-input
          v-model="queryParams.municipalityName"
          placeholder="请输入荷兰文: Gemeentenaam; 中文: 市政名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: Provinciecode; 中文: 省代码" prop="provinceCode">
        <el-input
          v-model="queryParams.provinceCode"
          placeholder="请输入荷兰文: Provinciecode; 中文: 省代码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: ProvinciecodePV; 中文: 省代码PV" prop="provinceCodePv">
        <el-input
          v-model="queryParams.provinceCodePv"
          placeholder="请输入荷兰文: ProvinciecodePV; 中文: 省代码PV"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: Provincienaam; 中文: 省名称" prop="provinceName">
        <el-input
          v-model="queryParams.provinceName"
          placeholder="请输入荷兰文: Provincienaam; 中文: 省名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['infrastructure:municipalityReference:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['infrastructure:municipalityReference:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['infrastructure:municipalityReference:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['infrastructure:municipalityReference:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="municipalityReferenceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="荷兰文: Gemeentecode; 中文: 市政代码" align="center" prop="municipalityCode" />
      <el-table-column label="荷兰文: GemeentecodeGM; 中文: 市政代码GM" align="center" prop="municipalityCodeGm" />
      <el-table-column label="荷兰文: Gemeentenaam; 中文: 市政名称" align="center" prop="municipalityName" />
      <el-table-column label="荷兰文: Provinciecode; 中文: 省代码" align="center" prop="provinceCode" />
      <el-table-column label="荷兰文: ProvinciecodePV; 中文: 省代码PV" align="center" prop="provinceCodePv" />
      <el-table-column label="荷兰文: Provincienaam; 中文: 省名称" align="center" prop="provinceName" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['infrastructure:municipalityReference:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['infrastructure:municipalityReference:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改MunicipalityReferenceTable对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="municipalityReferenceRef" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MunicipalityReference">
import { listMunicipalityReference, getMunicipalityReference, delMunicipalityReference, addMunicipalityReference, updateMunicipalityReference } from "@/api/infrastructure/municipalityReference"

const { proxy } = getCurrentInstance()

const municipalityReferenceList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    municipalityCode: null,
    municipalityCodeGm: null,
    municipalityName: null,
    provinceCode: null,
    provinceCodePv: null,
    provinceName: null
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询MunicipalityReferenceTable列表 */
function getList() {
  loading.value = true
  listMunicipalityReference(queryParams.value).then(response => {
    municipalityReferenceList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    municipalityCode: null,
    municipalityCodeGm: null,
    municipalityName: null,
    provinceCode: null,
    provinceCodePv: null,
    provinceName: null
  }
  proxy.resetForm("municipalityReferenceRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.municipalityCode)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加MunicipalityReferenceTable"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _municipalityCode = row.municipalityCode || ids.value
  getMunicipalityReference(_municipalityCode).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改MunicipalityReferenceTable"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["municipalityReferenceRef"].validate(valid => {
    if (valid) {
      if (form.value.municipalityCode != null) {
        updateMunicipalityReference(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addMunicipalityReference(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _municipalityCodes = row.municipalityCode || ids.value
  proxy.$modal.confirm('是否确认删除MunicipalityReferenceTable编号为"' + _municipalityCodes + '"的数据项？').then(function() {
    return delMunicipalityReference(_municipalityCodes)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('infrastructure/municipalityReference/export', {
    ...queryParams.value
  }, `municipalityReference_${new Date().getTime()}.xlsx`)
}

getList()
</script>
