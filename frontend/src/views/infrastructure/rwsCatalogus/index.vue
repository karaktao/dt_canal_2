<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="Loc.Code" prop="locCode">
        <el-input
          v-model="queryParams.locCode"
          placeholder="请输入Loc.Code"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Loc.Naam" prop="locNaam">
        <el-input
          v-model="queryParams.locNaam"
          placeholder="请输入Loc.Naam"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Loc.X (坐标)" prop="locX">
        <el-input
          v-model="queryParams.locX"
          placeholder="请输入Loc.X (坐标)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Loc.Y (坐标)" prop="locY">
        <el-input
          v-model="queryParams.locY"
          placeholder="请输入Loc.Y (坐标)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Parameter_Wat_Omschrijving" prop="metaParameterWatOmschrijving">
        <el-input
          v-model="queryParams.metaParameterWatOmschrijving"
          placeholder="请输入Meta.Parameter_Wat_Omschrijving"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Compartiment.Code" prop="metaCompartimentCode">
        <el-input
          v-model="queryParams.metaCompartimentCode"
          placeholder="请输入Meta.Compartiment.Code"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Compartiment.Oms" prop="metaCompartimentOms">
        <el-input
          v-model="queryParams.metaCompartimentOms"
          placeholder="请输入Meta.Compartiment.Oms"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Grootheid.Code" prop="metaGrootheidCode">
        <el-input
          v-model="queryParams.metaGrootheidCode"
          placeholder="请输入Meta.Grootheid.Code"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Grootheid.Oms" prop="metaGrootheidOms">
        <el-input
          v-model="queryParams.metaGrootheidOms"
          placeholder="请输入Meta.Grootheid.Oms"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Parameter.Code" prop="metaParameterCode">
        <el-input
          v-model="queryParams.metaParameterCode"
          placeholder="请输入Meta.Parameter.Code"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Parameter.Oms" prop="metaParameterOms">
        <el-input
          v-model="queryParams.metaParameterOms"
          placeholder="请输入Meta.Parameter.Oms"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Eenheid" prop="metaEenheid">
        <el-input
          v-model="queryParams.metaEenheid"
          placeholder="请输入Meta.Eenheid"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Hoedanigheid" prop="metaHoedanigheid">
        <el-input
          v-model="queryParams.metaHoedanigheid"
          placeholder="请输入Meta.Hoedanigheid"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Typering" prop="metaTypering">
        <el-input
          v-model="queryParams.metaTypering"
          placeholder="请输入Meta.Typering"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.WaardeBewerkingsmethode" prop="metaWaardeBewerkingsmethode">
        <el-input
          v-model="queryParams.metaWaardeBewerkingsmethode"
          placeholder="请输入Meta.WaardeBewerkingsmethode"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.BioTaxon" prop="metaBiotaxon">
        <el-input
          v-model="queryParams.metaBiotaxon"
          placeholder="请输入Meta.BioTaxon"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.BioTaxon_Compartiment" prop="metaBiotaxonCompartiment">
        <el-input
          v-model="queryParams.metaBiotaxonCompartiment"
          placeholder="请输入Meta.BioTaxon_Compartiment"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Meta.Orgaan" prop="metaOrgaan">
        <el-input
          v-model="queryParams.metaOrgaan"
          placeholder="请输入Meta.Orgaan"
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
          v-hasPermi="['infrastructure:rwsCatalogus:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['infrastructure:rwsCatalogus:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['infrastructure:rwsCatalogus:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['infrastructure:rwsCatalogus:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="rwsCatalogusList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="自增主键" align="center" prop="id" />
      <el-table-column label="Loc.Code" align="center" prop="locCode" />
      <el-table-column label="Loc.Naam" align="center" prop="locNaam" />
      <el-table-column label="Loc.EPSG (EPSG 编号)" align="center" prop="locEpsg" />
      <el-table-column label="Loc.X (坐标)" align="center" prop="locX" />
      <el-table-column label="Loc.Y (坐标)" align="center" prop="locY" />
      <el-table-column label="Meta.Parameter_Wat_Omschrijving" align="center" prop="metaParameterWatOmschrijving" />
      <el-table-column label="Meta.Compartiment.Code" align="center" prop="metaCompartimentCode" />
      <el-table-column label="Meta.Compartiment.Oms" align="center" prop="metaCompartimentOms" />
      <el-table-column label="Meta.Grootheid.Code" align="center" prop="metaGrootheidCode" />
      <el-table-column label="Meta.Grootheid.Oms" align="center" prop="metaGrootheidOms" />
      <el-table-column label="Meta.Parameter.Code" align="center" prop="metaParameterCode" />
      <el-table-column label="Meta.Parameter.Oms" align="center" prop="metaParameterOms" />
      <el-table-column label="Meta.Eenheid" align="center" prop="metaEenheid" />
      <el-table-column label="Meta.Hoedanigheid" align="center" prop="metaHoedanigheid" />
      <el-table-column label="Meta.Typering" align="center" prop="metaTypering" />
      <el-table-column label="Meta.WaardeBewerkingsmethode" align="center" prop="metaWaardeBewerkingsmethode" />
      <el-table-column label="Meta.BioTaxon" align="center" prop="metaBiotaxon" />
      <el-table-column label="Meta.BioTaxon_Compartiment" align="center" prop="metaBiotaxonCompartiment" />
      <el-table-column label="Meta.Orgaan" align="center" prop="metaOrgaan" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['infrastructure:rwsCatalogus:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['infrastructure:rwsCatalogus:remove']">删除</el-button>
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

    <!-- 添加或修改rwsCatalogus对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="rwsCatalogusRef" :model="form" :rules="rules" label-width="80px">
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

<script setup name="RwsCatalogus">
import { listRwsCatalogus, getRwsCatalogus, delRwsCatalogus, addRwsCatalogus, updateRwsCatalogus } from "@/api/infrastructure/rwsCatalogus"

const { proxy } = getCurrentInstance()

const rwsCatalogusList = ref([])
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
    locCode: null,
    locNaam: null,
    locEpsg: null,
    locX: null,
    locY: null,
    metaParameterWatOmschrijving: null,
    metaCompartimentCode: null,
    metaCompartimentOms: null,
    metaGrootheidCode: null,
    metaGrootheidOms: null,
    metaParameterCode: null,
    metaParameterOms: null,
    metaEenheid: null,
    metaHoedanigheid: null,
    metaTypering: null,
    metaWaardeBewerkingsmethode: null,
    metaBiotaxon: null,
    metaBiotaxonCompartiment: null,
    metaOrgaan: null
  },
  rules: {
    id: [
      { required: true, message: "自增主键不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询rwsCatalogus列表 */
function getList() {
  loading.value = true
  listRwsCatalogus(queryParams.value).then(response => {
    rwsCatalogusList.value = response.rows
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
    id: null,
    locCode: null,
    locNaam: null,
    locEpsg: null,
    locX: null,
    locY: null,
    metaParameterWatOmschrijving: null,
    metaCompartimentCode: null,
    metaCompartimentOms: null,
    metaGrootheidCode: null,
    metaGrootheidOms: null,
    metaParameterCode: null,
    metaParameterOms: null,
    metaEenheid: null,
    metaHoedanigheid: null,
    metaTypering: null,
    metaWaardeBewerkingsmethode: null,
    metaBiotaxon: null,
    metaBiotaxonCompartiment: null,
    metaOrgaan: null
  }
  proxy.resetForm("rwsCatalogusRef")
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
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加rwsCatalogus"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getRwsCatalogus(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改rwsCatalogus"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["rwsCatalogusRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateRwsCatalogus(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRwsCatalogus(form.value).then(response => {
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
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除rwsCatalogus编号为"' + _ids + '"的数据项？').then(function() {
    return delRwsCatalogus(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('infrastructure/rwsCatalogus/export', {
    ...queryParams.value
  }, `rwsCatalogus_${new Date().getTime()}.xlsx`)
}

getList()
</script>
